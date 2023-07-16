package me.lordierclaw.util;

import java.util.ArrayList;
import java.util.Collection;

public class Table {
    private final ArrayList<String> header;
    private final ArrayList<ArrayList<?>> rows = new ArrayList<>();
    private final ArrayList<Integer> pads = new ArrayList<>();

    private static int COLUMN_OFFSET = 3;

    public static void setColumnOffset(int value) {
        COLUMN_OFFSET = value;
    }

    public static final int PRINT_NULL = 1;
    public static final int PRINT_EMPTY = 2;
    protected int ifNullFound = PRINT_EMPTY;

    public void setIfNullFound(int value) {
        ifNullFound = value;
    }

    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    private int align = ALIGN_LEFT;

    public void setAlign(int align) {
        this.align = align;
    }

    public static final int STYLE_NONE = 0;
    public static final int STYLE_SIMPLE = 1;
    public static final int STYLE_ADVANCED = 2;
    public static final int STYLE_MORE_BORDER = 3;

    private int style = STYLE_SIMPLE;

    public void setStyle(int style) {
        this.style = style;
    }

    private int padSum = 0;

    public Table(ArrayList<String> header) {
        this.header = header;
        for (String col: header) {
            int value = col.length() + COLUMN_OFFSET;
            pads.add(value);
            padSum += value;
        }
    }

    public Table(Collection<String> header) {
        this(new ArrayList<>(header));
    }

    private String padString(String string, int length) {
        if (align == ALIGN_RIGHT) {
            if (style == STYLE_ADVANCED || style == STYLE_MORE_BORDER)
                return String.format("%" + length + "s ", string);
            else
                return String.format("%" + length + "s", string);
        } else {
            if (style == STYLE_ADVANCED || style == STYLE_MORE_BORDER)
                return String.format(" %-" + length + "s", string);
            else
                return String.format("%-" + length + "s", string);
        }
    }

    private void correctPadding(ArrayList<?> row) {
        int localSum = 0;
        for (int i = 0; i < row.size(); i++) {
            int max = row.get(i).toString().length() + COLUMN_OFFSET;
            if (pads.get(i) > max) max = pads.get(i);
            pads.set(i, max);
            localSum += max;
        }
        if (localSum > padSum) padSum = localSum;
    }
    public void addRow(ArrayList<?> row) {
        if (row.size() != header.size()) {
            throw new TableImportException("Added row's size does not match with header's size");
        }
        rows.add(row);
        correctPadding(row);
    }

    public void addRow(Collection<?> row) {
        addRow(new ArrayList<>(row));
    }

    private String getHeaderTableFormat() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < header.size(); i++) {
            str.append(padString(header.get(i), pads.get(i)));
            if (style == STYLE_ADVANCED || style == STYLE_MORE_BORDER) {
                if (i != header.size() - 1) str.append('|');
            }
        }
        if (style == STYLE_MORE_BORDER) {
            StringBuilder head = new StringBuilder();
            String line = padString("", padSum + header.size()*2).replace(" ", "-");
            head.append(line);
            head.append("\n|").append(str).append("|\n");
            head.append(line);
            return head.toString() + '\n';
        } else if (style == STYLE_ADVANCED) {
            String line = padString("", padSum + header.size()*2).replace(" ", "-");
            str.append('\n').append(line);
        } else if (style == STYLE_SIMPLE) {
            String line = padString("", padSum).replace(" ", "-");
            str.append('\n').append(line);
        }
        return str.toString() + '\n';
    }

    private String getRowTableFormat(ArrayList<?> row) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            str.append(padString(row.get(i).toString(), pads.get(i)));
            if (style == STYLE_ADVANCED || style == STYLE_MORE_BORDER) {
                if (i != row.size()-1) str.append('|');
            }
        }
        if (style == STYLE_MORE_BORDER) {
            return "|" + str + "|\n";
        }
        str.append('\n');
        return str.toString();
    }

    @Override
    public String toString() {
        if (rows.isEmpty()) return "Table is empty.";
        StringBuilder str = new StringBuilder();
        str.append(getHeaderTableFormat());
        for (ArrayList<?> row: rows) {
            str.append(getRowTableFormat(row));
        }
        if (style == STYLE_MORE_BORDER) {
            String line = padString("", padSum + header.size()*2).replace(" ", "-");
            str.append(line);
        }
        return str.toString().stripTrailing(); // Remove the last \n character
    }

    public static class TableImportException extends RuntimeException {
        TableImportException(String msg) {
            super(msg);
        }
    }
}