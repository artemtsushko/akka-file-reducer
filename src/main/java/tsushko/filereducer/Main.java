package tsushko.filereducer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import tsushko.filereducer.actors.FileProcessor;
import tsushko.filereducer.actors.Terminator;
import tsushko.filereducer.messages.ProcessFile;

/**
 * Takes input and output file paths as command line arguments
 * and reduces the input file of lines like "ID;amount"
 * to the output file of lines like "ID;totalAmount"
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("fileReducer");
        ActorRef fileProcessor = system.actorOf(
                Props.create(FileProcessor.class),
                "master");
        ActorRef terminator = system.actorOf(
                Props.create(Terminator.class, fileProcessor),
                "terminator");
        fileProcessor.tell(new ProcessFile(args[0], args[1]), terminator);
    }
}
