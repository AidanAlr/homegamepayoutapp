package com.github.aidanalr.homegamepayoutapp.backend;

import java.util.Comparator;

public class Player {
    private String name;
    private float buyIn;
    private float cashOut;
    private float profit;

    public Player(String name, float buyIn, float cashOut) {
        setCashOut(cashOut);
        setBuyIn(buyIn);
        setName(name);
        setProfit(cashOut - buyIn);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBuyIn() {
        return buyIn;
    }

    public void setBuyIn(float buyIn) {
        this.buyIn = buyIn;
    }

    public float getCashOut() {
        return cashOut;
    }

    public void setCashOut(float cashOut) {
        this.cashOut = cashOut;
    }

    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public static Comparator<Player> profitComparator = new Comparator<Player>() {
        @Override
        public int compare(Player p1, Player p2) {
            return (int) (p2.getProfit() - p1.getProfit());
        }
    };

    @Override
    public String toString() {
        return getName() + " " + getProfit();
    }
}
