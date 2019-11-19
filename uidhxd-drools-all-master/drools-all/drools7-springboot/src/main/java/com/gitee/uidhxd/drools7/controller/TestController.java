package com.gitee.uidhxd.drools7.controller;

import javax.annotation.Resource;

import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gitee.uidhxd.drools7.entity.Address;
import com.gitee.uidhxd.drools7.entity.AddressResult;

/**
 * Created by hxd on 2019/06/24
 */
@RequestMapping("/test")
@Controller
public class TestController {

    @Resource
    private KieSession kieSession;

    @ResponseBody
    @RequestMapping("/address")
    public void test(){
        Address address = new Address();
        address.setPostcode("99425");

        AddressResult result = new AddressResult();
        kieSession.insert(address);
        kieSession.insert(result);
        //kieSession.execute(null);
        int ruleFiredCount = kieSession.fireAllRules();
        
        System.out.println("触发了" + ruleFiredCount + "条规则");

        if(result.isPostCodeResult()){
            System.out.println("规则校验通过");
        }
        
    }
}
