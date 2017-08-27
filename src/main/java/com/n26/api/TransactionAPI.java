package com.n26.api;

import com.n26.domain.Transaction;
import com.n26.dto.TransactionDTO;
import com.n26.repository.TransactionRepository;
import com.n26.service.ServiceTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

@RestController
public class TransactionAPI {
    private final Logger log = LoggerFactory.getLogger(TransactionAPI.class);
    public final TransactionRepository transactionRepository;
    public final ServiceTransaction serviceTransaction;


    public TransactionAPI(TransactionRepository transactionRepository, ServiceTransaction serviceTransaction) {
        this.serviceTransaction = serviceTransaction;
        this.transactionRepository = transactionRepository;
    }

    public long getCurrentTimeStamp() {
        Instant instant = Instant.now();
        return instant.toEpochMilli();
    }

    @PostMapping("/transactions")
    public ResponseEntity createTransaction(@Valid @RequestBody TransactionDTO transactionDTO,
                                            BindingResult bindingResult) throws URISyntaxException {
        log.debug("create transaction");
        if(bindingResult.hasErrors()) {
            String fieldName = bindingResult.getFieldError().getField(),
                    errorMessage = bindingResult.getFieldError().getDefaultMessage(),
                    message = fieldName +" "+ errorMessage;
            return ResponseEntity.badRequest()
                    .body(message);
        }
        if(transactionDTO.getId() != null) {
            return ResponseEntity.badRequest()
                    .body("new transaction must not have an ID");
        }
        long difference = System.currentTimeMillis() - new Long(transactionDTO.getTimestamp());
        if(difference > 60000) {
            serviceTransaction.createTransaction(transactionDTO);
            return ResponseEntity.noContent().build();
        }

        Transaction transaction1 =  serviceTransaction.createTransaction(transactionDTO);
        return ResponseEntity.created(new URI("/transactions/"+transaction1.getId()))
                .body(transaction1);
    }

}
