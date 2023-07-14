package com.smallworld;

public class main {
    public static void main(String[] args){
        TransactionDataFetcher transactionDataFetcher = new TransactionDataFetcher();
        System.out.println(transactionDataFetcher.getTop3TransactionsByAmount());
    }
}
