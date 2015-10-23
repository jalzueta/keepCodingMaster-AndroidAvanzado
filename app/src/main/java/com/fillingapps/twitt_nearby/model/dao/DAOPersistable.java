package com.fillingapps.twitt_nearby.model.dao;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface DAOPersistable<T> {

    public long insert(@NonNull T data);
    public void update(long id, @NonNull T data);
    public void delete(long id);
    public void delete(@NonNull T data);
    public void deleteAll();
    public @Nullable Cursor queryCursor();
    public T query(long id);

}
