package com.n26.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

public class TransactionDTO {

    @Id
    private String id;

    @NotEmpty @NotNull
    private String amount;

    @NotEmpty @NotNull
    private String timestamp;

    @JsonIgnore
    private String transactionName;

    public TransactionDTO(String id, String amount, String timestamp) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionName = "transaction";
    }

    public TransactionDTO() {
        this.transactionName = "transaction";
    }
    public TransactionDTO(String amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }
}
