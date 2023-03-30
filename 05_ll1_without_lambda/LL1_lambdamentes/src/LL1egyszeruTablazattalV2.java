import java.io.*;
import java.nio.file.Path;
import java.util.*;


// Futtatáskor az első argumentum a grammatikát tartalmazó file neve, a második argumentum az elemzendő szó.
//A grammatika file formátuma olyan, mint a mellékelt grammar.txt fileé: az első sor a kezdőszimbólum, a második a
// terminálisok, a harmadik a nemterminálisok halmaza
// Ezután egy-egy sorban a sor elején álló nemterminálisra vonatkozó szabály-jobboldalak szóközzel elválasztva


public class LL1egyszeruTablazattalV2 {

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
    public static ArrayList<String> terminals = new ArrayList<String>();
    public static ArrayList<String> nonTerminals = new ArrayList<String>();
    public static TreeMap<String,ArrayList<String>> grammar = new TreeMap<>();
    public static TreeMap<String,ArrayList<SzamozottJobboldal>> szamozottGrammar = new TreeMap<>();

    ////    public static TreeMap<String,TreeMap<String,String>> llTablazat =new TreeMap<>();
    public static TreeMap<String,TreeMap<String,SzamozottJobboldal>> llTablazat =new TreeMap<>();


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

        ArrayList<TreeMap<String, HashSet<String>>> first1Rows = new ArrayList<>();


        TreeMap<String, HashSet<String>> h0Row = new TreeMap<>();
        for (String nonTerminal
                : nonTerminals) {
            HashSet<String> cell = new HashSet<>();
            for (var rule :
                    grammar.get(nonTerminal)) {
                cell.add(String.valueOf(rule.charAt(0)));
            }
            h0Row.put(nonTerminal, cell);
        }
        for (String terminal :
                terminals) {
            HashSet<String> cell = new HashSet<>();
            cell.add(terminal);
            h0Row.put(terminal, cell);
        }
        first1Rows.add(h0Row);
        System.out.println(first1Rows);

        int index = 0;
        do {
            index++;
            TreeMap<String, HashSet<String>> hIRow = new TreeMap<>();

            for (String nonTerminal
                    : nonTerminals) {

                HashSet<String> lastCell = first1Rows.get(index-1).get(nonTerminal);
                HashSet<String> newCell = new HashSet<>();

                for (var cellContent :
                        lastCell) {

                    newCell.addAll(first1Rows.get(index-1).get(cellContent));

                }
                hIRow.put(nonTerminal, newCell);
            }

            for (String terminal :
                    terminals) {
                HashSet<String> cell = new HashSet<>();
                cell.add(terminal);
                hIRow.put(terminal, cell);
            }
            first1Rows.add(hIRow);
        }while (!first1Rows.get(index).equals(first1Rows.get(index-1)));


        for (var row
                : first1Rows) {
            System.out.println(row);
        }

        TreeMap<String, HashSet<String>> first1Row = first1Rows.get(index);
        for (String nonTerminal :
                nonTerminals) {

            TreeMap<String, SzamozottJobboldal> newRow = new TreeMap<>();

            for (SzamozottJobboldal rightSide :
                    szamozottGrammar.get(nonTerminal)) {

                String firstCharacter = String.valueOf(rightSide.getL().charAt(0));
                HashSet<String> terminalsToUseThisRule = first1Row.get(firstCharacter);
                for (String terminal :
                        terminalsToUseThisRule) {
                    newRow.put(terminal, rightSide);
                }
            }
            llTablazat.put(nonTerminal, newRow);
        }

        System.out.println(llTablazat);
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

