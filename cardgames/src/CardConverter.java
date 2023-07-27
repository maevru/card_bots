import java.util.HashMap;

public class CardConverter {

    private final HashMap<Integer, String> convert;

    public CardConverter(){
        convert = new HashMap<>();
        convert.put(0, "Klaver 2");
        convert.put(1, "Klaver 3");
        convert.put(2, "Klaver 4");
        convert.put(3, "Klaver 5");
        convert.put(4, "Klaver 6");
        convert.put(5, "Klaver 7");
        convert.put(6, "Klaver 8");
        convert.put(7, "Klaver 9");
        convert.put(8, "Klaver 10");
        convert.put(9, "Klaver Boer");
        convert.put(10, "Klaver Dame");
        convert.put(11, "Klaver Heer");
        convert.put(12, "Klaver Aas");
        convert.put(13, "Schoppen 2");
        convert.put(14, "Schoppen 3");
        convert.put(15, "Schoppen 4");
        convert.put(16, "Schoppen 5");
        convert.put(17, "Schoppen 6");
        convert.put(18, "Schoppen 7");
        convert.put(19, "Schoppen 8");
        convert.put(20, "Schoppen 9");
        convert.put(21, "Schoppen 10");
        convert.put(22, "Schoppen Boer");
        convert.put(23, "Schoppen Dame");
        convert.put(24, "Schoppen Heer");
        convert.put(25, "Schoppen Aas");
        convert.put(26, "Koeken 2");
        convert.put(27, "Koeken 3");
        convert.put(28, "Koeken 4");
        convert.put(29, "Koeken 5");
        convert.put(30, "Koeken 6");
        convert.put(31, "Koeken 7");
        convert.put(32, "Koeken 8");
        convert.put(33, "Koeken 9");
        convert.put(34, "Koeken 10");
        convert.put(35, "Koeken Boer");
        convert.put(36, "Koeken Dame");
        convert.put(37, "Koeken Heer");
        convert.put(38, "Koeken Aas");
        convert.put(39, "Harten 2");
        convert.put(40, "Harten 3");
        convert.put(41, "Harten 4");
        convert.put(42, "Harten 5");
        convert.put(43, "Harten 6");
        convert.put(44, "Harten 7");
        convert.put(45, "Harten 8");
        convert.put(46, "Harten 9");
        convert.put(47, "Harten 10");
        convert.put(48, "Harten Boer");
        convert.put(49, "Harten Dame");
        convert.put(50, "Harten Heer");
        convert.put(51, "Harten Aas");
    }

    public String translate(int card){
        return(convert.get(card));
    }
}
