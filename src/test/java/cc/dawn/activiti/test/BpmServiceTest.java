package cc.dawn.activiti.test;

import dawn.activiti.Application;
import dawn.activiti.BpmService;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class BpmServiceTest {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmService bpmService;

    Map<String,User> userMap = new HashMap<>();

    private ProcessInstance lastPi;

    @Before
    public void init(){
        // install groups & users
        Group group = identityService.newGroup("user");
        group.setName("users");
        group.setType("security-role");
        identityService.saveGroup(group);

        User emp = identityService.newUser("emp");
        emp.setFirstName("Joram");
        emp.setLastName("Barrez");
        emp.setPassword("emp");
        identityService.saveUser(emp);

        User manager = identityService.newUser("manager");
        manager.setFirstName("Josh");
        manager.setLastName("Long");
        manager.setPassword("manager");
        identityService.saveUser(manager);


        User director = identityService.newUser("director");
        director.setFirstName("director");
        director.setLastName("Guo");
        director.setPassword("director");
        identityService.saveUser(director);

        identityService.createMembership("emp", "user");
        identityService.createMembership("manager", "user");
        identityService.createMembership("director", "user");

        userMap.put("emp",emp);
        userMap.put("manager",manager);
        userMap.put("director",director);
    }




    @Test
    @Transactional
    public void test_1(){

        Map<String, Object> vars = new HashMap();
        vars.put("days", 6L);


        ProcessInstance pi = bpmService.startByLastVersion("vacation", UUID.randomUUID().toString(),"emp", vars);
        List<Task> managerTask = bpmService.getTodoList("manager",pi.getBusinessKey());
        bpmService.complete("manager",managerTask.get(0).getId(),new Object[]{"agreed",Boolean.TRUE});
        List<Task> directorTask = bpmService.getTodoList("director",pi.getBusinessKey());
        Assert.assertTrue(directorTask.size() == 1);


        pi = bpmService.startByLastVersion("vacation", UUID.randomUUID().toString(),"emp", vars);
        managerTask = bpmService.getTodoList("manager",pi.getBusinessKey());
        bpmService.complete("manager",managerTask.get(0).getId(),new Object[]{"agreed",Boolean.FALSE});
        directorTask = bpmService.getTodoList("director",pi.getBusinessKey());
        Assert.assertTrue(directorTask.size() == 0);

        runtimeService.deleteProcessInstance(pi.getProcessInstanceId(),"cancel");


        vars.put("days", 5L);
        pi = bpmService.startByLastVersion("vacation", UUID.randomUUID().toString(),"emp", vars);
        managerTask = bpmService.getTodoList("manager",pi.getBusinessKey());
        bpmService.complete("manager",managerTask.get(0).getId(),new Object[]{"agreed",Boolean.TRUE});
        directorTask = bpmService.getTodoList("director",pi.getBusinessKey());
        Assert.assertTrue(directorTask.size() == 0);


        List<HistoricProcessInstance> historicProcessInstances = bpmService.getHistoricProcessInstance("emp");

        Assert.assertTrue(historicProcessInstances.size() == 3);
        Assert.assertTrue(bpmService.getFinishedHistoricProcessInstance("emp").size() == 2);
        Assert.assertTrue(bpmService.getUnFinishedHistoricProcessInstance("emp").size() == 1);
    }

}
