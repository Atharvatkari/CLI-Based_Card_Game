const readline = require('readline');
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

// Deck initialization
let deck = [];
const suits = ['Hearts', 'Diamonds', 'Clubs', 'Spades'];
const ranks = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K', 'A'];

suits.forEach((suit) => {
  ranks.forEach((rank) => {
    deck.push(`${rank} of ${suit}`);
  });
});

// Shuffle deck
deck.sort(() => Math.random() - 0.5);

// Function to distribute cards
function distributeCards() {
  const player1 = deck.splice(0, 5);
  const player2 = deck.splice(0, 3);
  const player3 = deck.splice(0, 2);
  return [player1, player2, player3];
}

// Players' hands and scores
let players = distributeCards();
let collectedCards = [[], [], []]; // Array to store collected cards for each player

console.log('Initial Hands:');
console.log('Player 1:', players[0]);
console.log('Player 2:', players[1]);
console.log('Player 3:', players[2]);

// Function to determine the winner of a trick
function determineWinner(cardsPlayed) {
  // Filter out null values to avoid errors
  const validCards = cardsPlayed.filter((card) => card !== null);

  let highestRank = -1;
  let winnerIndex = -1;

  validCards.forEach((card, index) => {
    const rank = ranks.indexOf(card.split(' ')[0]); // Get rank index
    if (rank > highestRank) {
      highestRank = rank;
      winnerIndex = cardsPlayed.indexOf(card); // Find the original player index
    }
  });

  return winnerIndex;
}

// Function to play a turn
function playTurn(player, playerName, playedCards, nextPlayerCallback) {
  if (player.length === 0) {
    console.log(`${playerName} has no cards left and skips their turn.`);
    playedCards.push(null); // Add null for skipped players
    nextPlayerCallback(); // Skip to the next player
    return;
  }

  console.log(`\n${playerName}, your cards: ${player.join(', ')}`);
  rl.question('Play a card: ', (card) => {
    if (!player.includes(card)) {
      console.log('Invalid card! Try again.');
      playTurn(player, playerName, playedCards, nextPlayerCallback); // Replay turn
    } else {
      player.splice(player.indexOf(card), 1); // Remove played card from hand
      playedCards.push(card);
      console.log(`${playerName} played: ${card}`);
      nextPlayerCallback();
    }
  });
}

// Game loop
let currentTurn = Math.floor(Math.random() * 3); // Randomly choose the starting player
function gameLoop() {
  let playedCards = []; // Cards played in the current round

  function playRound(playerIndex) {
    if (playerIndex === 3) {
      // All players have played their cards; determine the winner
      const winnerIndex = determineWinner(playedCards);
      console.log(`\nTrick Winner: Player ${winnerIndex + 1}`);
      collectedCards[winnerIndex].push(...playedCards.filter(Boolean)); // Collect cards

      console.log('\nCards Collected:');
      console.log('Player 1:', collectedCards[0]);
      console.log('Player 2:', collectedCards[1]);
      console.log('Player 3:', collectedCards[2]);

      // Check if the game is over
      if (players.every((player) => player.length === 0)) {
        console.log('\nGame Over!');
        console.log('\nFinal Scores (Number of Cards Collected):');
        collectedCards.forEach((cards, i) => {
          console.log(`Player ${i + 1}: ${cards.length} cards`);
        });
        const maxCards = Math.max(...collectedCards.map((cards) => cards.length));
        const winners = collectedCards
          .map((cards, i) => (cards.length === maxCards ? i + 1 : null))
          .filter(Boolean);

        console.log(`\nWinner(s): Player ${winners.join(', ')}`);
        rl.close();
      } else {
        // Start the next round with the winner of the trick
        currentTurn = winnerIndex;
        gameLoop();
      }
    } else {
      // Continue the current round
      playTurn(players[playerIndex], `Player ${playerIndex + 1}`, playedCards, () => {
        playRound(playerIndex + 1);
      });
    }
  }

  playRound(0); // Start the round with the first player
}

// Start the first round
console.log(`\nStarting player for this game: Player ${currentTurn + 1}`);
gameLoop();
