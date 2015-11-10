package tsushko.filereducer.messages;

import java.io.Serializable;

/**
 * Empty message from master to workers indicating that it's time
 * for workers to reply with {@link LinesProcessed} message.
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class Finish implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Finish instance = new Finish();

    private Finish() {}

    public static Finish getInstance() {
        return instance;
    }

}
