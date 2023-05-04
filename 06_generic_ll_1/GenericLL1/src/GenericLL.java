import java.io.*;
import java.nio.file.Path;
import java.util.*;


// Futtatáskor az első argumentum a grammatikát tartalmazó file neve, a második argumentum az elemzendő szó.
//A grammatika file formátuma olyan, mint a mellékelt grammar.txt fileé: az első sor a kezdőszimbólum, a második a
// terminálisok, a harmadik a nemterminálisok halmaza
// Ezután egy-egy sorban a sor elején álló nemterminálisra vonatkozó szabály-jobboldalak szóközzel elválasztva


public class GenericLL {

        public static class SzamozottJobboldal {
            private String l;
            private Integer r;

            public SzamozottJobboldal(String l, Integer r) {
                this.l = l;
                this.r = r;
            }

            public String getL() {
                return l;
            }

            public Integer getR() {
                return r;
            }

            public void setL(String l) {
                this.l = l;
            }

            public void setR(Integer r) {
                this.r = r;
            }

            @Override
            public java.lang.String toString() {
                return "(" + l +", " + r +")";
            }
        }



    public static String word;
    public static String startingSymbol;

    public static String deleteSymbol = "&";
    public static ArrayList<String> terminals = new ArrayList<String>();
    public static ArrayList<String> nonTerminals = new ArrayList<String>();
    public static TreeMap<String,ArrayList<String>> grammar = new TreeMap<>();
    public static TreeMap<String,ArrayList<SzamozottJobboldal>> szamozottGrammar = new TreeMap<>();

    public static TreeMap<String,TreeMap<String,SzamozottJobboldal>> llTablazat =new TreeMap<>();

    public static TreeMap<Integer,TreeMap<String,HashSet<String>>> hTablazat = new TreeMap<>();

    public static TreeMap<String, ArrayList<HashSet<String>>> first1_map = new TreeMap<>();


    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("Usage: java LL1egyszeruTablazattal <File> <Word>.");
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
        for(String bal: grammar.keySet()){
            System.out.println(bal + " -> " + grammar.get(bal));
        }
        for (String bal : szamozottGrammar.keySet()) {
            System.out.println(" -------- ");
            for (SzamozottJobboldal jobb : szamozottGrammar.get(bal)) {
                System.out.print(bal+" --> ");
                System.out.println(jobb.getL()+" "+jobb.getR());
            }
        }
        System.out.println("+--------+");

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

        while (input.hasNextLine() && line <= 3) {
            tmp.addAll(Arrays.<String>asList(toArray(input.nextLine())));
            if (line == 2) {
                terminals.addAll(tmp);
            }
            if (line == 3) {
                nonTerminals.addAll(tmp);
            }
            tmp.clear();
            line++;
        }

        while (input.hasNextLine()) {
            tmp.addAll(Arrays.<String>asList(toArray(input.nextLine())));
            String leftSide = tmp.get(0);
            tmp.remove(0);
            grammar.put(leftSide, new ArrayList<String>());
            grammar.get(leftSide).addAll(tmp);
            tmp.clear();
        }
        input.close();

        int szabalyszam = 0;
        for (String baloldal : grammar.keySet()) {
            szamozottGrammar.put(baloldal, new ArrayList<>());
            for (String jobboldal : grammar.get(baloldal)) {
                SzamozottJobboldal tmp2=new SzamozottJobboldal("",0);
                tmp2.setL(jobboldal);
                tmp2.setR(szabalyszam);
                szamozottGrammar.get(baloldal).add(tmp2);
                szabalyszam++;
            }
        }

        for (var nonTerminal :
                nonTerminals) {
            ArrayList<HashSet<String>> hGroups = new ArrayList<>();

            for (var rule :
                    grammar.get(nonTerminal)) {
                HashSet<String> hGroupResult = new HashSet<>();
                hGroups.add(calculateHGroups(rule, hGroupResult));
            }

            first1_map.put(nonTerminal, hGroups);
        }
        ArrayList<String> tempTerminals = new ArrayList<>(terminals);
        tempTerminals.add("#");
        for (String terminal :
                tempTerminals) {
            ArrayList<HashSet<String>> tempTerminalValue = new ArrayList<>();
            tempTerminalValue.add(new HashSet<>(Collections.singletonList(terminal)));
            first1_map.put(terminal, tempTerminalValue);
        }

