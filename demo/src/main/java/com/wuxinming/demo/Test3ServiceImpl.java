package com.wuxinming.demo;

import com.wuxinming.crudgenerator.CRUDServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Test3ServiceImpl extends CRUDServiceImpl<Test3>{

    @Autowired
    protected Test3DAO baseMapper;

    @Override
    public List<Test3> selectList() {
        List<Test3> test3s = this.baseMapper.selectList(null);
        test3s.get(0).setName2("aaaaaaaaaaa");
        return test3s;
    }
}
