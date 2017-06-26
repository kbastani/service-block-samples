package demo.functions;

import demo.functions.event.TightCouplingEvent;
import demo.functions.project.Commit;
import demo.functions.project.Project;
import demo.functions.project.ProjectEvent;
import demo.functions.project.ProjectEventParam;
import demo.functions.view.View;
import demo.functions.query.TightCouplingProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SpringBootApplication
public class MetricsFunction {

    public static void main(String[] args) {
        SpringApplication.run(MetricsFunction.class, args);
    }

    @Bean
    public Function<ProjectEventParam, Map<String, Object>> function(MongoTemplate template) {
        return projectEventParam -> {
            // Extract parameters from the event before processing
            Map<String, Object> result = new HashMap<>();
            result.put("updated", new ArrayList<String>());
            result.put("inserted", new ArrayList<String>());
            result.put("events", new ArrayList<TightCouplingEvent>());

            Project project = projectEventParam.getProject();
            ProjectEvent event = projectEventParam.getProjectEvent();
            Commit commit = event.getPayload().getOrDefault("commit", null);

            // If a commit exists and has files, create a view processor to generate query model updates
            if (commit != null) {
                TightCouplingProcessor tightCouplingProcessor = new TightCouplingProcessor(event, project, commit);
                List<View> viewList = tightCouplingProcessor.generateView();

                // Insert or update query models produced from the event
                upsertViewList(viewList, template, result);
            }

            // Returns back a list of inserted and updated keys
            return result;
        };
    }

    @Transactional
    private void upsertViewList(List<View> viewList, MongoTemplate template, Map<String, Object> result) {
        viewList.forEach(view -> {
            // Find the document if it exists, if not, insert a new one
            Query updateQuery = new Query(Criteria.where("_id").is(view.getId()));

            // Increment the match count for the coupled files
            Update update = new Update().inc("matches", 1).set("lastModified", view.getLastModified());

            // Apply the increment or insert a new document
            View viewResult = template.findAndModify(updateQuery, update,
                    new FindAndModifyOptions().returnNew(true).upsert(true), View.class);

            // Apply properties of a new view if the document was just inserted
            if (viewResult.getMatches() <= 1) {
                template.save(view);
                // Keep track of inserts and updates
                ((List<String>) result.get("inserted")).add(view.getId());
            } else {
                ((List<String>) result.get("updated")).add(view.getId());
                if(viewResult.getMatches() >= 2) {
                    ((List<TightCouplingEvent>) result.get("events"))
                            .add(new TightCouplingEvent(view.getProjectId(), viewResult));
                }
            }
        });
    }
}
