package schuchardt.evin.poker;

import java.util.Random;

public class DeckState {
	/*
	 * diamonds, clubs, hearts, spades
	 */
	private static final int FOLD = 0;
	private static final int STAY = 1;
	
	private static final int FALSE = 0;
	private static final int TRUE = 1;
	
	//Hand values
	private static final int CARD_HIGH = 0;
	private static final int PAIR = 1;
	private static final int TWO_PAIR = 2;
	private static final int THREE_OF_A_KIND = 3;
	private static final int FOUR_OF_A_KIND = 4;
	private static final int STRAIGHT = 5;
	private static final int FLUSH = 6;
	private static final int FULL_HOUSE = 7;
	private static final int STRAIGHT_FLUSH = 8;
	
	private static final int CARDS_PER_SUIT = 13;
	
	//lower and upper bounds to deck for each suit
	private static final int DIAMONDS_LOW = 0;
	private static final int DIAMONDS_HIGH = 12;
	private static final int CLUBS_LOW = 13;
	private static final int CLUBS_HIGH = 25;
	private static final int HEARTS_LOW = 26;
	private static final int HEARTS_HIGH = 38;
	private static final int SPADES_LOW = 39;
	private static final int SPADES_HIGH = 51;
	
	//suits
	private static final int DIAMONDS = 0;
	private static final int CLUBS = 1;
	private static final int HEARTS = 2;
	private static final int SPADES = 3;
	
	//Phases for the round
    private static final int DEAL_PHASE = 0;
    private static final int BET1_PHASE = 1;
    private static final int DRAW_PHASE = 2;
    private static final int BET2_PHASE = 3;
    private static final int FINAL_PHASE = 4;
    
  //Phases for the round
    private static final String DEAL_PHASE_STRING = "Deal";
    private static final String BET1_PHASE_STRING = "Bid";
    private static final String DRAW_PHASE_STRING = "Draw";
    private static final String BET2_PHASE_STRING = "Bid";
    private static final String FINAL_PHASE_STRING = "Deal";
    private static final String BID_PHASE_STRING = "Bid";
	
	
	//int[] deck;
	int[] playerState;
	int[] playersBids;
//	int[] playersPreviousBids;
	int[] usedCards;
	int[] playersCards; //the cards of each player in order.  Do mod to find player num
	int[] playersMoney;
	int[] playerUpdate;
	int[] playersFinalHand;
	int numPlayers;
	int phase;
	int pot;
	int currentBid;
	int winningHand;
	int winningPlayer;
	Random ranGen = new Random();
	
	
	
	/** 
	 * Constructor
	 */
	DeckState() {
		//deck = new int[52];
		pot = 0;
		currentBid = 0;
		playersFinalHand = new int[4];
		initPlayersFinalHand();
		playerState = new int[4];
		initPlayerState();
		playersBids = new int[4];
		initPlayersBids();
//		playersPreviousBids = new int[4];
//		initiPlayersPreviousBids();
		playersCards = new int[20];
		usedCards = new int[52];
		playerUpdate = new int[4];
		playersMoney = new int[4];
		for(int i=0; i<playersMoney.length; i++) {
			playersMoney[i]= 200;
		}
		//for(int i=0; i<52; i++) {
		//	deck[i] = i;
		//}
	}
	
	public void initPlayersFinalHand() {
		for(int i=0; i<playersFinalHand.length; i++) {
			playersFinalHand[i] = 0;
		}
	}
	
	public void initPlayersCards() {
		for(int i=0; i<playersCards.length; i++) {
			playersCards[i] = 52;
		}
	}
	
	public void initPlayerState() {
		for(int i=0; i<playerState.length; i++) {
			playerState[i]= STAY;
		}
	}
	
	public void initPlayersBids() {
		for(int i=0; i<playersBids.length; i++) {
			playersBids[i]= 0;
		}
	}
	
//	public void initiPlayersPreviousBids() {
//		for(int i=0; i<playersPreviousBids.length; i++) {
//			playersPreviousBids[i]= 0;
//		}
//	}
	
