import java.util.*;

public class CardGame {
    static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    static List<String> deck = new ArrayList<>();
    static List<List<String>> players = new ArrayList<>();
    static List<List<String>> collectedCards = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeDeck();
        shuffleDeck();
        distributeCards();
        printInitialHands();

        int currentTurn = 0; // Player 1 always starts the game
        System.out.println("\nStarting player for this game: Player 1");
        gameLoop(currentTurn);
    }

    private static void initializeDeck() {
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(rank + " of " + suit);
            }
        }
    }

    private static void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private static void distributeCards() {
        players.add(new ArrayList<>(deck.subList(0, 5))); // Player 1
        players.add(new ArrayList<>(deck.subList(5, 8))); // Player 2
        players.add(new ArrayList<>(deck.subList(8, 10))); // Player 3
        for (int i = 0; i < 3; i++) {
            collectedCards.add(new ArrayList<>());
        }
    }

    private static void printInitialHands() {
        for (int i = 0; i < players.size(); i++) {
            System.out.println("Player " + (i + 1) + ": " + players.get(i));
        }
    }

    private static void gameLoop(int currentTurn) {
        while (players.stream().anyMatch(hand -> !hand.isEmpty())) {
            playRound(currentTurn);
        }

        System.out.println("\nGame Over!");
        System.out.println("\nFinal Scores (Number of Cards Collected):");
        int maxCards = 0;
        List<Integer> winners = new ArrayList<>();
        for (int i = 0; i < collectedCards.size(); i++) {
            int cardsCollected = collectedCards.get(i).size();
            System.out.println("Player " + (i + 1) + ": " + cardsCollected + " cards");
            if (cardsCollected > maxCards) {
                maxCards = cardsCollected;
                winners.clear();
                winners.add(i + 1);
            } else if (cardsCollected == maxCards) {
                winners.add(i + 1);
            }
        }

        System.out.println("\nWinner(s): Player " + winners);
    }

    private static void playRound(int currentTurn) {
        List<String> playedCards = new ArrayList<>(Arrays.asList(null, null, null));
        System.out.println("\n--- New Round ---");

        for (int i = 0; i < 3; i++) {
            int playerIndex = (currentTurn + i) % 3;
            if (players.get(playerIndex).isEmpty()) {
                System.out.println("Player " + (playerIndex + 1) + " has no cards left and skips their turn.");
                continue;
            }
            System.out.println("\nPlayer " + (playerIndex + 1) + ", your cards: " + players.get(playerIndex));
            System.out.print("Play a card: ");
            String card = scanner.nextLine();
            while (!players.get(playerIndex).contains(card)) {
                System.out.println("Invalid card! Try again.");
                System.out.print("Play a card: ");
                card = scanner.nextLine();
            }
            players.get(playerIndex).remove(card);
            playedCards.set(playerIndex, card);
            System.out.println("Player " + (playerIndex + 1) + " played: " + card);
        }

        int trickWinner = determineWinner(playedCards);
        System.out.println("\nTrick Winner: Player " + (trickWinner + 1));
        for (String card : playedCards) {
            if (card != null) {
                collectedCards.get(trickWinner).add(card);
            }
        }
    }

    private static int determineWinner(List<String> playedCards) {
        String winningCard = null;
        int winner = -1;
        for (int i = 0; i < playedCards.size(); i++) {
            String card = playedCards.get(i);
            if (card != null) {
                if (winningCard == null || compareCards(card, winningCard) > 0) {
                    winningCard = card;
                    winner = i;
                }
            }
        }
        return winner;
    }

    private static int compareCards(String card1, String card2) {
        String rank1 = card1.split(" ")[0];
        String rank2 = card2.split(" ")[0];
        return Integer.compare(Arrays.asList(RANKS).indexOf(rank1), Arrays.asList(RANKS).indexOf(rank2));
    }
}
