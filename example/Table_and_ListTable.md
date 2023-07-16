# Table_and_ListTable

## 1. Table
`Table` is a responsive console table. It is easy to use, add row, and can be print out easily.

Define a Table by passing a list for header:

```java
Table table = new Table(List.of("ID", "Name", "User Type"))
```

After that, we can add rows to the table by using `addRow()`. It takes a collection/list of String or Object, then get their content by `toString()` method.

```java
table.addRow(List.of(user.getId(), user.getName(), user.getType()))
```

A table can be printed by passing it to `System.out.print()` normally or get the whole content of the table by calling `toString()`

There are also some modifications for the table. By default, if a reference is null, the table will print empty space. However, we can make it print out `null` with this way:

```java
table.setIfNullFound(Table.PRINT_NULL)
```

We can also change the alignment of the table with `setAlign()` or change the style with `setStyle()`.

## 2. ListTable

`ListTable` was created to be an `ArrayList` with `Table`. However, the definition of `ListTable` can be quite difficult to use.
We need to pass at least **2** parameters, one is for the **_table's header_**, and one is for its **_element extraction_**.

This is the constructor of `ListTable`:

```java
public ListTable(Collection<String> tableHeader, ObjectExporter<E> objectExporter {
    // Primary Constructor
}

public ListTable(Collection<E> list, Collection<String> tableHeader, ObjectExporter<E> objectExporter) {
    // This constructor will import the collection above
}
```

ListTable is a normal ArrayList, we can use `add()`, `remove()`, or any other methods. To get a Table, simply using `getTable()`.

We can also pass a `ValueFilter` interface to `getTable()` or to `findByFilter()` to get the exact element we want.

This is an example of using ListTable and Table

```java
// Definition
ListTable<User> userListTable = new ListTable<>(List.of("ID", "Name", "User Type"), new ListTable.ObjectExporter<User>() {
    @Override
    public Collection<?> exportObjectToRow(User object) {
        return List.of(object.getId(), object.getName(), object.getType());
    }
});

// Usage
userListTable.add(new User(123L, "Nguyen Van A", User.UserType.TYPE1));
userListTable.add(new User(456L, "Nguyen Thi B", User.UserType.TYPE2));
userListTable.add(new User(789L, "Nguyen C", User.UserType.TYPE3));

// Print the Table
Table table = userListTable.getTable();
System.out.println(table);

table.setAlign(Table.ALIGN_RIGHT);
System.out.println(table);
```