package tsushko.filereducer.actors;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

/**
 * Watches the ActorRef passed to constructor and terminates the actor system
 * in case of this actor's termination
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class Terminator extends UntypedActor {

    private final ActorRef ref;

    public Terminator(ActorRef ref) {
        this.ref = ref;
        getContext().watch(ref);
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof Terminated) {
            getContext().system().terminate();
        } else {
            unhandled(msg);
        }
    }

}
