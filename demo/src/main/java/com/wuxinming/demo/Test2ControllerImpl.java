package com.wuxinming.demo;

import com.wuxinming.crudgenerator.CRUDControllerImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ttt")
public class Test2ControllerImpl extends CRUDControllerImpl<Test2>{


//    @Autowired
//    CRUDService<Test2> crudService2;
//
//    @Autowired
//    CRUDService<Test3> crudService3;
//    @Override
//    @RequestMapping(value = "Test2",method = RequestMethod.GET)
//    public List<Test2> selectList(){
//        System.out.println(this.crudService.selectList());
//        return crudService.selectList();
//    }

}
