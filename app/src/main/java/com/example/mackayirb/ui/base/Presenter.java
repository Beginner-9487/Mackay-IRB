package com.example.mackayirb.ui.base;

/**
 *
 */
public interface Presenter<V extends MvpView> {
    void attachView(V mvpView);

    void detachView();
}
