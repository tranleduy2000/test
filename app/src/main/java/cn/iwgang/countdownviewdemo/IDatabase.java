package cn.iwgang.countdownviewdemo;

import java.util.List;

interface IDatabase<E> {
    List<E> readAll();

    void add(E e);

    void remove(E e);

    void update(E e);
}
