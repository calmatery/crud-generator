package com.wuxinming.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface Test3DAO extends BaseMapper<Test3>{
    Test3 selectByName2(String name2);
}
