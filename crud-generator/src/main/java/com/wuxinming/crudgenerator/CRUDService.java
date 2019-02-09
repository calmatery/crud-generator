package com.wuxinming.crudgenerator;

import java.io.Serializable;
import java.util.List;

public interface CRUDService<T> {
    List<T> selectList();

    T selectById(Serializable id);

    int deleteById(Serializable id);

    int insert(T t);
}