        System.out.println("------------------");
        System.out.println("FIRST1 MAP");
        for (String key:
                nonTerminals) {
            System.out.printf("%s = %s\n",key, first1_map.get(key));
        }
        System.out.println("------------------");


        // Construct FOLLOW1
        TreeMap<String,HashSet<String>> follow1 = constructFollow1();

        System.out.println("------------------");
        System.out.println("FOLLOW1 MAP");
        for (String key:
                nonTerminals) {
            System.out.printf("%s = %s\n",key, follow1.get(key));
        }
        System.out.println("------------------");

        // Build LL Table
        ArrayList<String> terminalSymbols = new ArrayList<>(terminals);
        terminalSymbols.add("#");
        for (String nonTerminal :
                nonTerminals) {
            llTablazat.put(nonTerminal, new TreeMap<>());
            for (String terminal:
                    terminalSymbols) {
                llTablazat.get(nonTerminal).put(terminal, new SzamozottJobboldal("hiba", -1));
            }
        }

        for (String nonTerminal :
                nonTerminals) {
            for (String terminal:
                    terminalSymbols) {

                for (SzamozottJobboldal szabaly :
                        szamozottGrammar.get(nonTerminal)) {
                    HashSet<String> getFirstAlfa = getFirst(szabaly.getL(), 0, new HashSet<>());
                    if (getFirstAlfa.contains(terminal)
                            ||(getFirstAlfa.contains(deleteSymbol)
                            && follow1.get(nonTerminal).contains(terminal))) {
                        if (llTablazat.get(nonTerminal).get(terminal).getL() == "hiba")
                        {
                            llTablazat.get(nonTerminal).put(terminal,
                                    new SzamozottJobboldal(szabaly.getL().replace("lambda", ""), szabaly.getR()));
                        }
                        else {
                            System.out.println("Nem LL(1) grammatika!");
                            System.exit(-1);
                        }
                    }
                }
            }
        }

