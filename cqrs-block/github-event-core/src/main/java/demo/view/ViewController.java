package demo.view;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class ViewController {

    private final ViewRepository viewRepository;

    public ViewController(ViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    @RequestMapping(path = "/views/{viewName}/{id}")
    public ResponseEntity getQueryView(@PathVariable String viewName, @PathVariable String id) {
        return Optional.ofNullable(viewRepository.findOne(String.format("%s_%s", viewName, id)))
                .map(e -> new ResponseEntity<>(e.getModel(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/views/{viewName}")
    public ResponseEntity getQueryViews(@PathVariable String viewName) {
        return Optional.ofNullable(viewRepository.findViewsByViewName(viewName))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
