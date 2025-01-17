package jhorlamide;

import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ccwc", mixinStandardHelpOptions = true, version = "ccwc 1.0",
        description = "simple wc tool to counts the number for lines, words, characters from a file")
public class Main implements Callable<Result> {
    @CommandLine.Parameters(index = "0", description = "The file to calculate for.", defaultValue = "./test.txt")
    private File file;

    @CommandLine.Option(names = {"-c"}, description = "-c for counting character")
    private boolean switchCharacters;

    @CommandLine.Option(names = {"-l"}, description = "-l for count lines")
    private boolean switchLine;

    @CommandLine.Option(names = {"-w"}, description = "-w for count words")
    private boolean switchWords;

    public static void main(String[] args) {
        var command = new CommandLine(new Main());
        command.execute(args);
        var result = command.getExecutionResult();
        if (result != null) {
            System.out.println(result);
        }
    }

    @Override
    public Result call() throws Exception {
        var result = new Result();

        if (!this.file.exists()) {
            throw new FileNotFoundException("File "+this.file.getAbsolutePath()+" not found");
        }
        result.fileName = this.file.getName();
        byte[] fileContent = Files.readAllBytes(Path.of(this.file.toURI()));
        boolean switchAll = (this.switchCharacters == this.switchLine) && (this.switchLine == this.switchWords);

        if (switchAll) {
            this.switchLine = true;
            this.switchWords = true;
            this.switchCharacters = true;
        }

        if (this.switchCharacters) {
            result.charCount = getCharsCount(fileContent);
        }

        if (this.switchLine) {
            result.lineCount = getLineCount(fileContent);
        }

        if (this.switchWords) {
            result.wordCount = getWordCount(fileContent);
        }

        return result;
    }

    private int getCharsCount(byte[] fileBytes) {
        return fileBytes.length;
    }

    private int getLineCount(byte[] fileBytes) {
        var i = 0;

        for (byte fileByte : fileBytes) {
            if (fileByte == '\n') {
                i++;
            }
        }

        return i;
    }

    private int getWordCount(byte[] fileBytes) {
        var i = 0;
        var lastWordCount = 0;

        for (byte fileByte : fileBytes) {
            if (Character.isWhitespace(fileByte)) {
                if (lastWordCount > 0) {
                    i++;
                    lastWordCount = 0;
                }
            } else {
                lastWordCount++;
            }

        }
        if (lastWordCount > 0) {
            i++;
        }
        return i;
    }
}
