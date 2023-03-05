import java.io.*;
import java.util.*;

// Futtatáskor az első argumentum a grammatikát tartalmazó file neve, a második argumentum az elemzendő szó.
//A grammatika file formátuma olyan, mint a mellékelt grammar.txt fileé: az első sor a kezdőszimbólum, a második a
// terminálisok, a harmadik a nemterminálisok halmaza
// Ezután egy-egy sorban a sor elején álló nemterminálisra vonatkozó szabály-jobboldalak szóközzel elválasztva

public class TopDownGeneral{

    public static String word;
    public static String startingSymbol;
    public static ArrayList<String> terminals = new ArrayList<String>();
    public static ArrayList<String> nonTerminals = new ArrayList<String>();
    public static TreeMap<String,ArrayList<String>> grammar = new TreeMap<>();

    public static Map<String, List<String>> alternatives = new HashMap<>();
    public static Map<String, List<String>> alternativeResults = new HashMap<>();

    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("Usage: java TopDownGeneral <File> <Word>.");
            System.exit(1);
        } else {
            readInput(args);
            parse();
        }
    }

    public static void readInput(String[] args){
        parseGrammar(args);
        System.out.println("Word: " + word);
        System.out.println("\nG = (" + terminals.toString().replace("[", "{").replace("]", "}")
                + ", " + nonTerminals.toString().replace("[", "{").replace("]", "}")
                + ", P, " + startingSymbol + ")\n\nWith Productions P as:");
        for(String s: grammar.keySet()){
            System.out.println(s + " -> " + grammar.get(s));
        }
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

    public static void parse() {
        System.out.println("\nA generált alternatívák:");
        makeAlternatives(grammar);

        int hossz = word.length();

        char allapot = 'q';
        int pozicio = 0;
        String jel;  // az elemzett szo aktuális poziciojan talalhato szimbolum


        Stack<Integer> alternativIndexek = new Stack<Integer>();

        String jobbTetejuFelso = "";
        Stack<String> jobbTetejuVerem = new Stack<>();
        String balTetejuFelso = "";
        Stack<String> balTetejuVerem = new Stack<>();
        balTetejuVerem.push(startingSymbol);


        System.out.println("\nGenerálási folyamat:");
        while (allapot != 't') {
            printState(allapot, pozicio, jobbTetejuVerem, balTetejuVerem);

            if (!balTetejuVerem.isEmpty()) {
                balTetejuFelso = balTetejuVerem.pop();
            } else {
                balTetejuFelso="$";
            }

            if (!jobbTetejuVerem.isEmpty()) {
                jobbTetejuFelso = jobbTetejuVerem.peek();
            }
            jel = String.valueOf(word.charAt(pozicio));


            // 3. sikeres elemzes
            if ((pozicio == word.length()-1) && (balTetejuVerem.isEmpty()) && (balTetejuFelso.equals("$"))){
                allapot='t';
                System.out.println("A "+word+" szo generalhato");
                continue;
            }

            // 1. kiterjesztes
            if ((allapot == 'q') && ( nonTerminals.contains(balTetejuFelso) )){
                alternativIndexek.push(0);
                jobbTetejuVerem.push(getAlternativeSymbol(balTetejuFelso, alternativIndexek.peek()));

                List<String> symbolWordAsList = alternativeResults.get(jobbTetejuVerem.peek());
                for (String s : symbolWordAsList) {
                    balTetejuVerem.push(s);
                }
                continue;
            }

            // input illesztés
            if ((allapot == 'q') && ((terminals.contains(balTetejuFelso) || balTetejuFelso.equals("$")))) {

                // 2. input illesztés sikeres
                if (balTetejuFelso.equals(jel)) {
                    pozicio += 1;
                    jobbTetejuVerem.push(balTetejuFelso);
                    continue;
                }
                // 4. input illesztés sikertelen
                else {
                    allapot = 'b';
                    balTetejuVerem.push(balTetejuFelso);
                    continue;
                }
            }

            // 5. backtrack az inputban
            if ((allapot == 'b') && terminals.contains(jobbTetejuFelso)) {
                if (!balTetejuFelso.equals("$"))
                    balTetejuVerem.push(balTetejuFelso);

                pozicio -= 1;
                balTetejuVerem.push(jobbTetejuVerem.pop());
                continue;
            }

            // 6. backtrack a kiterjesztésben
            String originalNonTerminal = String.valueOf(jobbTetejuFelso.charAt(0));
            if ((allapot == 'b') && nonTerminals.contains(originalNonTerminal)) {

                // I. eset
                if (alternativIndexek.peek() < alternatives.get(originalNonTerminal).size() - 1) {
                    allapot = 'q';
                    String lastAlternativ = jobbTetejuVerem.pop();
                    if (alternativeResults.get(lastAlternativ).size() > 1) {
                        for (int i = 1; i < alternativeResults.get(lastAlternativ).size(); i++) {
                            balTetejuVerem.pop();
                        }
                    }
                    alternativIndexek.push(alternativIndexek.pop() + 1);

                    jobbTetejuVerem.push(getAlternativeSymbol(originalNonTerminal, alternativIndexek.peek()));

                    List<String> symbolWordAsList = alternativeResults.get(jobbTetejuVerem.peek());
                    for (String s : symbolWordAsList) {
                        balTetejuVerem.push(s);
                    }
                    continue;
                }

                // II. eset
                if (pozicio == 0 && jobbTetejuFelso.equals(alternatives.get(startingSymbol).get(alternatives.get(startingSymbol).size()-1))) {
                    System.out.println("A "+word+" szo NEM generalhato");
                    break;
                }

                // III. eset
                String lastAlternativ = jobbTetejuVerem.pop();
                if (alternativeResults.get(lastAlternativ).size() > 1) {
                    for (int i = 1; i < alternativeResults.get(lastAlternativ).size(); i++) {
                        balTetejuVerem.pop();
                    }
                }
                balTetejuVerem.push(originalNonTerminal);
                alternativIndexek.pop();
                continue;
            }
        }
        printState(allapot, pozicio, jobbTetejuVerem, balTetejuVerem);
    }


    private static String getAlternativeSymbol(String balTetejuFelso, int alternativaIndex) {
        return alternatives.get(balTetejuFelso).get(alternativaIndex);
    }

    private static void printState(char allapot, int pozicio, Stack<String> jobbTetejuVerem, Stack<String> balTetejuVerem) {
        List<String> balTetejuJoOrientacio = new ArrayList();
        for (int i = balTetejuVerem.size() - 1; i >= 0; i--) {
            balTetejuJoOrientacio.add(balTetejuVerem.get(i));
        }

        System.out.printf("(%s, %s, %s, %s)\n", allapot, pozicio, jobbTetejuVerem, balTetejuJoOrientacio);
    }

    private static void makeAlternatives(TreeMap<String, ArrayList<String>> grammar) {
        for (String key :
                grammar.keySet()) {
            List<String> currentAlternatives = new ArrayList<>();
            for (int i = 0; i < grammar.get(key).size(); i++) {

                String altKey = String.format("%s%s", key, i);

                currentAlternatives.add(altKey);


                String symbolWord = grammar.get(key).get(i);
                List<String> symbolWordList = new ArrayList<>();
                for (int j = symbolWord.length() - 1; j >= 0; j--) {
                    symbolWordList.add(String.valueOf(symbolWord.charAt(j)));
                }
                alternativeResults.put(altKey, symbolWordList);
            }
            alternatives.put(key, currentAlternatives);
        }

        System.out.println(alternatives);
        System.out.println(alternativeResults);
    }
}

