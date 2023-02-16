import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class LogicTable {
    public final List<TableRow> fullTable = new ArrayList<>();

    private boolean isMuted;

    public LogicTable(boolean isMuted) {
        this.isMuted = isMuted;
        fullTable.add(new TableRow(1, 2, 4, 6, 19, 8, 19, 19, 12, 19, 14, 17, 1, 19, 21, false, true, null));
        fullTable.add(new TableRow(2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, false, true, null));
        fullTable.add(new TableRow(3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,true,false, Optional.of("<azonosito>")));
        fullTable.add(new TableRow(4,5,4,5,5,5,5,5,5,5,5,5,5,5,5,false,true, null));
        fullTable.add(new TableRow(5,1,1,1,1,1,1,1,1,1,1,1,1,1,1,true,false, Optional.of("<konstans>")));
        fullTable.add(new TableRow(6,6,6,6,7,6,6,6,6,6,6,6,6,6,19,false,true, null));
        fullTable.add(new TableRow(7,1,1,1,1,1,1,1,1,1,1,1,1,1,1,false,false, null));
        fullTable.add(new TableRow(8,20,20,20,20,20,9,20,20,20,20,20,20,20,19,false,true, null));
        fullTable.add(new TableRow(9,9,9,9,9,9,10,9,9,9,9,9,9,9,19,false,true, null));
        fullTable.add(new TableRow(10,9,9,9,9,9,10,11,9,9,9,9,9,9,19,false,true, null));
        fullTable.add(new TableRow(11,1,1,1,1,1,1,1,1,1,1,1,1,1,1,false,false, null));
        fullTable.add(new TableRow(12,20,20,20,20,20,20,20,20,13,20,20,20,20,19,false,true, null));
        fullTable.add(new TableRow(13,1,1,1,1,1,1,1,1,1,1,1,1,1,1,false,false, Optional.of("<:=>")));
        fullTable.add(new TableRow(14,20,20,20,20,20,20,20,20,15,20,16,20,20,19,false,true, null));
        fullTable.add(new TableRow(15,1,1,1,1,1,1,1,1,1,1,1,1,1,1,false,false, Optional.of("<<=>")));
        fullTable.add(new TableRow(16,1,1,1,1,1,1,1,1,1,1,1,1,1,1,false,false, Optional.of("<<>>")));
        fullTable.add(new TableRow(17,20,20,20,20,20,20,20,20,18,20,20,20,20,19,false,true, null));
        fullTable.add(new TableRow(18,1,1,1,1,1,1,1,1,1,1,1,1,1,1,false,false, Optional.of("<>=>")));
        fullTable.add(new TableRow(19,1,1,1,1,1,1,1,1,1,1,1,1,1,1,false,false, Optional.of("bug occurred")));
        fullTable.add(new TableRow(20,1,1,1,1,1,1,1,1,1,1,1,1,1,1,true,false, Optional.of("missing feature")));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public TableRow getRow(int rowIndex) {
        TableRow resRow = fullTable.stream()
                .filter( row -> row.id == rowIndex)
                .findFirst()
                .get();

        if (isMuted){
            resRow.message = Optional.empty();
        }
        return resRow;

    }
}
