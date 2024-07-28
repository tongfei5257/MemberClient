package com.example.memberclient;

import com.example.memberclient.model.Source;

public interface ISaveCallback<T> {
    void onSave(T source);

}