	public void initUsedCards() {
		for(int i=0; i<usedCards.length; i++) {
			usedCards[i] = 52;
		}
	}
	
	public void initPlayerUpdate() {
		for(int i=0; i<playerUpdate.length; i++) {
			if(getPlayerState(i) == STAY) {
				playerUpdate[i] = 0;
			}
		}
	}
	
	public boolean isUpdated() {
		boolean updated = true;
		for(int i=0; i<numPlayers; i++) {
			if(playerUpdate[i] == 0) {
				updated = false;
				break;
			}
		}
		return updated;
	}
	
	/**
     * 
     * @param hand  a passed in hand of a player or used cards
     * @param card  an int that represents a given card
     * @return  true if card is legal.
     */
    public boolean checkLegalCard(int[] hand, int card) {
    	boolean acceptCard = true;
    	for(int i=0; i<hand.length; i++) {
    		if(card == hand[i]) {
    			acceptCard = false;
    			break;
    		}
    	}
    	return acceptCard;
    }
	
	/**
     * Deals the cards based upon cards that have already been dealt to other players.
     * @param hand  the hand of a player to be passed in.
     * @param usedCards  used cards from all players.
     * @return
     */
	public void dealCards(int[] hand, int[] usedCards) {
    	int cardCount = 0;
    	int ranNumber;
    	boolean acceptCard = true;
    	
    	//init cards to 0
    	for(int i=0; i<5; i++) {
    		hand[i] = 52; //52 is not a number in the deck.
    	}
    	
    	//While all the cards in the hand have not been changed...
    	while (cardCount < 5) {
    		ranNumber = ranGen.nextInt(52);
    		if(usedCards != null) {
    			//see if the number is in someone else's hand.
    			acceptCard = checkLegalCard(usedCards, ranNumber);
    		}
    		
			//check to see that the card is not already in the hand.
			if(acceptCard) {
				acceptCard = checkLegalCard(hand, ranNumber);
				if(acceptCard) {
					hand[cardCount] = ranNumber;
    				cardCount++;
				} //end if
			} //end if
			
			//reset accept true to true
    		acceptCard = true;
    	} //end while
    } //end dealCards()
	
	public int getRandomCard(int[] uCards, int[] hand) {
		boolean validCard = false;
		int ranNumber = 52;
		while (!validCard) {
    		ranNumber = ranGen.nextInt(52);
    		if(uCards != null) {
    			//see if the number is in someone else's hand.
    			validCard = checkLegalCard(uCards, ranNumber);
    		}
    		else {
    			//TODO: throw exception or something
    		}
    		
    		if(validCard) {
    			validCard = checkLegalCard(hand, ranNumber);
			} //end if
			
			//reset accept true to true
    	} //end while
		return ranNumber;
	}
	
	public int getRemainingPlayer() { //returns the winning player number.
		for(int i=0; i<numPlayers; i++) {
			if(playerState[i] == STAY) {
				winningPlayer = i;
				return i;
			}
		}
		return 52;
	}
	
	public void distributeWinnings(int playerNum) {
		for(int i=0; i<numPlayers; i++) {
			playersMoney[playerNum] = playersMoney[playerNum] + playersBids[i];
		}
	}
	
	/**
	 * Copies cards from the cards of all players and assigns only the individual
	 * hand of the player specified into hand.
	 * 
	 * @param hand  5 cards to put assigned cards into depending on playerNum
	 * @param playerNum  The number of the player
	 */
	public void getHand(int[] hand, int playerNum) {
		if(playerNum > numPlayers) {
			//TODO: throw error
		}
		int offset = (playerNum*5);
		int j = 0;
		for(int i=offset; i<offset+5; i++) {
			hand[j]=playersCards[i];
			j++;
		}
	}
	
	public void setHand(int[] hand, int playerNum) {
		if(playerNum > numPlayers) {
			//TODO: throw error
		}
		int offset = (playerNum*5);
		int j = 0;
		for(int i=offset; i<offset+5; i++) {
			playersCards[i]=hand[j];
			j++;
		}
	}
	
