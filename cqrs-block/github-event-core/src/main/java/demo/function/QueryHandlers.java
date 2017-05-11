package demo.function;

import demo.query.TightCouplingQuery;
import org.springframework.stereotype.Service;

@Service
public class QueryHandlers {

    private final TightCouplingQuery tightCouplingQuery;

    public QueryHandlers(TightCouplingQuery tightCouplingQuery) {
        this.tightCouplingQuery = tightCouplingQuery;
    }

    public TightCouplingQuery getTightCouplingQuery() {
        return tightCouplingQuery;
    }
}
