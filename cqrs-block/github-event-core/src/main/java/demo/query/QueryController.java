package demo.query;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class QueryController {

    private final QueryRepository queryRepository;

    public QueryController(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @RequestMapping(path = "/queries/{viewName}/{id}")
    public ResponseEntity getQueryView(@PathVariable String viewName, @PathVariable String id) {
        return Optional.ofNullable(queryRepository.findOne(String.format("%s_%s", viewName, id)))
                .map(e -> new ResponseEntity<>(e.getModel(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/queries/{viewName}")
    public ResponseEntity getQueryViews(@PathVariable String viewName) {
        return Optional.ofNullable(queryRepository.findQueryModelsByViewName(viewName))
                .map(e -> new ResponseEntity<>(e.stream().map(QueryModel::getModel), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
