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


public class Package implements Serializable {

    ArrayList<ArrayList<Float>> optimalFactorMatrix;
    transient FileOutputStream storageOutStream;
    String name;
    Date birthDate;
    public Date shouldRepeatDate;
    TreeSet<CrammerItem> itemsTree;

    public Package(String string) throws Exception {
        name = string;
        itemsTree = new TreeSet<CrammerItem>();
        birthDate = new Date();
        shouldRepeatDate = new Date();
        shouldRepeatDate.setTime((4 * 86400000) + shouldRepeatDate.getTime());
        optimalFactorMatrix = new ArrayList<ArrayList<Float>>(50);
        initializeMatrix();
    }

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

    public void serializePackage() throws Exception {
        storageOutStream = new FileOutputStream("/data/data/com.koretsky.crammer/files/" + shouldRepeatDate + "_" + name + ".crammer");
        ObjectOutputStream os = new ObjectOutputStream(storageOutStream);
        os.writeObject(this);
        os.close();
        storageOutStream.close();
    }

    public static Package deserializePackage(File file, String string) throws Exception {
        FileInputStream storageInStream = new FileInputStream(file + "/" + string);
        ObjectInputStream is = new ObjectInputStream(storageInStream);
        Package pack = (Package) is.readObject();
        is.close();
        storageInStream.close();
        return pack;
    }

    public void setPackageName(String newName) {
        this.name = newName;
    }

    public void addItem(String value, String answer) {
        itemsTree.add((new CrammerItem(value, answer)));
    }

    public void removeItem(String targetItem) {
        Iterator iter = itemsTree.iterator();
        while (iter.hasNext()) {
            CrammerItem next = (CrammerItem) iter.next();
            if (next.cmprName(targetItem)) {
                iter.remove();
            }
        }
    }

    private int determineInterval(CrammerItem item) {
        float optimalFactor = this.optimalFactorMatrix.get(item.getEFactorForMatrix()).get(item.getTimesRepeated());
        return item.setInterval((int) optimalFactor * item.getInterval());
    }

    private void modifyMatrix(CrammerItem item, int mark) {
        this.optimalFactorMatrix.get(item.getEFactorForMatrix()).set(item.getTimesRepeated(),
                (float) ((this.optimalFactorMatrix.get(item.getEFactorForMatrix()).get(item.getTimesRepeated()))
                        * (1 - 0.9 * (0.72 + (mark * 0.07) - 1))));
    }

    public void OnItemReviewed(CrammerItem item, int mark) {
        item.incTimesRep();
        item.modifyEfactor(mark);
        this.modifyMatrix(item, mark);
        this.determineInterval(item);
        item.syncNextRepDate();
    }

    public ArrayList<CrammerItem> getCrammerItemsArrayList() {
        return new ArrayList<CrammerItem>(itemsTree);
    }

    public ArrayList<HashMap<String, String>> getItemsArrayList() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap map = null;
        String name = null;
        ArrayList<CrammerItem> tree = new ArrayList(itemsTree);
        for (int i = 0; i < tree.size(); i++) {
            map = new HashMap();
            name = tree.get(i).getValue().toString();
            DateFormat shortFormat = new SimpleDateFormat("EEE, d MMM");
            map.put("value", tree.get(i).getValue());
            map.put("answer", tree.get(i).getAnswer());
            map.put("info", "Rounded mark: " + Math.round(tree.get(i).getAverageMark()) + ", next review: " + shortFormat.format(shouldRepeatDate).toString());
            list.add(map);
        }
        return list;
    }
}
