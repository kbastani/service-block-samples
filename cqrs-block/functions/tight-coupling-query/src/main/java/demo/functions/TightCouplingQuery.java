package demo.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.functions.project.Commit;
import demo.functions.project.Project;
import demo.functions.project.ProjectEvent;
import demo.functions.project.ProjectEventParam;
import demo.functions.view.View;
import demo.functions.view.ViewRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@SpringBootApplication
public class TightCouplingQuery {

    public static void main(String[] args) {
        SpringApplication.run(TightCouplingQuery.class, args);
    }

    @Bean
    public Function<ProjectEventParam, Map<String, Object>> function(ViewRepository viewRepository) {
        return projectEventParam -> {
            Project project = projectEventParam.getProject();
            ProjectEvent event = projectEventParam.getProjectEvent();
            Map<String, Object> map = new HashMap<>();

            // Get commits from payload
            if (event.getPayload().containsKey("commit")) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Commit commit = objectMapper.readValue(
                            objectMapper.writeValueAsString(event.getPayload().get("commit")),
                            Commit.class);

                    if (commit != null) {
                        View view = Stream.of(commit)
                                .map(c -> {
                                    View v = new View("tcq_" + project.getName());
                                    map.put("commit", commit);
                                    v.setModel(map);
                                    return v;
                                }).findFirst().get();
                        viewRepository.save(view);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return map;
        };
    }
}
