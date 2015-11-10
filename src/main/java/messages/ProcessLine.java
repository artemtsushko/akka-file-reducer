package messages;

import java.io.Serializable;

/**
 * A message from master to worker, that initializes processing a line
 * from the input document. Wraps a String in format <code>ID;amount</code>
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class ProcessLine implements Serializable{

    private static final long serialVersionUID = 1L;

    private final String line;

    public ProcessLine(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }
}
