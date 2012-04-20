package schuchardt.evin.poker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Poker2Activity extends Activity {
	
	//Debug
	private final static boolean D = true;
	private final static String TAG = "Poker2Activity";
	
	//TODO: replace this with the actual player number.
	private static final int PLAYER_NUM = 0;
	/*
	 * This first part is the implementation of poker.
	 * The second section concerns the android part of the application
	 * (the onCreate, context switches and stuff)
	 */
	
	//Phases for the round
    private static final int DEAL_PHASE = 0;
    private static final int BET1_PHASE = 1;
    private static final int DRAW_PHASE = 2;
    private static final int BET2_PHASE = 3;
    private static final int FINAL_PHASE = 4;
    
    //Set truth value
    private static final int FALSE = 0;
    private static final int TRUE = 1;
    
    //Offset for player cards
    private static final int PLAYER_CARD_OFFSET = 5;
    
    //In or out of the game
    private static final int FOLD = 0;
	private static final int STAY = 1;
	
	private static final int BID_PHASE_CHECK = 0;
	private static final int BID_PHASE_CALL = 1;
	private static final int BID_PHASE_RAISE = 2;
	private static final int BID_PHASE_FOLD = 3;
    
    static DeckState mDeckState;
    
    //for use in last section
    private static final int CARDS_IN_HAND = 5;
    private static final int CARD1 = 0;
    private static final int CARD2 = 1;
    private static final int CARD3 = 2;
    private static final int CARD4 = 3;
    private static final int CARD5 = 4;
    
    
    /**
     * Needs to init state and stuff
     */
//    public void initPoker() {
//    	mDeckState = new DeckState();
//    	mDeckState.setNumPlayers(2);
//    	mDeckState.setPhase(DEAL_PHASE);
//    	//TODO: init player
//    	
//    }
    
    //TODO: Need to serialize each individual thing...http://arstechnica.com/civis/viewtopic.php?f=20&t=311940
	public static byte[] serialize(DeckState state) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(state);
		byte[] bytes = bos.toByteArray();
		return bytes;
		//...
		//out.close();
		//bos.close();
	}
    
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    	ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    	ObjectInput in = new ObjectInputStream(bis);
    	Object o = in.readObject(); 
    	return o;
    	//...
    	//bis.close();
    	//in.close();
    }
    
    /*
     * Test functions
     */
    	public static void testPair(int[] hand) {
    		hand[0] = 0;
			hand[1] = 13;
			hand[2] = 28;
			hand[3] = 29;
			hand[4] = 30;
    	}
    	public static void testTwoPair(int[] hand) {
    		hand[0] = 0;
			hand[1] = 13;
			hand[2] = 1;
			hand[3] = 14;
			hand[4] = 29;
    	}
    	public static void testThreeOfAKind(int[] hand) {
    		hand[0] = 0;
			hand[1] = 13;
			hand[2] = 26;
			hand[3] = 27;
			hand[4] = 28;
    	}
    	public static void testFourOfAKind(int[] hand) {
    		hand[0] = 0;
			hand[1] = 13;
			hand[2] = 26;
			hand[3] = 39;
			hand[4] = 40;
    	}
    	public static void testStraight(int[] hand) {
    		hand[0] = 14;
			hand[1] = 2;
			hand[2] = 3;
			hand[3] = 4;
			hand[4] = 5;
    	}
    	public static void testFlush(int[] hand) {
    		hand[0] = 0;
			hand[1] = 2;
			hand[2] = 3;
			hand[3] = 4;
			hand[4] = 5;
    	}
    	public static void testFullHouse(int[] hand) {
    		hand[0] = 0;
			hand[1] = 13;
			hand[2] = 26;
			hand[3] = 1;
			hand[4] = 14;
    	}
    	public static void testStraightFlush(int[] hand) {
    		hand[0] = 0;
			hand[1] = 1;
			hand[2] = 2;
			hand[3] = 3;
			hand[4] = 4;
    	}
	/*
     * END Test functions
     */
        
        
    /** 
     * This is the initial function that will start all the stuff that poker does.
     * Should interpret data and appropriately adjust the state.
     * 
     * @param buffer  The buffer that has the message passed from another user.
     */
    public static void pokerInterp(byte[] buffer, int playerNum) {
    	//begin deserialize and receive
    	//DeckState newDeckState = new DeckState();
    	try {
    		mDeckState = (DeckState)deserialize(buffer);
    	} 
    	//TODO: CATCH EXCEPTIONS
    	catch (IOException e) {/*System.out.println("IOException");*/} catch (ClassNotFoundException e) {/*System.out.println("ClassNotFoundException");*/}
    	//end deserialize and receive
    	
    	
    	//TODO: remove this scanner
//    	Scanner scan = new Scanner(System.in);
//    	System.out.println("Player " + playerNum);
    	
    	int[] hand;
    	boolean whileFlag = true;
    	int bid = 0;
    	//boolean whileFlag = true;
    	//switch on the phase.
    	switch (mDeckState.getPhase()) {
    	case DEAL_PHASE:
    		hand = new int[5];
    		//mDeckState.getHand(hand, playerNum);
    		if(mDeckState.getPlayerUpdate(playerNum) == FALSE) {
    			//TODO: undo this following comment
    			mDeckState.dealCards(hand, mDeckState.getUsedCards());
    			
//    			/*
//    			 * TODO: THIS IS THE TESTING PART
//    			 */
//    			switch(playerNum) {
//    			case 0:
//    				testStraight(hand);
//    				break;
//    			case 1:
//    				testFlush(hand);
//    				break;
//    			case 2:
//    				testFullHouse(hand);
//    				break;
//    			case 3:
//    				testStraightFlush(hand);
//    				break;
//    			}
    			mDeckState.setHand(hand, playerNum);
    			//add dealt cards to usedCards var
    			mDeckState.setHand(hand, playerNum);
    			//add dealt cards to usedCards var
    			mDeckState.setUsedCards(hand);
    			mDeckState.setPlayerUpdate(playerNum, TRUE);
    		}
    		if(mDeckState.isUpdated()) {
    			mDeckState.setPhase(BET1_PHASE);
    			mDeckState.initPlayerUpdate();
    		}
    		break;
    	case BET1_PHASE: //Place bet or fold
    		
    		break;
    	case DRAW_PHASE: 
    		break;
    	case BET2_PHASE: 

    		break;
    	case FINAL_PHASE: 
    		/*
    		 * In the final phase, check to see if there is only 1 player left.
    		 * If so, that player gets all the winnings.
    		 * If not, run function to compare hand against each other.
    		 * Finally, reset things so that they can go to the deal phase again.
    		 */
    		
    		int winningPlayer = 52;
    		
    		//Check to see if there is more than 1 player left.  If not, that player gets all the winnings.
    		if(mDeckState.checkPlayerState()) { //meaning there is more than 1 player left
    			//run function to compare hands against each other and assign the winner to winning player
    			//TODO: stuff with winning hand and winning player
    			mDeckState.calcWinningHand();
    			winningPlayer = mDeckState.getWinningPlayer();
			}
    		else {
    			winningPlayer = mDeckState.getRemainingPlayer(); //there is only one person in the round
    		}
    		
    		
    		//distribute winnings to winning player
    		mDeckState.distributeWinnings(winningPlayer);
    		
    		//TODO: fix reset stuff
    		mDeckState.setPhase(DEAL_PHASE);
    		break;
    	default: break;
    	}
    	
    }
    
    public static void betPhase(DeckState mDeckState, int playerNum) {
    	//TODO: remove this scanner
    	Scanner scan = new Scanner(System.in);
    	
    	int[] hand;
    	boolean whileFlag = true;
    	int bid = 0;
    	
    	while(whileFlag) {
			if(mDeckState.getPlayerState(playerNum) == FOLD) {
				break;
			}
    		if(mDeckState.getPlayerUpdate(playerNum) == FALSE) {
    			System.out.println("Options: \n" +
    					"0. Check\n" +
    					"1. Call\n" +
    					"2. Raise");
    			int bidPhaseOption = scan.nextInt();
    			switch(bidPhaseOption) {
    			case BID_PHASE_CHECK:
    				//check if current bid is higher than bid
    				//if it is, replace currentBid with bid
    				//if bid is good, set flag to false
    				//else repeat and say why it didn't work
    				bid = mDeckState.getPlayersBids(playerNum);
    				if(mDeckState.checkGoodCheck(bid)) {
    					mDeckState.setCurrentBid(bid);
    					mDeckState.setPlayerUpdate(playerNum, TRUE);
    					whileFlag = false; //break the loop.
    				}
    				else {
    					System.out.println("\n Cannot Check.\n");
    				}
    				break;
    			case BID_PHASE_CALL:
    				//check if current bid is higher than bid
    				//if bid is good, set flag to false
    				//else repeat and say why it didn't work
    				bid = mDeckState.getCurrentBid();
    				if(mDeckState.getPlayersBids(playerNum) != bid) {
    					//don't need to set current bid because he just called to the current highest bid.
    					mDeckState.bidMoney(bid, playerNum); //subtract this from his total money 
    					mDeckState.setPlayersBids(bid, playerNum);
    					mDeckState.setPlayerUpdate(playerNum, TRUE);
    					whileFlag = false; //break the loop.
    				}
    				else {
    					System.out.println("\n Cannot Call.\n");
    				}
    				break;
    			case BID_PHASE_RAISE:
    				//check if current bid is higher than bid
    				//if bid is good, set flag to false
    				//else repeat and say why it didn't work
    				System.out.println("You have " + mDeckState.getPlayersMoney(playerNum) + " to bid.\n Enter amount: ");
    				//TODO: check to see if they enter a valid amount.
    				bid = scan.nextInt();
    				if(mDeckState.checkGoodRaise(bid)) {
    					mDeckState.bidMoney(bid, playerNum); //subtract this from his total money 
    					mDeckState.setPlayersBids(bid, playerNum);
    					mDeckState.setCurrentBid(bid);
    					mDeckState.setPlayerUpdateAndClear(playerNum, TRUE);
    					whileFlag = false; //break the loop.
    				}
    				else {
    					System.out.println("\n Did not outbid max.\n");
    				}
    				break;
    			case BID_PHASE_FOLD:
    				break;
    			default:
    				//TODO: put in while loop for user input or something
    				System.out.println("\n Did not recognize input.\n");
    				break;
    			}
    		}
		} //end while loop
    }
    
    /*
     * This middle section is a segue of helper functions for the UI.
     */

    
    
	/*
	 * This last section concerns the android part of the application
	 * (the onCreate, context switches and stuff)
	 */
    TextView money;
    TextView phase;
    
	TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;
    
    Button deal;
    Button bid;
    Button fold;
    Button draw;
    
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(D) Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        //if(D) Log.d(TAG, "success super.onCreate(savedInstanceState);");
        setContentView(R.layout.play_view);
        //if(D) Log.d(TAG, "success setContentView(R.layout.play_view);");
        
        money = (TextView)findViewById(R.id.money);
        phase = (TextView)findViewById(R.id.phase);
        
        card1 = (TextView)findViewById(R.id.card1);
        card2 = (TextView)findViewById(R.id.card2);
        card3 = (TextView)findViewById(R.id.card3);
        card4 = (TextView)findViewById(R.id.card4);
        card5 = (TextView)findViewById(R.id.card5);
        
        deal = (Button)findViewById(R.id.deal);
        bid = (Button)findViewById(R.id.bid);
        fold = (Button)findViewById(R.id.fold);
        draw = (Button)findViewById(R.id.draw);
        
        //TODO: figure out how to assign each player a number.
        money.setText("$" + mDeckState.getPlayersMoney(PLAYER_NUM));
        phase.setText(mDeckState.getPhase());
        
        card1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(Poker2Activity.this, MainActivity.class);
