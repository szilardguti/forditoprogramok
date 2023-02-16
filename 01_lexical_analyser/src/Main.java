import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("ReassignedVariable")
public class Main {
    public static void main(String[] args)
            throws IOException {
        LogicTable table = new LogicTable(false);

        Path fileName
                = Path.of("src/proba.txt");

        String sourceString = Files.readString(fileName);

        int state = 1;
        int charPosition = 0;
        char sign;

        do {
            TableRow currentRow = table.getRow(state);

            sign = sourceString.charAt(charPosition);

            if (currentRow.readInput && charPosition != sourceString.length()-1)
                charPosition++;
            else if (currentRow.backupChar && charPosition != 0)
                charPosition--;

            state = currentRow.evaluate(sign);

        } while (state != 21);

        AsciiTableGenerator tableGenerator = new AsciiTableGenerator();
        tableGenerator.generateAsciiTable();
    }
}


