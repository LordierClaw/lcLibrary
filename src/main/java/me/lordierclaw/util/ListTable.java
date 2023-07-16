package me.lordierclaw.util;

import java.util.ArrayList;
import java.util.Collection;

public class ListTable<E> extends ArrayList<E> {

    public interface ObjectExporter<E> {
        Collection<?> exportObjectToRow(E object);
    }

    public interface ValueFilter<E> {
        Boolean filter(E object);
    }

    private final ObjectExporter<E> objectExporter;
    private final Collection<String> tableHeader;

    public ListTable(Collection<String> tableHeader, ObjectExporter<E> objectExporter) {
        this.objectExporter = objectExporter;
        this.tableHeader = tableHeader;
    }

    public ListTable(Collection<E> list, Collection<String> tableHeader, ObjectExporter<E> objectExporter) {
        super(list);
        this.objectExporter = objectExporter;
        this.tableHeader = tableHeader;
    }

    public Table getTable() {
        Table table = new Table(tableHeader);
        for (E object: this) {
            table.addRow(objectExporter.exportObjectToRow(object));
        }
        return table;
    }

    public Table getTable(ValueFilter<E> valueFilter) {
        Table table = new Table(tableHeader);
        for (E object: this) {
            if (valueFilter.filter(object))
                table.addRow(objectExporter.exportObjectToRow(object));
        }
        return table;
    }

    public static class NoMatchingResult extends Exception {
        public NoMatchingResult(String msg) {
            super(msg);
        }
    }

    public E findByFilter(ValueFilter<E> valueFilter) throws NoMatchingResult {
        for (E object: this) {
            if (valueFilter.filter(object))
                return object;
        }
        throw new NoMatchingResult("There is no element matching.");
    }
}
