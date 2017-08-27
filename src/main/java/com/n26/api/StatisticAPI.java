package com.n26.api;

import com.n26.domain.Statistic;
import com.n26.service.ServiceTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@RestController
public class StatisticAPI {
    private final Logger log = LoggerFactory.getLogger(StatisticAPI.class);
    public final ServiceTransaction serviceTransaction;

    public StatisticAPI(ServiceTransaction serviceTransaction) {
        this.serviceTransaction = serviceTransaction;
    }

    @GetMapping("/statistics")
    public ResponseEntity getStatistics() throws URISyntaxException {
        log.debug("create transaction");
        long utcDate = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
        Statistic statistic = serviceTransaction.getStat(utcDate);
        if( statistic == null ) {
            return ResponseEntity.badRequest().body("there is no transactions in last 60 seconds");
        }
        return ResponseEntity.ok(statistic);
    }
}
