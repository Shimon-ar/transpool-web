package org.transpool.engine.ds;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private int balance;
    private List<ActionAccount> historyAction;

    public Account() {
        balance = 0;
        historyAction = new ArrayList<>();
    }

    public void addAction(String action,int amount,String time){
        int amountAfter;
        if(action.equals("Payment"))
            amountAfter = balance - amount;
        else {
            amountAfter = balance + amount;
        }
        historyAction.add(new ActionAccount(action,amount,time,balance,amountAfter));
        balance = amountAfter;
    }

    public int getBalance() {
        return balance;
    }

    public List<ActionAccount> getHistoryAction() {
        return historyAction;
    }
}