//				startActivity(i);
				if(card1.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.green_card).getConstantState().hashCode()) { //Crazy set of functions to find a comparison on stroke.  change to red
					if(D) Log.d(TAG, "in color is green");
					//TODO: put this in the cards to be drawn
					card1.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_card));
				}
				else { //change to green
					if(D) Log.d(TAG, "in color is red");
					//TODO: take this out of the cards to be drawn
					card1.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
				}
				
			}
		});
        card2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(Poker2Activity.this, MainActivity.class);
//				startActivity(i);
			}
		});
        card3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(Poker2Activity.this, MainActivity.class);
//				startActivity(i);
			}
		});
        card4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(Poker2Activity.this, MainActivity.class);
//				startActivity(i);
			}
		});
        card5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(Poker2Activity.this, MainActivity.class);
//				startActivity(i);
			}
		});
        
        deal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: make sure that only one person presses this??
				if(mDeckState.getPhase() == FINAL_PHASE) {
					//TODO: action for dealing
				}
				else {
					//TODO: make toast or something saying you can't click that yet.
				}
			}
		});
        bid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == BET1_PHASE || mDeckState.getPhase() == BET2_PHASE) {
					if(mDeckState.getPlayerUpdate(PLAYER_NUM) == FALSE) { //maker sure this player has not already updated
						//TODO: figure out how to assign each player a number.
						betPhase(mDeckState, PLAYER_NUM);
			    		
			    		//check to see if all players involved have finished this phase
						if(mDeckState.isUpdated()) {
			    			mDeckState.setPhase(DRAW_PHASE);
			    			mDeckState.initPlayerUpdate();
			    		}
						
			    		//check to see if there are still more than one player who has not folded.
						//if there is not, set state to FINAL_PHASE
						if(!mDeckState.checkPlayerState()) {
							mDeckState.setPhase(FINAL_PHASE);
						}
					}
					else {
						//TODO: toast to the fact that they need to hold their horses until the other players do their thing
					}
				}
				else {
					//TODO: make toast or something saying you can't click that yet.
				}
			}
		});
        fold.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == BET1_PHASE || mDeckState.getPhase() == BET2_PHASE) {
					if(mDeckState.getPlayerUpdate(PLAYER_NUM) == FALSE) { //maker sure this player has not already updated
						//if bid is good, set flag to false
	    				mDeckState.setPlayerState(FOLD, PLAYER_NUM);
	    				mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
					}
					else {
						//TODO: toast to the fact that they need to hold their horses until the other players do their thing
					}
				}
				else {
					//TODO: make toast or something saying you can't click that yet.
				}
			}
		});
        draw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == DRAW_PHASE) {
					if(mDeckState.getPlayerUpdate(PLAYER_NUM) == FALSE) { //maker sure this player has not already updated
						if(mDeckState.getPlayerState(PLAYER_NUM) == STAY) { //meaning this player has not folded.
							
							int hand[] = new int[CARDS_IN_HAND];
							mDeckState.getHand(hand, PLAYER_NUM);
							/*
							 * When this is clicked, it should see how many of the cards are red. 
							 * replacing = #redCards
							 * if replacing>0 {
							 * 		go through an array one by one calling getRandomCard and set used card
							 * }
							 */
							//When this is clicked, it should see how many of the cards are red. :replacing = #redCards
							//int replacing = 0;
							if(card1.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.red_card).getConstantState().hashCode()); {//check to see if the card is red
								hand[CARD1] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD1]); //insert new number into usedCards
								//replacing++;
							}
							if(card2.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.red_card).getConstantState().hashCode()); {//check to see if the card is red
								hand[CARD2] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD2]); //insert new number into usedCards
								//replacing++;
							}
							if(card3.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.red_card).getConstantState().hashCode()); {//check to see if the card is red
								hand[CARD3] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD3]); //insert new number into usedCards
								//replacing++;
							}
							if(card4.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.red_card).getConstantState().hashCode()); {//check to see if the card is red
								hand[CARD4] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD4]); //insert new number into usedCards
								//replacing++;
							}
							if(card5.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.red_card).getConstantState().hashCode()); {//check to see if the card is red
								hand[CARD5] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD5]); //insert new number into usedCards
								//replacing++;
							}
							mDeckState.setPlayersCards(hand, PLAYER_NUM); //replace cards in deckstate
							
				    		//update player
				    		mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
				    		
				    		//if everyone is updated, switch phases
				    		if(mDeckState.isUpdated()) {
				    			mDeckState.setPhase(BET2_PHASE);
				    			mDeckState.initPlayerUpdate();
				    		}
						}
					}
					else {
						//TODO: toast to the fact that they need to hold their horses until the other players do their thing
					}
				}
				else {
					//TODO: make toast or something saying you can't click that yet.
				}
			}
		});
        
    }
    
   
}