import java.io.*;
import java.util.*;

// Futtatáskor az első argumentum a grammatikát tartalmazó file neve, a második argumentum az elemzendő szó.
//A grammatika file formátuma olyan, mint a mellékelt grammar.txt fileé: az első sor a kezdőszimbólum, a második a
// terminálisok, a harmadik a nemterminálisok halmaza
// Ezután egy-egy sorban a sor elején álló nemterminálisra vonatkozó szabály-jobboldalak szóközzel elválasztva

public class CYK{
    public static String word;
    public static String startingSymbol;
    public static ArrayList<String> terminals = new ArrayList<String>();
    public static ArrayList<String> nonTerminals = new ArrayList<String>();
    public static TreeMap<String,ArrayList<String>> grammar = new TreeMap<>();

    public static ArrayList<ArrayList<ArrayList<String>>> cykPyramid;

    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("Usage: java CYK <File> <Word>.");
            System.exit(1);
        } else
            doSteps(args);
    }

    public static void doSteps(String[] args){
        parseGrammar(args);
        System.out.println("Word: " + word);
        System.out.println("\nG = (" + terminals.toString().replace("[", "{").replace("]", "}")
                + ", " + nonTerminals.toString().replace("[", "{").replace("]", "}")
                + ", P, " + startingSymbol + ")\n\nWith Productions P as:");
        for(String s: grammar.keySet()){
            System.out.println(s + " -> " + grammar.get(s));
        }
        doCyk();
    }

    public static Scanner openFile(String file){
        try{
            return new Scanner(new File(file));
        }catch(FileNotFoundException e){
            System.out.println("Error: Can't find or open the file: " + file + ".");
            System.exit(1);
            return null;
        }
    }

    public static void parseGrammar(String[] args) {
        Scanner input = openFile(args[0]);
        ArrayList<String> tmp = new ArrayList<>();
        int line = 2;

        word = args[1];
        startingSymbol = input.next();
        input.nextLine();

        while(input.hasNextLine() && line <= 3){
            tmp.addAll(Arrays.<String>asList(toArray(input.nextLine())));
            if(line == 2) { terminals.addAll(tmp); }
            if(line == 3) { nonTerminals.addAll(tmp); }
            tmp.clear();
            line++;
        }

        while(input.hasNextLine()){
            tmp.addAll(Arrays.<String>asList(toArray(input.nextLine())));
            String leftSide = tmp.get(0);
            tmp.remove(0);
            grammar.put(leftSide, new ArrayList<String>());
            grammar.get(leftSide).addAll(tmp);
            tmp.clear();
        }
        input.close();
    }


    public static String[] toArray(String input){
        return input.split("\\s");
    }



    @SuppressWarnings("ReassignedVariable")
    public static void doCyk() {
        generateCykPyramid(word.length());
        
        // Fill the first layer wit non-terminals
        for (int charIndex = 0; charIndex < word.length(); charIndex++) {
            String terminal = String.valueOf(word.charAt(charIndex));
            ArrayList<String> foundNonTerminals = findGeneratingNonTerminals(terminal);
            cykPyramid.get(0).get(charIndex).addAll(foundNonTerminals);
        }

        System.out.println("\nStarting CYK pyramid:");
        printCykPyramid(cykPyramid);
        
        // Fill out the upper levels as well
        for (int rowIndex = 1; rowIndex < cykPyramid.size(); rowIndex++) {
            for (int cellIndex = 0; cellIndex < cykPyramid.get(rowIndex).size(); cellIndex++) {
                ArrayList<String> cell = cykPyramid.get(rowIndex).get(cellIndex);

                // on the n-th level we only have to check n-1 pairs
                for (int checkIndex = 1; checkIndex <= rowIndex; checkIndex++) {
                    var leftNonTerminals = cykPyramid.get(-1+checkIndex).get(cellIndex);
                    var rightNonTerminals = cykPyramid.get(rowIndex-checkIndex).get(cellIndex+checkIndex);

                    ArrayList<String> possibleNonTerminalPairs = generateEveryPossibleNonTerminalPair(leftNonTerminals, rightNonTerminals);
                    for (String nonTerminalPair :
                            possibleNonTerminalPairs) {
                        cell.addAll(findGeneratingNonTerminals(nonTerminalPair));
                    }
                }
            }
        }
        System.out.println("\nFinal CYK pyramid:");
        printCykPyramid(cykPyramid);

        if (cykPyramid.get(cykPyramid.size()-1).get(0).contains(startingSymbol))
            System.out.println("\nThe word is derivable!");
        else
            System.out.println("\nThe word is not derivable!");
    }

    private static ArrayList<String> generateEveryPossibleNonTerminalPair(ArrayList<String> leftNonTerminals, ArrayList<String> rightNonTerminals) {
        ArrayList<String> resultList = new ArrayList<>();

        for (String leftNonTerminal :
                leftNonTerminals) {
            for (String rightNonTerminal :
                    rightNonTerminals) {
                resultList.add(String.format("%s%s", leftNonTerminal, rightNonTerminal));
            }
        }
        return resultList;
    }

    private static ArrayList<String> findGeneratingNonTerminals(String result) {
        var foundNonTerminals = new ArrayList<String>();
        for (var nonTerminalKey :
                grammar.keySet()) {

            if(grammar.get(nonTerminalKey).contains(result))
                foundNonTerminals.add(nonTerminalKey);
        }
        return foundNonTerminals;
    }

    public static void generateCykPyramid(int length) {
        cykPyramid = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            ArrayList<ArrayList<String>> newRow = new ArrayList<>();
            for (int j = 0; j < length-i; j++) {
                newRow.add(new ArrayList<>());
            }
            cykPyramid.add(newRow);
        }
    }

    public static void printCykPyramid(ArrayList<ArrayList<ArrayList<String>>> cykPyramid) {
        for (int rowIndex = cykPyramid.size()-1; rowIndex >= 0 ; rowIndex--) {
            for (int i = 0; i < (rowIndex); i++) {
                String spaces = "";
                for (int j = 0; j < word.length()/2; j++) {
                    spaces = spaces.concat(" ");
                }
                System.out.print(spaces);
            }
            for (var cell :
                    cykPyramid.get(rowIndex)) {
                String spaces = "";
                for (int i = 0; i < word.length() - cell.size(); i++) {
                    spaces = spaces.concat(" ");
                }
                System.out.printf("%s%s", cell, spaces);
            }
            System.out.print("\n");
        }
    }
}

