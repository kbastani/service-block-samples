package demo.view;

import demo.query.QueryEventRepository;
import demo.query.TightCouplingEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.time.Duration;
import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class ViewController {

    private final ViewRepository viewRepository;
    private final QueryEventRepository eventRepository;

    public ViewController(ViewRepository viewRepository, QueryEventRepository eventRepository) {
        this.viewRepository = viewRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(path = "/views/{viewName}/{id}")
    public ResponseEntity getQueryView(@PathVariable String viewName, @PathVariable String id) {
        return Optional.ofNullable(viewRepository.findById(id))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/views/{viewName}")
    public ResponseEntity getQueryViews(@PathVariable String viewName) {
        return Optional.ofNullable(viewRepository.findViewsByViewName(viewName))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/projects/{projectId}/tightCouplingEvents", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<TightCouplingEvent>> streamTightCouplingEvents(@PathVariable Long projectId) {
        ReplayProcessor<ServerSentEvent<TightCouplingEvent>> replayProcessor =
                ReplayProcessor.createTimeout(Duration.ofDays(Integer.MAX_VALUE));

        eventRepository.findByProjectId(projectId)
                .map(s -> ServerSentEvent.builder(s)
                        .event(s.getCreatedDate().toString())
                        .id(s.getId())
                        .retry(Duration.ZERO)
                        .build())
                .subscribe(replayProcessor::onNext);

        return replayProcessor;
    }
}
