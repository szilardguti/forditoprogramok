import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AsciiTableGenerator {
    public final String asciiCharacters = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    
    public void generateAsciiTable() throws IOException {
        LogicTable table = new LogicTable(true);
        List<String> resultRows = new ArrayList<>();

        resultRows.add(generateHeader());

        for (int i = 0; i < asciiCharacters.length(); i++) {
            char currentChar = asciiCharacters.charAt(i);

            resultRows.add(generateRow(currentChar, table));
        }

        FileWriter fileWriter = new FileWriter("src/output.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (String row :
                resultRows) {
            printWriter.println(row);
        }
        printWriter.close();
    }

    private String generateRow(char currentChar, LogicTable table) {
        StringBuilder rowBuilder = new StringBuilder();
        rowBuilder.append(String.format("<%c>", currentChar));

        for (int i = 1; i < 21; i++) {
            TableRow row = table.getRow(i);
            rowBuilder.append(';').append(row.evaluate(currentChar));
        }

        return rowBuilder.toString();
    }

    public String generateHeader() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < 21; i++)
            stringBuilder.append(';').append('<').append(Integer.toString(i)).append('>');

        return stringBuilder.toString();
    }
}
