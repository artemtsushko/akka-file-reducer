package messages;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;


/**
 * A message from worker to master, contains total number of
 * processed lines and a map(ID, totalAmount)
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class LinesProcessed implements Serializable{

    private static final long serialVersionUID = 1L;

    private final int linesProcessed;

    private final Map<String,BigDecimal> map;

    public LinesProcessed(int linesProcessed, Map<String,BigDecimal> map) {
        this.linesProcessed = linesProcessed;
        this.map = Collections.unmodifiableMap(map);
        /*
            TODO: consider whether to copy the passed map
            The last line should be like this
                this.map = Collections.unmodifiableMap(
                    new HashMap<String,BigDecimal>(map));
            so that the message is completely immutable.
            But it will break encapsulation and diminish performance.
            Currently there is no need to do so.
        */
    }

    public int getLinesNumber() {
        return linesProcessed;
    }

    public Map<String,BigDecimal> getMap() {
        return map;
    }
}
