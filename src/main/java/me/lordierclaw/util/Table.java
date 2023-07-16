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

    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    private int align = ALIGN_LEFT;

    public void setAlign(int align) {
        this.align = align;
    }

    public void setIfNullFound(int value) {
        ifNullFound = value;
    }

    private String padString(String string, int length) {
        if (align == ALIGN_RIGHT) return String.format("%" + length + "s", string);
        else return String.format("%-" + length + "s", string);
    }

    public Table(ArrayList<String> header) {
        this.header = header;
        for (String col: header) {
            pads.add(col.length() + COLUMN_OFFSET);
        }
    }

    public Table(Collection<String> header) {
        this(new ArrayList<>(header));
    }

    private void correctPadding(ArrayList<?> row) {
        for (int i = 0; i < row.size(); i++) {
            int max = row.get(i).toString().length() + COLUMN_OFFSET;
            if (pads.get(i) > max) max = pads.get(i);
            pads.set(i, max);
        }
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

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (rows.isEmpty()) return "Table is empty.";
        int maxLength = 0;
        for (int i = 0; i < header.size(); i++) {
            maxLength += pads.get(i);
            str.append(padString(header.get(i), pads.get(i)));
        }
        str.append('\n');
        str.append(padString("", maxLength).replace(" ", "-"));
        str.append('\n');
        for (var row: rows) {
            for (int i = 0; i < row.size(); i++) {
                String content = "";
                if (row.get(i) != null) content = row.get(i).toString();
                else if (ifNullFound == PRINT_NULL) content = "null";
                str.append(padString(content, pads.get(i)));
            }
            str.append('\n');
        }
        return str.toString();
    }

    public static class TableImportException extends RuntimeException {
        TableImportException(String msg) {
            super(msg);
        }
    }
}