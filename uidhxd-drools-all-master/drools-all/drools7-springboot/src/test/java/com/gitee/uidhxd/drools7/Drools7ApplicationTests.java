package com.gitee.uidhxd.drools7;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gitee.uidhxd.drools7.entity.Address;
import com.gitee.uidhxd.drools7.entity.AddressResult;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Drools7ApplicationTests {
	
    @Resource
    private KieSession kieSession;
    
	@Test
	public void contextLoads() {
		Address address = new Address();
        address.setPostcode("99425");
        AddressResult result = new AddressResult();
        
		kieSession.insert(address);
		kieSession.insert(result);
		kieSession.fireAllRules();
		int ruleFiredCount = kieSession.fireAllRules();
        
        System.out.println("触发了" + ruleFiredCount + "条规则");

        if(result.isPostCodeResult()){
            System.out.println("规则校验通过");
        }
	}

}
