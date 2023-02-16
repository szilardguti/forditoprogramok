import java.util.Optional;

public class TableRow {

    public int id;

    public int alphabeticalGoTo;

    public int numericalGoTo;

    public int curlyBracketOpenGoTo;

    public int curlyBracketCloseGoTo;

    public int bracketOpenGoTo;

    public int bracketCloseGoTo;

    public int starSignGoTo;

    public int colonGoTo;

    public int equalGoTo;

    public int squareBracketOpenGoTo;

    public int squareBracketCloseGoTo;

    public int spaceGoTo;

    public int dollarSignGoTo;

    public int elseCharacterGoTo;

    public boolean backupChar;

    public boolean readInput;

    public Optional<String> message;

    public int evaluate(char sign) {
        if (message != null && message.isPresent())
            System.out.println(message.get());

        if (Character.isAlphabetic(sign))
            return alphabeticalGoTo;
        else if(Character.isDigit(sign))
            return numericalGoTo;

        switch (sign) {
            case '{' -> {
                return curlyBracketOpenGoTo;
            }
            case '}' -> {
                return curlyBracketCloseGoTo;
            }
            case '(' -> {
                return bracketOpenGoTo;
            }
            case ')' -> {
                return bracketCloseGoTo;
            }
            case '*' -> {
                return starSignGoTo;
            }
            case ':' -> {
                return colonGoTo;
            }
            //
            case '=' -> {
                return equalGoTo;
            }
            case '<' -> {
                return squareBracketOpenGoTo;
            }
            case '>' -> {
                return squareBracketCloseGoTo;
            }
            case ' ' -> {
                return spaceGoTo;
            }
            case '$' -> {
                return dollarSignGoTo;
            }
            default -> {
                return elseCharacterGoTo;
            }
        }
    }

    public TableRow(int id, int alphabeticalGoTo, int numericalGoTo, int curlyBracketOpenGoTo, int curlyBracketCloseGoTo, int bracketOpenGoTo, int starSignGoTo, int bracketCloseGoTo,  int colonGoTo, int equalGoTo, int squareBracketOpenGoTo, int squareBracketCloseGoTo, int spaceGoTo, int elseCharacterGoTo, int dollarSignGoTo, boolean backupChar, boolean readInput, Optional<String> message) {
        this.id = id;
        this.alphabeticalGoTo = alphabeticalGoTo;
        this.numericalGoTo = numericalGoTo;
        this.curlyBracketOpenGoTo = curlyBracketOpenGoTo;
        this.curlyBracketCloseGoTo = curlyBracketCloseGoTo;
        this.bracketOpenGoTo = bracketOpenGoTo;
        this.bracketCloseGoTo = bracketCloseGoTo;
        this.starSignGoTo = starSignGoTo;
        this.colonGoTo = colonGoTo;
        this.equalGoTo = equalGoTo;
        this.squareBracketOpenGoTo = squareBracketOpenGoTo;
        this.squareBracketCloseGoTo = squareBracketCloseGoTo;
        this.spaceGoTo = spaceGoTo;
        this.dollarSignGoTo = dollarSignGoTo;
        this.elseCharacterGoTo = elseCharacterGoTo;
        this.backupChar = backupChar;
        this.readInput = readInput;
        this.message = message;
    }

    @Override
    public String toString() {
        return "TableRow{" +
                "id=" + id +
                ", alphabeticalGoTo=" + alphabeticalGoTo +
                ", numericalGoTo=" + numericalGoTo +
                ", curlyBracketOpenGoTo=" + curlyBracketOpenGoTo +
                ", curlyBracketCloseGoTo=" + curlyBracketCloseGoTo +
                ", bracketOpenGoTo=" + bracketOpenGoTo +
                ", bracketCloseGoTo=" + bracketCloseGoTo +
                ", starSignGoTo=" + starSignGoTo +
                ", colonGoTo=" + colonGoTo +
                ", equalGoTo=" + equalGoTo +
                ", squareBracketOpenGoTo=" + squareBracketOpenGoTo +
                ", squareBracketCloseGoTo=" + squareBracketCloseGoTo +
                ", spaceGoTo=" + spaceGoTo +
                ", dollarSignGoTo=" + dollarSignGoTo +
                ", elseCharacterGoTo=" + elseCharacterGoTo +
                ", backupChar=" + backupChar +
                ", readInput=" + readInput +
                ", message=" + message +
                '}';
    }
}
