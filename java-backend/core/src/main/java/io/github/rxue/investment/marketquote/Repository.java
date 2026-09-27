package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.Metric;
import io.github.rxue.investment.vo.MetricValues;

import java.util.*;

public interface Repository {

    List<MetricValues> getMetrics(Collection<String> securityIds, Collection<Metric> requiredMetrics);

}
