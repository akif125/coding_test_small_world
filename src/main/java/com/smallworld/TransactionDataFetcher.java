package com.smallworld;

import com.smallworld.data.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionDataFetcher {
    private JSONParser jsonParser;
    private List<Transaction> transactionList;

    public TransactionDataFetcher() {
        jsonParser = new JSONParser();
        transactionList = new ArrayList<>();
    }

    private void loadData() {
        try (FileReader reader = new FileReader("transactions.json")) {
            Object obj = jsonParser.parse(reader);
            JSONArray transactionJsonArray = (JSONArray) obj;
            transactionJsonArray.forEach(transaction -> {
                transactionList.add(parseTransaction((JSONObject) transaction));
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction parseTransaction(JSONObject jsonObject) {
        Transaction transaction = new Transaction();
        transaction.setMtn((Long) jsonObject.get("mtn"));
        transaction.setAmount(BigDecimal.valueOf((Double) jsonObject.get("amount")));
        transaction.setSenderFullName((String) jsonObject.get("senderFullName"));
        transaction.setBeneficiaryFullName((String) jsonObject.get("beneficiaryFullName"));
        transaction.setBeneficiaryAge((Long) jsonObject.get("beneficiaryAge"));
        transaction.setIssueId((Long) jsonObject.get("issueId")!=null?(Long) jsonObject.get("issueId"):null);
        transaction.setIssueSolved(Boolean.parseBoolean(String.valueOf((Boolean) jsonObject.get("issueSolved"))));
        transaction.setBeneficiaryFullName((String) jsonObject.get("issueMessage"));
        return transaction;
    }

    /**
     * Returns the sum of the amounts of all transactions
     */
    public double getTotalTransactionAmount() {
        loadData();
        Double amount = transactionList.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).sum();
        return amount;
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public double getTotalTransactionAmountSentBy(String senderFullName) {
        loadData();
        Double amount;
        amount = transactionList.stream()
                .filter(transaction -> transaction.getSenderFullName().equals(senderFullName))
                .mapToDouble(transaction -> transaction.getAmount().doubleValue())
                .sum();
        return amount;
    }

    /**
     * Returns the highest transaction amount
     */
    public double getMaxTransactionAmount() {
        loadData();
        double amount = transactionList.stream()
                .mapToDouble(transaction -> transaction.getAmount().doubleValue())
                .max()
                .orElse(Double.MIN_VALUE);
        return amount;
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients() {
        loadData();
        Long uniqueClients = transactionList.stream()
                .map(Transaction::getSenderFullName)
                .distinct()
                .count();
        return uniqueClients;
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    public boolean hasOpenComplianceIssues(String clientFullName) {
        loadData();
        Long issueCount = transactionList.stream()
                .filter(transaction -> transaction.getSenderFullName().equals(clientFullName) || transaction.getBeneficiaryFullName().equals(clientFullName))
                .filter(transaction -> transaction.getIssueId() != null && !transaction.getIssueSolved())
                .count();
        return issueCount > 0;
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, Transaction> getTransactionsByBeneficiaryName() {
        loadData();
        return transactionList.stream()
                .collect(Collectors.toMap(Transaction::getBeneficiaryFullName, Function.identity()));
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Long> getUnsolvedIssueIds() {
        loadData();
        return transactionList.stream()
                .filter(transaction -> transaction.getIssueId() != null && !transaction.getIssueSolved())
                .map(Transaction::getIssueId)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages() {
        loadData();
        return transactionList.stream()
                .map(Transaction::getIssueMessage)
                .collect(Collectors.toList());
    }

    /**
     * Returns the 3 transactions with the highest amount sorted by amount descending
     */
    public List<Transaction> getTop3TransactionsByAmount() {
        loadData();
        return transactionList.stream()
                .sorted(Comparator.comparing(Transaction::getAmount, Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * Returns the senderFullName of the sender with the most total sent amount
     */
    public Optional<String> getTopSender() {
        loadData();
        Map<String, Long> senderCount = transactionList.stream()
                .collect(Collectors.groupingBy(Transaction::getSenderFullName, Collectors.counting()));

        return senderCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

}
