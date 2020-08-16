package org.transpool.engine.ds;

public class ActionAccount {
    private final Action action;
    private final int amount;
    private final String time;
    private final int amountBefore;
    private final int amountAfter;

    public ActionAccount(String action, int amount, String time, int amountBefore, int amountAfter) {
        this.action = Action.valueOf(action);
        this.amount = amount;
        this.time = time;
        this.amountBefore = amountBefore;
        this.amountAfter = amountAfter;
    }

    public String getAction() {
        return action.name();
    }

    public int getAmount() {
        return amount;
    }

    public String getTime() {
        return time;
    }

    public int getAmountBefore() {
        return amountBefore;
    }

    public int getAmountAfter() {
        return amountAfter;
    }

    public enum Action{
        Deposit,Payment,Receive
    }


}
