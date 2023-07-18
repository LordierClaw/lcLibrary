package me.lordierclaw.util;

import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

public class Table {

    public Table(ArrayList<String> header) {
        this.header = header;
        int n = header.size();
        while (n-- > 0) pads.add(0);
        setFont(DEFAULT_FONT);
    }

    public Table(Collection<String> header) {
        this(new ArrayList<>(header));
    }

    private final ArrayList<String> header;
    private final ArrayList<ArrayList<?>> rows = new ArrayList<>();
    private final ArrayList<Integer> pads = new ArrayList<>();

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
    private int style = STYLE_MORE_BORDER;

    public void setStyle(int style) {
        this.style = style;
    }

    private final int DEFAULT_PADDING_OFFSET = 3;
    private int padSum = 0;

    private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private FontMetrics fontMetrics = null;

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public void setFontMetrics(FontMetrics fontMetrics) {
        this.fontMetrics = fontMetrics;
    }

    public void setFont(Font font) {
        Graphics graphics = new BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB).getGraphics();
        graphics.setFont(font);
        setFontMetrics(graphics.getFontMetrics());
    }

    public void addRow(ArrayList<?> row) {
        if (row.size() != header.size()) {
            throw new TableImportException("Added row's size does not match with header's size");
        }
        rows.add(row);
    }

    public void addRow(Collection<?> row) {
        addRow(new ArrayList<>(row));
    }

    private String padString(String string, int width) {
        int length = Math.round((float)(width - fontMetrics.stringWidth(string)) / fontMetrics.charWidth(' '));
        String padding = new String(new char[length]).replace('\0', ' ');
        if (align == ALIGN_RIGHT) {
            if (style == STYLE_ADVANCED || style == STYLE_MORE_BORDER)
                return String.format(padding + "%s ", string);
            else
                return String.format(padding + "%s", string);
        } else {
            if (style == STYLE_ADVANCED || style == STYLE_MORE_BORDER)
                return String.format(" %s" + padding, string);
            else
                return String.format("%s" + padding, string);
        }
    }

    private void generatePadding() {
        for (int i = 0; i < header.size(); i++) {
            int pad = fontMetrics.stringWidth(header.get(i)) + fontMetrics.charWidth(' ')*DEFAULT_PADDING_OFFSET;
            pads.set(i, pad);
            padSum += pad;
        }
        for (ArrayList<?> row: rows) {
            int localSum = 0;
            for (int i = 0; i < row.size(); i++) {
                int pad = fontMetrics.stringWidth(row.get(i).toString()) + fontMetrics.charWidth(' ')*DEFAULT_PADDING_OFFSET;
                if (pads.get(i) < pad) pads.set(i, pad);
                localSum += pads.get(i);
            }
            if (localSum > padSum) padSum = localSum;
        }
    }

    private String getSeparator() {
        int width = 0;
        switch (style) {
            case STYLE_SIMPLE -> width = padSum;
            case STYLE_ADVANCED -> width = padSum + fontMetrics.charWidth('|') * header.size() + fontMetrics.charWidth(' ') * header.size();
            case STYLE_MORE_BORDER -> width = padSum + fontMetrics.charWidth('|') * (header.size()+1) + fontMetrics.charWidth(' ') * header.size();
        }
        if (width == 0) return "";
        int length = Math.round((float) width/fontMetrics.charWidth('-'));
        return new String(new char[length]).replace('\0', '-');
    }

    private String getHeaderTableFormat() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < header.size(); i++) {
            str.append(padString(header.get(i), pads.get(i)));
            if (style == STYLE_ADVANCED || style == STYLE_MORE_BORDER) {
                if (i != header.size() - 1) str.append('|');
            }
        }
        String line = getSeparator();
        if (style == STYLE_MORE_BORDER) {
            return line + "\n|" + str + "|\n" + line + '\n';
        } else if (style == STYLE_ADVANCED) {
            str.append('\n').append(line);
        } else if (style == STYLE_SIMPLE) {
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
        generatePadding();
        StringBuilder str = new StringBuilder();
        str.append(getHeaderTableFormat());
        for (ArrayList<?> row: rows) {
            str.append(getRowTableFormat(row));
        }
        if (style == STYLE_MORE_BORDER)
            str.append(getSeparator());
        else
            str.deleteCharAt(str.length()-1); // remove the last \n character
        return str.toString();
    }

    public static class TableImportException extends RuntimeException {
        TableImportException(String msg) {
            super(msg);
        }
    }
}