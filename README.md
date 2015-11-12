# Akka File Reducer
### Problem formulation
The input file contains lines like "ID;amount". 
There are 100000 lines and 1000 unique ID's in the input file.

For instance
> ID104242;30.00

> AA1818BI;20.00

> ID104242;39.99

> IDBBBBBB;25.00
 
> ID104242;30.01

> AA1818BI;30.00


Output file should contain total amount for each ID. For the previous example the expected output is
> AA1818BI;50.00

> ID104242;100.00

> IDBBBBBB;25.00


### Solution architecture
![diagram](http://i.imgur.com/3wMKcz3.png)

Here we have
- 3 actor types, namely *FileProcessor*, *LineProcessor*, and *Terminator* 
- 4 message types: *ProcessFile*, *ProcessLine*, *Finish*, and *LinesProcessed*

The main top-level actor is **FileProcessor**. It runs on a dedicated thread, which is provided 
by *PinnedDispatcher*. A typical scenario looks like that. The **FileProcessor** receives a **ProcessFile**
message with input and output file paths. Then it starts reading input file line by line.
It wraps each line in **ProcessLine** message and sends it to **LineProcessor**s through a router.
Currently the **linesRouter** is a *RoundRobinPool* with 10 routees, but it can be easily changed in configuration.
The **LineProcessor** maintains a counter of received lines and a hash map of ID's and corresponding total amounts.
Once the next **ProcessLine** message is received, it increments the counter and merges the value into the map.
After all lines were read from input, the **FileProcessor** sends a **Finish** message (wrapped into *Broadcast*) 
to the router. Each **LineProcessor** responds with a **LinesProcessed** message, containing total number of 
processed lines and the map of ID's and total amounts. The **FileProcessor** copies the first received map 
and merges next received maps into it, until total number of processed lines becomes equal to the number of lines
sent. Finally, it sends a *PoisonPill* to the **linesRouter**, writes the map to the output file,
and then terminates. There is another top level actor, **Terminator**, that death-watches the **FileProcessor**
and terminates the actor system in response to **FileProcessors** termination.

### Supervision strategy
One of significant Akka features is fault tolerance, which can be implemented using the concept 
of supervisors and subordinates. But in our small system any failure means that the result of computation 
will be incorrect, therefore the system shouldn't recover from it but rather tell the external caller 
about the error. That's why the **FileProcessor** actor escalates any Exception raised by it's child 
to the guardian */user*. In it's turn, the guardian */user* logs the error message and terminates the failed 
top level actor. Finally, the **Terminator** actor, that is set to deathwatch our **FileProcessor**, receives 
a message about it's termination and terminates the actor system.

### User specific settings
In case total number of unique IDs is known, it is useful to set the proper initial capacity and load factor
of the hash map in order to minimize the number of rehash operations. In our system this parameters are set in
configuration file, like that:
```
  file-reducer.hash-map {
    capacity = 1536
    load-factor = 0.66
  }
```
Later on this values are used by the **LineProcessor** in this manner
```java
@Override
    public void preStart() throws Exception {
        final SettingsImpl settings =
                SettingsProvider.get(getContext().system());

        map = new HashMap<>(settings.HASH_MAP_CAPACITY,
                            settings.HASH_MAP_LOAD_FACTOR);
    }
```
This is possible thanks for Akka's mechanism of **Extension**s and **ExtensionProvider**s.

### TODOs
This system is a simple example of Akka utilization and the actor model it uses is pretty simple.
Anyway, there are still things to think of. One of them is message reliability. Akka uses at-most-once delivery,
i.e. some messages may be lost. Let such situation happen, and our app will hang forever. A real application should

1. motitor it's state
2. employ acknowledgement mechanism (something like the one that TCP uses)

Another option is to use *event sourcing* approach to achieve at-least-once message delivery, which can be 
implemented with the help of Akka Persistence.