	public void copy(DeckState oldState, DeckState newState) { //TODO: may need to do a soft copy because of pointers.
		oldState.setUsedCards(newState.getUsedCards());
		oldState.setPhase(newState.getPhase());
		oldState.setPlayersCards(newState.getPlayersCards(), numPlayers);
	}
	
	public boolean checkGoodCheck(int bid) {
		return bid == currentBid;
	}
	public boolean checkGoodRaise(int bid) {
		return bid > currentBid;
	}
	
	/**
	 * checks to see if more than one player is still going in the game.  
	 * @return  If there is only one player that has not folded, return false. 
	 * 			If there are more than one player in game, return true.
	 */
	public boolean checkPlayerState() {
		int n = 0;
		for(int i=0; i<numPlayers; i++) {
			if(playerState[i] == STAY)
				n++;
		}
		if(n>1)
			return true;
		return false;
	}
	
	
	/**
	 * like setUsedCards but only inserts one card.
	 * This won't be as efficient as doing setUsedCards() for multiple cards, but could be tons easier.
	 * @param cards
	 */
	public void setUsedCard(int card) {
		int i = 0;
		for(; i<usedCards.length; i++) {
			if(usedCards[i] == 52) {
				break;
			}
		}
		usedCards[i] = card;
	}
	/**
	 * sets used cards by going to the first instance of 52 and inserting cards starting from there on.
	 * @param cards
	 */
	public void setUsedCards(int[] cards) {
		for(int i=0; i<usedCards.length; i++) {
			if(usedCards[i] != 52) {
				continue;
			}
			for(int j=0; j<cards.length; j++) {
				usedCards[i] = cards[j];
				i++;
			}
			break;
		}
	}
	public int[] getUsedCards() {
		return usedCards;
	}
	
	public void setNumPlayers(int players) {
		numPlayers = players;
	}
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public void setPhase(int p) {
		phase = p;
	}
	public int getPhase() {
		return phase;
	}
	public String getPhaseName() {
		switch(getPhase()) {
		case DEAL_PHASE:
			return DEAL_PHASE_STRING;
		case BET1_PHASE:
			return BET1_PHASE_STRING;
		case DRAW_PHASE:
			return DRAW_PHASE_STRING;
		case BET2_PHASE:
			return BET2_PHASE_STRING;
		case FINAL_PHASE:
			return FINAL_PHASE_STRING;
		}
		return DEAL_PHASE_STRING;
	}
	
	/**
	 * updates playersCards 
	 * @param cards  To be copied
	 * @param playerNum 
	 */
	public void setPlayersCards(int[] cards, int playerNum) {
		for(int i=0; i<cards.length; i++) {
			playersCards[playerNum*5 + i] = cards[i];
		}
	}
	public int[] getPlayersCards() {
		return playersCards;
	}
	
	public void setPlayerUpdate(int player, int value) { //update whether a player had taken his turn
		playerUpdate[player] = value;
	}
	public int getPlayerUpdate(int playerNum) {
		return playerUpdate[playerNum]; 
	}
	public void setPlayerUpdateAndClear(int player, int value) { //sets update to value and sets all others that are still in to false
		for(int i=0; i<numPlayers; i++) {
			if(playerState[i] != FOLD) {
				playerUpdate[i] = FALSE;
			}
		}
		playerUpdate[player] = value;
	}
	
	public int getCurrentBid() {
		return currentBid;
	}
	public void setCurrentBid(int bid) {
		currentBid = bid;
	}
	
	public int getPot() {
		return pot;
	}
	public void setPot(int bid) {
		pot = bid;
	}
	
	public void setPlayersBids(int bid, int playerNum) {
		playersBids[playerNum] = bid;
	}
	public int getPlayersBids(int playerNum) {
		return playersBids[playerNum];
	}
	
