package messages;

import java.io.Serializable;

/**
 * A message to master, that initializes processing the input file.
 * Contains input and output file paths.
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class ProcessFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String inputFilePath;
    private final String outputFilePath;

    public ProcessFile(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }
}
