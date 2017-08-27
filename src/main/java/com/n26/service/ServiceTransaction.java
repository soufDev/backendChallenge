package com.n26.service;

import com.n26.domain.Statistic;
import com.n26.domain.Transaction;
import com.n26.dto.TransactionDTO;
import com.n26.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTransaction {

    public final TransactionRepository transactionRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public ServiceTransaction(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new Double(transactionDTO.getAmount()));
        transaction.setTimestamp(new Long(transactionDTO.getTimestamp()));
        return transactionRepository.save(transaction);
    }

    public Statistic getStat(double timestamp) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").lte(timestamp).gte(timestamp-60000)),
                Aggregation.group("transactionName").sum("amount").as("sum")
                        .avg("amount").as("avg")
                        .max("amount").as("max")
                        .min("amount").as("min")
                        .count().as("count")
        );
        AggregationResults<Statistic> groupResults = mongoTemplate.aggregate(agg, Transaction.class, Statistic.class);
        return groupResults.getUniqueMappedResult();
    }
}