	public void setPlayersMoney(int bid, int playerNum) {
		playersMoney[playerNum] = bid;
	}
	public int getPlayersMoney(int playerNum) {
		return playersMoney[playerNum];
	}
	/** 
	 * takes bid. subtracts only the raised portion of the bid.
	 * @param bid
	 * @param playerNum
	 */
	public void bidMoney(int bid, int playerNum) {
		int netBid = bid - getPlayersBids(playerNum); //how much to subtract it by
		setPlayersMoney(getPlayersMoney(playerNum) - netBid, playerNum);
	}
	
	public void setPlayerState(int value, int playerNum) {
		playerState[playerNum] = value;
	}
	public int getPlayerState(int playerNum) { //1 is good, 0 is fold
		return playerState[playerNum];
	}
	
	private void setPlayersFinalHand(int handValue, int playerNum) {
		playersFinalHand[playerNum] = handValue;
	}
	public int getPlayersFinalHand(int playerNum) {
		return playersFinalHand[playerNum];
	}
	
	/*
	 * Begin hand recognition
	 */
	
	public void calcWinningHand() {
		int value;
		for(int i=0; i<numPlayers; i++) {
			value = isNOfAKind(i);
			setPlayersFinalHand(value, i);
			setWinningHand(value);
			if(isFlush(i) == TRUE) {
				setPlayersFinalHand(FLUSH, i);
				setWinningHand(FLUSH);
			}
			if(isStraight(i) == TRUE) {
				setPlayersFinalHand(STRAIGHT, i);
				setWinningHand(STRAIGHT);
			}
			if(isStraightFlush(i) == TRUE) {
				setPlayersFinalHand(STRAIGHT_FLUSH, i);
				setWinningHand(STRAIGHT_FLUSH);
			}
			 						
		}
		calcWinningPlayer();
	}
	public void calcWinningPlayer() {
		int[] players = new int[numPlayers];
		int[] playersSorted = new int[numPlayers];
		for(int i=0; i<players.length; i++) {
			players[i] = getPlayersFinalHand(i);
		}
		for(int i=0; i<playersSorted.length; i++) {
			playersSorted[i] = getPlayersFinalHand(i);
		}
		
		bubbleSort(playersSorted);
		//see if more than one person has a winning hand
		int winningHand = playersSorted[3];
		int count = 0; //how many others have this hand
		for(int i=numPlayers-2; i>=0; i--) { //start with the second highest hand
			if(playersSorted[i] == winningHand) {
				count++;
			}
		}
		
		//if there is only one winner, set him as the winner
		if(count == 0) {
			for(int i=0; i<numPlayers; i++) {
				if(players[i] == winningHand) { //this is the player who has the winning hand.
					setWinningPlayer(i);
					setWinningHand(winningHand);
				}
			}
		}
		else { //TODO: THERE IS MORE THAN ONE WINNER
			
		}
		
	}
	public int getWinningHand() {
		return winningHand;
	}
	public void setWinningHand(int hand) {
		winningHand = hand;
	}
	/**
	 * 
	 * @return  Who the winning player was
	 */
	public int getWinningPlayer() {
		return winningPlayer;
	}
	private void setWinningPlayer(int playerNum) {
		winningPlayer = playerNum;
	}
	
