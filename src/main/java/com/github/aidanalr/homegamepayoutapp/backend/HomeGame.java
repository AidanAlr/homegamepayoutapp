package com.github.aidanalr.homegamepayoutapp.backend;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeGame {

    ArrayList<Player> participants = new ArrayList<>();
    DecimalFormat df = new DecimalFormat("#.00");

    public void addPlayer(Player newPlayer) {
        participants.add(newPlayer);
    }

    public float getTotalBuyin() {
        float totalBuyin = 0;

        for (Player player : this.participants) {
            totalBuyin += player.getBuyIn();
        }

        return totalBuyin;
    }

    public float getTotalCashout() {
        float totalCashout = 0;

        for (Player player : this.participants) {
            totalCashout += player.getCashOut();
        }

        return totalCashout;
    }

    public boolean totalBuyinEqualsCashout() {
        System.out.printf("Total Buyin: %s\nTotal Cashout: %s\n", getTotalBuyin(), getTotalCashout());
        return getTotalBuyin() == getTotalCashout();
    }

    public ArrayList<Player> getSortedParticipants() {
        participants.sort(Player.profitComparator);
        return participants;

    }

    private ArrayList<Player> getArrayListOfWinners(ArrayList<Player> players) {
        ArrayList<Player> winners = new ArrayList<>();

        for (Player p : players) {
            if (p.getProfit() >= 0) {
                winners.add(p);
            }
        }
        return winners;
    }

    private ArrayList<Player> getArrayListOfLosers(ArrayList<Player> players) {
        ArrayList<Player> losers = new ArrayList<>();

        for (Player p : players) {
            if (p.getProfit() < 0) {
                losers.add(p);
            }
        }
        return losers;
    }

    private float getTotalProfit(ArrayList<Player> players){
        float totalProfit = 0;
        for (Player p : players) {
            totalProfit += p.getProfit();
        }
        return totalProfit;
    }

    private String getPayoutString(Player p1, Player p2, float payout) {
        return String.format("%s pays %s %s", p1.getName(), p2.getName(), df.format(payout));
    }

    private void adjustProfitForWinnersToCompensateForMissingFunds(){
        ArrayList<Player> players = getSortedParticipants();
        float missingFunds = getTotalCashout() - getTotalBuyin();

        System.out.println("Missing Funds: " + missingFunds);

        ArrayList<Player> winners = getArrayListOfWinners(players);
        float totalWinnersProfit = getTotalProfit(winners);

        for (Player p : winners) {
            float amountToAdjust;
            float percentageOfTotalWinnersProfit = p.getProfit() / totalWinnersProfit;

            if (p == winners.getLast())
            {
                amountToAdjust = missingFunds;
            }
            else
            {
                amountToAdjust = missingFunds * percentageOfTotalWinnersProfit;
            }
            // Adjust the profit
            p.setProfit(p.getProfit() - amountToAdjust);
            missingFunds -= amountToAdjust;
            System.out.println("Subtracted: " + df.format(amountToAdjust) + " from " + p.getName() + "'s profit");
        }

    }

    public ArrayList<String> calculatePayouts() {
        ArrayList<Player> players = getSortedParticipants();

        if (players.size() < 2) {
            System.out.println("Not enough players to calculate payouts for");
            return null;
        }

//        if (!totalBuyinEqualsCashout()) {
//            System.out.println("Total Buyin does not equal Total Cashout");
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Would you like to adjust the profits to compensate for missing funds? (y/n)");
//            String response = scanner.nextLine();
//            if (response.equals("y")) {
//                adjustProfitForWinnersToCompensateForMissingFunds();
//            }
//            else {
//                System.out.println("Exiting program");
//                return;
//            }
//        }

        ArrayList<Player> winners = getArrayListOfWinners(players);
        ArrayList<Player> losers = getArrayListOfLosers(players);

        ArrayList<String> payouts = new ArrayList<>();

        while (!winners.isEmpty() && !losers.isEmpty()) {
            Player winner = winners.getFirst();
            Player loser = losers.getLast();
            float loserAbsoluteLoss = loser.getProfit() * -1;

            // If the winner's profit is greater than or equal to the loser's profit
            if (winner.getProfit() >= loserAbsoluteLoss) {
                // All of this losers losses are given to the winner
                // The winner has received his winnings from this loser so subtract the amount from his profit
                // The loser has paid his losses to the winner so set his profit to 0
                payouts.add(getPayoutString(loser, winner, loserAbsoluteLoss));
                winner.setProfit(winner.getProfit() - loserAbsoluteLoss);
                loser.setProfit(0);
            }
            // If the winner's profit is less than the loser's absolute loss
            else if (winner.getProfit() < loserAbsoluteLoss) {
                // The winner is paid solely from the loser's loss
                // Adjust the losers debt to reflect the amount paid to the winner
                // The winner has been paid in full so set his profit to 0
                payouts.add(getPayoutString(loser, winner, winner.getProfit()));
                loser.setProfit(loser.getProfit() + winner.getProfit());
                winner.setProfit(0);
            }
            if (winner.getProfit() == 0) winners.removeFirst();
            if (loser.getProfit() == 0) losers.removeLast();
        }

        return payouts;


    }

}
