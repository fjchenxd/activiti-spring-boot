package dawn.activiti;

import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Demonstrates the <A href="http://localhost:8080/">REST API</A>
 */

@EnableTransactionManagement
@SpringBootApplication
@AutoConfigureAfter
public class Application {

    @Bean
    InitializingBean usersAndGroupsInitializer(final IdentityService identityService, final RepositoryService repositoryService,
                                               final TaskService taskService, final RuntimeService runtimeService,
                                               final HistoryService historyService) {

        return new InitializingBean() {
            @Override
            public void afterPropertiesSet() throws Exception {

            }
        };
    }

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }
}