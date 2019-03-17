package com.wuxinming.crudgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.util.List;

import static com.wuxinming.crudgenerator.CRUDControllerImpl.MODULE_PATH;

@RequestMapping(value = MODULE_PATH)
public class CRUDControllerImpl<T> implements CRUDController<T> {

    public static final String CRUD_ITEM_PATH = "[curd-item-path]";
    public static final String MODULE_PATH = "[module-path]";

    @Autowired
    protected CRUDService<T> crudService;

    @RequestMapping(value = CRUD_ITEM_PATH, method = RequestMethod.GET)
    public List<T> selectList() {
        return crudService.selectList();
    }

    @RequestMapping(value = CRUD_ITEM_PATH + "/{id}", method = RequestMethod.GET)
    public T selectById(@PathVariable("id") Serializable id) {
        return crudService.selectById(id);
    }

    @RequestMapping(value = CRUD_ITEM_PATH + "/{id}", method = RequestMethod.DELETE)
    @Override
    public int deleteById(@PathVariable("id") Serializable id) {
        return crudService.deleteById(id);
    }

    @RequestMapping(value = CRUD_ITEM_PATH, method = RequestMethod.POST)
    @Override
    public int insert(T t) {
        return crudService.insert(t);
    }
}
