package tsushko.filereducer.actors;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.Broadcast;
import akka.routing.FromConfig;
import tsushko.filereducer.messages.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * The master actor that, once received
 * {@link tsushko.filereducer.messages.ProcessFile} message,
 * reduces the input file containing strings "ID;amount"
 * to output file containing string "ID;totalAmount"
 * and then terminates the entire <code>ActorSystem</code>
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class FileProcessor extends UntypedActor {

    private BufferedReader reader;
    private PrintWriter writer;
    private int linesSent = 0;
    private Integer linesProcessed;
    private ActorRef linesRouter;
    private Map<String,BigDecimal> map;

    @Override
    public void preStart() throws Exception {
        linesRouter = getContext().actorOf(
                FromConfig.getInstance().props(Props.create(LineProcessor.class)),
                "linesRouter");
    }


    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ProcessFile) {
            ProcessFile msg = (ProcessFile) message;
            reader = new BufferedReader(new FileReader(msg.getInputFilePath()));
            writer = new PrintWriter(msg.getOutputFilePath());
            String line;
            while ((line = reader.readLine()) != null) {
                linesRouter.tell(new ProcessLine(line), getSelf());
                ++linesSent;
            }
            reader.close();
            linesRouter.tell(new Broadcast(Finish.getInstance()), getSelf());
        } else if (message instanceof LinesProcessed) {
            LinesProcessed msg = (LinesProcessed) message;
            if (linesProcessed == null) {
                map = new HashMap<>(msg.getMap());
                linesProcessed = msg.getLinesNumber();
            } else {
                msg.getMap().forEach((k,v)->map.merge(k,v,BigDecimal::add));
                linesProcessed += msg.getLinesNumber();
            }
            if (linesProcessed == linesSent) {
                linesRouter.tell(PoisonPill.getInstance(), getSelf());
                map.forEach((k,v)->writer.println(k+";"+v));
                writer.close();
                getContext().system().terminate();
            }
        } else {
            unhandled(message);
        }
    }

}

