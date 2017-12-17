package com.koretsky.crammer.sm5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Данный класс реализует алгоритм интервальных повторений SM5 и содержит множество заметок определенной тематики
 *
 * @author Корецкий Михаил
 * @version 1.0
 */
public class Package implements Serializable {
    /**
     * Матрица оптимальных множителей
     */
    private ArrayList<ArrayList<Float>> optimalFactorMatrix;
    /**
     * Объект для организации сериализации
     */
    private transient FileOutputStream storageOutStream;
    /**
     * Имя пакета
     */
    private String name;
    /**
     * Дата создания пакета
     */
    private Date birthDate;
    /**
     * Самая ранняя дата повторения из всех заметок контейнера
     */
    public Date shouldRepeatDate;
    /**
     * Дерево хранящее заметки в порядке возростания даты повторения
     */
    private TreeSet<Item> itemsTree;

    /**
     * Конструктор пакета
     * @param string будущее имя пакета
     * @throws Exception
     */
    public Package(String string) throws Exception {
        name = string;
        itemsTree = new TreeSet<Item>();
        birthDate = new Date();
        shouldRepeatDate = new Date();
        shouldRepeatDate.setTime((4 * 86400000) + shouldRepeatDate.getTime());
        optimalFactorMatrix = new ArrayList<ArrayList<Float>>(50);
        initializeMatrix();
    }

    /**
     * Реализует последовательность операций при повторениии заметки
     * @param item повторяемая заметка
     * @param mark оценка после повторения
     */
    public void OnItemReviewed(Item item, float mark) {
        item.incTimesRep();
        item.modifyEfactor(mark);
        this.modifyMatrix(item, mark);
        this.determineInterval(item);
        item.syncNextRepDate();
    }

    /**
     *
     * @return Список заметок упорядоченных по дате
     */
    public ArrayList<Item> getCrammerItemsArrayList() {
        return new ArrayList<Item>(itemsTree);
    }

    /**
     *
     * @return Список словарей с именами заметок и дополнительной информацией
     */
    public ArrayList<HashMap<String, String>> getItemsMapsArrayList() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap map = null;
        String name = null;
        ArrayList<Item> tree = new ArrayList(itemsTree);
        for (int i = 0; i < tree.size(); i++) {
            map = new HashMap();
            name = tree.get(i).getValue().toString();
            DateFormat shortFormat = new SimpleDateFormat("EEE, d MMM");
            map.put("value", tree.get(i).getValue());
            map.put("answer", tree.get(i).getAnswer());
            map.put("info", "Rounded mark: " + Math.round(tree.get(i).getAverageMark()) + ", next review: " + shortFormat.format(tree.get(i).getNextRepDate()).toString());
            list.add(map);
        }
        return list;
    }

    /**
     * Сохраняет данный пакет в файл
     * @throws Exception
     */
    public void serializePackage() throws Exception {
        storageOutStream = new FileOutputStream("/data/data/com.koretsky.crammer/files/" + shouldRepeatDate + "_" + name + ".crammer");
        ObjectOutputStream os = new ObjectOutputStream(storageOutStream);
        os.writeObject(this);
        os.close();
        storageOutStream.close();
    }

    /**
     * Возвращает объект пакета заметок из файла по имени передаваемом в параметрах
     * @param file Директория внутреннего хранилища Android-приложения
     * @param string Имя пакета для преобразования из файла в объект
     * @return Пакет полученный из файла
     * @throws Exception
     */
    public static Package deserializePackage(File file, String string) throws Exception {
        FileInputStream storageInStream = new FileInputStream(file + "/" + string);
        ObjectInputStream is = new ObjectInputStream(storageInStream);
        Package pack = (Package) is.readObject();
        is.close();
        storageInStream.close();
        pack.shouldRepeatDate = pack.itemsTree.first().getNextRepDate();
        return pack;
    }

    /**
     *
     * @param newName Новое имя пакета
     */
    public void setPackageName(String newName) {
        this.name = newName;
    }

    /**
     * Создает новую заметку в пакете
     * @param value Первое поле заметки
     * @param answer Второе поле заметки
     */
    public void addItem(String value, String answer) {
        itemsTree.add((new Item(value, answer)));
    }

    /**
     *
     * @param targetItem Имя заметки для удаления из пакета
     */
    public void removeItem(String targetItem) {
        Iterator iter = itemsTree.iterator();
        while (iter.hasNext()) {
            Item next = (Item) iter.next();
            if (next.cmprName(targetItem)) {
                iter.remove();
            }
        }
    }

    /**
     * Определяет интервал повторения для заметки передаваемой в параметрах
     * @param item заметка для которой необходимо определить интервал
     * @return новый интервал повторения заметки
     */
    private int determineInterval(Item item) {
        float optimalFactor = this.optimalFactorMatrix.get(item.getEFactorForMatrix()).get(item.getTimesRepeated());
        return item.setInterval((int) optimalFactor * item.getInterval());
    }

    /**
     * Производит первичную инициализацию матрицы
     */
    private void initializeMatrix() {
        ArrayList<Float> column = new ArrayList<Float>();
        for (int j = 0; j <= 50; j++) {
            column = new ArrayList<Float>(column);
            column.clear();
            column.add(new Float(4));
            for (int i = 0; i < 100; i++) {
                column.add(new Float(j * 0.1));
            }
            optimalFactorMatrix.add(column);

        }
    }

    /**
     * Корректирует матрицу оптимальных множителей после повторения заметки
     * @param item повторяемая заметка
     * @param mark оценка после ее повторения
     */
    private void modifyMatrix(Item item, float mark) {
        this.optimalFactorMatrix.get(item.getEFactorForMatrix()).set(item.getTimesRepeated(),
                (float) ((this.optimalFactorMatrix.get(item.getEFactorForMatrix()).get(item.getTimesRepeated()))
                        * (1 - 0.9 * (0.72 + (mark * 0.07) - 1))));
    }


}
