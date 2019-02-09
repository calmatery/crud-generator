package com.wuxinming.crudgenerator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public class CRUDServiceImpl<T> implements CRUDService<T> {

    @Autowired
    protected BaseMapper<T> baseMapper;

    @Override
    public List<T> selectList() {
        return baseMapper.selectList(null);
    }

    @Override
    public T selectById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    public int deleteById(Serializable id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public int insert(T t) {
        return baseMapper.insert(t);
    }
}
