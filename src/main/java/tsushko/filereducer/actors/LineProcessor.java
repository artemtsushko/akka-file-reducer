package tsushko.filereducer.actors;

import akka.actor.UntypedActor;
import tsushko.filereducer.messages.Finish;
import tsushko.filereducer.messages.LinesProcessed;
import tsushko.filereducer.messages.ProcessLine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A worker actor, that maintains a map(ID;totalAmount),
 * receives messages containing lines like "ID;amount",
 * parses them and inserts into the map, incrementing
 * the "totalAmount" value by "amount" in case of collision,
 * and finally sends total number of processed lines and the map
 * back to the master actor.
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class LineProcessor extends UntypedActor{

    private int linesProcessed = 0;
    private Pattern separator = Pattern.compile(";");
    private Map<String,BigDecimal> map;

    @Override
    public void preStart() throws Exception {
        map = new HashMap<>();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ProcessLine) {
            ProcessLine msg = (ProcessLine) message;
            String[] parts = separator.split(msg.getLine());
            String key = parts[0];
            BigDecimal amount = new BigDecimal(parts[1]);
            map.merge(key,amount,BigDecimal::add);
            ++linesProcessed;
        } else if (message instanceof Finish) {
            LinesProcessed msg = new LinesProcessed(linesProcessed,map);
            getSender().tell(msg, getSelf());
        } else {
            unhandled(message);
        }
    }
}
