package com.hxg.player.ui;

public interface Consumer<T> {
    void accept(T t);
}