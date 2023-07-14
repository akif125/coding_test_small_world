package com.smallworld.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction {
        private Long mtn;
        private BigDecimal amount;
        private String senderFullName;
        private String beneficiaryFullName;
        private Long beneficiaryAge;
        private Long issueId;
        private Boolean issueSolved;
        private String issueMessage;
}
