package demo.query;

import org.springframework.stereotype.Service;

@Service
public class ProjectQueries {

    private final TightCoupling tightCoupling;

    public ProjectQueries(TightCoupling tightCoupling) {
        this.tightCoupling = tightCoupling;
    }

    public TightCoupling getTightCoupling() {
        return tightCoupling;
    }
}
