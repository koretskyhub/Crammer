package com.koretsky.crammer.sm5;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Михаил on 28.11.2017.
 * Данный класс реализует объект заметки предназначенной для повторения
 * @author Корецкий Михаил
 * @version 1.0
 */

public class Item implements Serializable, Comparable<Item> {
    /**
     * Данное поле хранит первую часть заметки
     */
    private String value;
    /**
     * Данное поле хранит вторую часть заметки или ответ на вопрос расположенный в первой части заметки
     */
    private String answer;
    /**
     * Данное поле хранит коэффициент простоты усвоения заметки, который используется для определния интервала повторения
     */
    private float eFactor;
    /**
     * Данное поле хранит среднюю оценку заметки
     */
    private float averageMark;
    /**
     * Данное поле хранит среднюю оценку заметки, необходимо для определния интервала повторений
     */
    private int timesRepeated;
    /**
     * Данное поле хранит текущий интервал повторения заметки
     */
    private int repInterval;
    /**
     * Данное поле хранит следующую дату повторения заметки в обьекте Date
     */
    private Date nextRepDate;

    /**
     * Конструктор нового объекта заметки
     *
     * @param v первая часть заметки в виде объекта String
     * @param a вторая часть заметки в виде объекта String
     */
    public Item(String v, String a) {
        value = v;
        answer = a;
        averageMark = (float) 2.5;
        timesRepeated = 0;
        repInterval = 4;
        nextRepDate = new Date();
        this.syncNextRepDate();

    }

    /**
     *
     * @return Значение первого поля заметки
     */
    public String getValue() {
        return value;
    }

    /**
     * @return Значение второго поля заметки
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @return Интервал повторения заметки
     */
    public int getInterval() {
        return this.repInterval;
    }

    /**
     * Реализует сравнение заметок по дате следующего повторения
     * @param crammerItem заметка для сравнения с вызывающей метод
     * @return ближайшая к повторению заметка
     */
    @Override
    public int compareTo(@NonNull Item crammerItem) {
        return this.getNextRepDate().compareTo(crammerItem.getNextRepDate());
    }

    /**
     *
     * @return Возвращает коэффициент простоты для использования в матрице оптимальных множителей
     */
    public int getEFactorForMatrix() {
        return (int) (this.eFactor * 10);
    }

    /**
     *
     * @return Возвращает количство повторений заметки
     */
    public int getTimesRepeated() {
        return (int) this.timesRepeated;
    }

    /**
     *
     * @return Возвращает дату следующего повторения заметки
     */
    protected Date getNextRepDate() {
        return this.nextRepDate;
    }

    /**
     * Обновляет дату повторения заметки в соответствии с интервалом повтоерния заметки
     */
    protected void syncNextRepDate() {
        this.nextRepDate.setTime((repInterval * 86400000) + new Date().getTime());
    }

    /**
     *
     * @param name имя для сравнения
     * @return Возвращает true при равенстве строки параметра и имени данной заметки
     */
    protected boolean cmprName(String name) {
        return name.equals(this.value);
    }

    /**
     *
     * @param mark оценка полученная при повторении заметки
     * @return новое значение коэффициента простоты
     */
    protected float modifyEfactor(float mark) {
        averageMark = (float) ((averageMark * (timesRepeated - 1) / timesRepeated) + (mark / timesRepeated));
        if (mark < 3) {
            timesRepeated = 0;
            return eFactor;
        }
        this.eFactor = (float) (eFactor + (0.1 - (5 - mark) * (0.08 + (5 - mark) * 0.02)));
        if (eFactor < 1.3) eFactor = (float) 1.3;
        if (eFactor > 5.0) eFactor = (float) 5.0;
        return eFactor;
    }

    /**
     *
     * @return Возвращает среднее арифметическое оценок заметки
     */
    protected float getAverageMark() {
        return averageMark;
    }

    /**
     * Инкрементирует количество повторений заметки
     * @return Актуальное количество повторений заметки
     */
    protected int incTimesRep() {
        return this.timesRepeated = timesRepeated + 1;
    }


    /**
     *
     * @param interval параметр нового интервала повторения заметки
     * @return Новый интервал повторения заметки
     */
    protected int setInterval(int interval) {
        this.repInterval = interval;
        return this.repInterval;
    }
}