	/**
	 * 
	 * @param playerNum
	 * @return  if it is not a pair, three, or four of a kind or two pair or full house, 
	 */
	public int isNOfAKind(int playerNum) {
		int returnValue = 52;
		int[] hand = new int[5];
		getHand(hand, playerNum);
		
		int counter1 = 1; //counters correspond to matches
		int counter2 = 1;
		int currentCard = 52;
		int match1 = 52; //match1 and 2 are for n of a kind.  the value is stored in one of these.
		int match2 = 52; //match2 will only be needed for 2 pair and full house.
		boolean firstPass = true;
		
		//set all cards to actual numerical value
		for(int i=0; i<hand.length; i++) {
			hand[i] = hand[i] % CARDS_PER_SUIT;
		}
		
		//TODO: nasty if clauses
		for(int i=0; i<hand.length; i++) {
			currentCard = hand[i];
			for(int j=i+1; j<hand.length; j++) { //navigate the remaining cards in "hand"
				
				if((currentCard == match1 || currentCard == match2) && !firstPass) //if this is the case, it has already been counted.
					continue;
				
				if(currentCard == hand[j]) {
					if(match1 == 52) { //this is the first pair it found
						match1 = currentCard;
						counter1++;
						
					}
					else { 
						if(currentCard == match1)  { //found another instance of match1
							counter1++;
						}
						else {
							if(match2 == 52) { //it needs to be assigned to match2 because it is a second pair.
								match2 = currentCard;
								counter2++;
							}
							else { //it SHOULD be the same cards as match2
								if(currentCard == match2)  { //found another instance of match1
									counter2++;
								}
								else {
									//TODO: THROW ERROR
									//System.out.println("HORRIBLE ERROR IN THE ISNOFKIND() METHOD");
								}
							}
						}
					}
				}
			}
			firstPass = false; //this is set after the first pass of the inner for loop
		}
		//find out what they have.
		if(match2 == 52) { //there is just one kind
			if(match1 == 52) { //no kinds
				return CARD_HIGH;
			}
			else { //at least a pair
				switch(counter1) {
				case 2:
					setPlayersFinalHand(PAIR, playerNum);
					return PAIR;
				case 3:
					setPlayersFinalHand(THREE_OF_A_KIND, playerNum);
					return THREE_OF_A_KIND;
				case 4:
					setPlayersFinalHand(FOUR_OF_A_KIND, playerNum);
					return FOUR_OF_A_KIND;
				}
			}
		}
		else { //either 2 pair or full house
			if(counter1 == 3 || counter2 == 3){
				setPlayersFinalHand(FULL_HOUSE, playerNum);
				return FULL_HOUSE;
			}
			else {
				setPlayersFinalHand(TWO_PAIR, playerNum);
				return TWO_PAIR;
			}
		}
		return returnValue;
	}
	
	public int isStraight(int playerNum) {
		int returnValue = FALSE;
		int[] hand = new int[5];
		getHand(hand, playerNum);
		
		//set all cards to actual numerical value
		for(int i=0; i<hand.length; i++) {
			hand[i] = hand[i] % CARDS_PER_SUIT;
		}
		bubbleSort(hand);
		
		for(int i=0; i<hand.length-1; i++) { //check to see if they are in order
			if(hand[i]+1 != hand[i+1]) { 
				returnValue = FALSE;
				break;
			}
			returnValue = TRUE;
		}
		if(returnValue == TRUE) {
			setPlayersFinalHand(STRAIGHT, playerNum);
		}
		return returnValue;
	}
	
	public int isFlush(int playerNum) {
		int[] hand = new int[5];
		getHand(hand, playerNum);
		
		int suit = hand[0]/13; //init suit to the first card
		for(int i=0; i<hand.length; i++) { //make all the same number if they are the same suit.
			hand[i] = hand[i]/13;
			if(hand[i] != suit) {
				return FALSE;
			}
		}
		setPlayersFinalHand(FLUSH, playerNum);
		return TRUE;
	}
	
//	public int isFullHouse(int playerNum) {
//		int returnValue = 52;
//		int[] hand = new int[5];
//		getHand(hand, playerNum);
//		
//		return returnValue;
//	}
	
	public int isStraightFlush(int playerNum) {
		int[] hand = new int[5];
		getHand(hand, playerNum);
		if(isFlush(playerNum) == TRUE && isStraight(playerNum) == TRUE) {
			setPlayersFinalHand(STRAIGHT_FLUSH, playerNum);
			return TRUE;
		}
		return FALSE;
	}
	
	public void bubbleSort(int[] hand) {
		int temp;
		boolean swapped = true;
		int j = 0;
		
		while(swapped) {
			swapped = false;
			j++;
			for(int i=0; i<hand.length-j; i++) {
				if(hand[i] > hand[i+1]) {
					temp = hand[i];
					hand[i] = hand[i+1];
					hand[i+1] = temp;
					swapped = true;
				}
			}
		}		
	}
}
