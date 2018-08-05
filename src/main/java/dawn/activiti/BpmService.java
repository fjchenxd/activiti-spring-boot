package dawn.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.management.openmbean.TabularData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BpmService {


    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected IdentityService identityService;


    @Transactional(propagation=Propagation.REQUIRED,readOnly = false)
    public ProcessInstance startByLastVersion(String processDefKey, String businessKey,String assignee, Map<String,Object> vars){
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefKey).latestVersion().singleResult();
        identityService.setAuthenticatedUserId(assignee);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDef.getId(), businessKey, vars);
        identityService.setAuthenticatedUserId(null);
        return processInstance;
    }

    /**
     * 获取用户待办列表
     * @param userId
     * @return
     */

    @Transactional(propagation=Propagation.NEVER,readOnly = true)
    public List<Task> getTodoList(String userId) {
        return taskService.createTaskQuery().taskCandidateOrAssigned(userId).active().orderByTaskCreateTime().desc().list();
    }

    /**
     * 获取用户指定业务单待办列表
     * @param userId
     * @param businessKey
     * @return
     */
    @Transactional(readOnly = true)
    public List<Task> getTodoList(String userId, String businessKey) {
        return taskService.createTaskQuery().taskCandidateOrAssigned(userId).processInstanceBusinessKey(businessKey).active().orderByTaskCreateTime().desc().list();
    }


    /**
     * 完成某项任务
     * @param userId
     * @param taskId
     * @param vars
     *
     *
     */
    @Transactional(propagation=Propagation.REQUIRED,readOnly = false)
    public void complete(String userId, String taskId, Map<String,Object> vars) {
        Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(userId).active().singleResult();
        identityService.setAuthenticatedUserId(userId);
        taskService.complete(taskId,vars);
        identityService.setAuthenticatedUserId(null);
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly = false)
    public void complete(String userId, String taskId, Object[]... varKeyValue) {
        HashMap<String, Object> vars = new HashMap<>();
        for(Object[] kv : varKeyValue){
            vars.put(kv[0].toString(),kv[1]);
        }
        complete(userId,taskId,vars);
    }


    /**
     * 查找用户历史发起的流程实例
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<HistoricProcessInstance> getHistoricProcessInstance(String userId){
        return historyService.createHistoricProcessInstanceQuery().startedBy(userId).orderByProcessInstanceStartTime().desc().list();
    }

    /**
     * 查询用户发起的已关闭流程实例
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<HistoricProcessInstance> getFinishedHistoricProcessInstance(String userId){
        return historyService.createHistoricProcessInstanceQuery().startedBy(userId).finished().orderByProcessInstanceStartTime().desc().list();
    }

    @Transactional(readOnly = true)
    public List<HistoricProcessInstance> getUnFinishedHistoricProcessInstance(String userId){
        return historyService.createHistoricProcessInstanceQuery().startedBy(userId).unfinished().orderByProcessInstanceStartTime().desc().list();
    }
}