        System.out.println("------------------");
        System.out.println("LL MAP");
        for (String ntKey:
                nonTerminals) {
            System.out.printf("%s = ",ntKey);
            for (String tKey :
                    terminalSymbols) {
                System.out.printf("%s: %s\t", tKey, llTablazat.get(ntKey).get(tKey));
            }
            System.out.print("\n");
        }
        System.out.println("------------------");

    }

    private static TreeMap<String, HashSet<String>> constructFollow1() {

        // SETUP H TABLE
        hTablazat.put(0, new TreeMap<>());

        for (String terminal :
                terminals) {
            hTablazat.get(0).put(terminal, new HashSet<>());
            hTablazat.get(0).get(terminal).add(terminal);
        }

        for (String nonTerminals:
                nonTerminals) {
            hTablazat.get(0).put(nonTerminals, new HashSet<>());
        }

        hTablazat.get(0).get(startingSymbol).add("#");

        int i = 1;
        boolean kesz = false;

        // BUILD H TABLE

        while (!kesz) {
            hTablazat.put(i, new TreeMap<>());
            for (String terminal :
                    terminals) {
                hTablazat.get(i).put(terminal, new HashSet<>());
                hTablazat.get(i).get(terminal).add(terminal);
            }

            for (String nt1 :
                    nonTerminals) {
                hTablazat.get(i).put(nt1, new HashSet<>());
                hTablazat.get(i).get(nt1).addAll(
                        hTablazat.get(i-1).get(nt1)
                );

                for (String nt2 :
                        nonTerminals) {

                    for (SzamozottJobboldal alfa :
                            szamozottGrammar.get(nt2)) {
                        int j;
                        if (!alfa.getL().isBlank()) {
                            j = alfa.getL().indexOf(nt1);
                        }
                        else {
                            j = -1;
                        }

                        String alfaString = alfa.getL();
                        while (j != -1) {
                            String beta = alfaString.substring(j+1);
                            HashSet<String> bSymbols = hTablazat.get(i-1).get(nt2);
                            if (bSymbols.contains(deleteSymbol))
                                bSymbols.add("");

                            for (String b :
                                    bSymbols) {
                                hTablazat.get(i).get(nt1).addAll(
                                        getFirst(beta.concat(b), 0, new HashSet<>())
                                );
                            }

                            alfaString = beta;
                            j = alfaString.indexOf(nt1);
                        }
                    }
                }
            }
            if (hTablazat.get(i).equals(hTablazat.get(i-1)))
                kesz = true;
            else
                i++;
        }
        return hTablazat.get(i);
    }

    private static HashSet<String> calculateHGroups(String rule, HashSet<String> hGroupResult) {
        if (rule.equals("lambda")) {
            hGroupResult.add(deleteSymbol);
            return hGroupResult;
        }

        String firstCharacter = String.valueOf(rule.charAt(0));

        if (terminals.contains(firstCharacter)) {
            hGroupResult.add(firstCharacter);
        }
        else {
            for (var innerRule :
                    grammar.get(firstCharacter)) {
                hGroupResult.addAll(calculateHGroups(innerRule, hGroupResult));
            }
        }

        return hGroupResult;
    }

    private static HashSet<String> getFirst(String word, int index, HashSet<String> set) {
        word = word.replace("lambda", deleteSymbol);
        if (index >= word.length()) {
            set.add(deleteSymbol);
            return set;
        }

        String currentCharacter = String.valueOf(word.charAt(index));

        if (currentCharacter.equals(deleteSymbol)) {
            set.add(deleteSymbol);
            return set;
        }

        // Egyedi Hgroup-ok miatt
        HashSet<String> terminalSet = new HashSet<>();
        for (HashSet<String> subSet :
                first1_map.get(currentCharacter)) {
            terminalSet.addAll(subSet);
        }

        // Nincs benne, azaz nem megyünk tovább
        if (!terminalSet.contains(deleteSymbol)) {
            set.addAll(terminalSet);
            return set;
        }
        // Benne van ezért megyünk tovább
        else {
            terminalSet.remove(deleteSymbol);
            set.addAll(terminalSet);
            return getFirst(word, ++index, set);
        }
    }


    public static String[] toArray(String input){
        return input.split("\\s");
    }


    public static void parse() {
        int hossz = word.length();
        int pozicio=0;
        String jel;
        String jobboldal;

        StringBuffer balTetejuVerem =new StringBuffer(startingSymbol+"#");
        String balTetejuFelso="";
        StringBuffer szabalySorozat=new StringBuffer("");

        boolean hiba=false;
        boolean kesz=false;

        while (!hiba && !kesz) {
            balTetejuFelso=balTetejuVerem.substring(0,1);
            jel=String.valueOf(word.charAt(pozicio));

            System.out.print(word.substring(pozicio)+", ");
            System.out.print(balTetejuVerem+", ");
            System.out.println(szabalySorozat);
            System.out.println("------------------");

            // 3. sikeres elemzes
            if ((pozicio==word.length()-1) && (balTetejuFelso.equals("#"))){
                kesz=true;
                System.out.println("A "+word+" szo generalhato");
                continue;
            }


            // 1. kiterjesztes
            if (nonTerminals.contains(balTetejuFelso)){
                jobboldal=llTablazat.get(balTetejuFelso).get(jel).getL().toString();
                if (jobboldal=="hiba") {
                    hiba=true;
                    System.out.println("A "+word+" szo nem generalhato");
                }
                else {
                    balTetejuVerem.replace(0, 1, jobboldal);
                    szabalySorozat.append(llTablazat.get(balTetejuFelso).get(jel).getR().toString());
                }
                continue;
            }

            // 2. és 4. sikeres vagy sikertelen illesztes az inputhoz
            if (terminals.contains(balTetejuFelso)||balTetejuFelso.equals("#")) {
                // 2. sikeres illesztes
                if (jel.equals(balTetejuFelso)) {
                    balTetejuVerem.deleteCharAt(0);
                    pozicio = pozicio + 1;
                } else { // 4. sikertelen illesztes
                    hiba = true;
                }
                continue;
            }

        }

    }

}

