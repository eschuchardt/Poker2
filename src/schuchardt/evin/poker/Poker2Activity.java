package schuchardt.evin.poker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import ec.nem.bluenet.BluetoothNodeService;
import ec.nem.bluenet.BluetoothNodeService.LocalBinder;
import ec.nem.bluenet.Message;
import ec.nem.bluenet.MessageListener;
import ec.nem.bluenet.Node;
import ec.nem.bluenet.NodeListener;

public class Poker2Activity extends Activity implements MessageListener, NodeListener {
	
	//Debug
	private final static boolean D = true;
	private final static String TAG = "Poker2Activity";
	
	private static final int GAME_ID = 13375;
	
	//TODO: replace this with the actual player number.
	private static int PLAYER_NUM;
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
    
    private static int layoutID = -1;
    
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
    
    private static DeckState mDeckState = new DeckState();
    
    private static int numPlayers = 0; 
    private static boolean hasRegistered;
    
    //for use in last section
    private static final int CARDS_IN_HAND = 5;
    private static final int CARD1 = 0;
    private static final int CARD2 = 1;
    private static final int CARD3 = 2;
    private static final int CARD4 = 3;
    private static final int CARD5 = 4;
    
    private static final int TOAST_DURATION = 1000;
    
    //Context Menu Options
    private static final String CHECK_STRING = "Check";
    private static final String CALL_STRING = "Call";
    private static final String RAISE_STRING = "Raise";
    
    //Check what color the card is
    private static boolean card1Red = false;
    private static boolean card2Red = false;
    private static boolean card3Red = false;
    private static boolean card4Red = false;
    private static boolean card5Red = false;
    
    //Card Value Strings
    private static final String ACE_OF_DIAMONDS = "Ace Dmds";
    private static final String TWO_OF_DIAMONDS = "2 Dmds";
    private static final String THREE_OF_DIAMONDS = "3 Dmds";
    private static final String FOUR_OF_DIAMONDS = "4 Dmds";
    private static final String FIVE_OF_DIAMONDS = "5 Dmds";
    private static final String SIX_OF_DIAMONDS = "6 Dmds";
    private static final String SEVEN_OF_DIAMONDS = "7 Dmds";
    private static final String EIGHT_OF_DIAMONDS = "8 Dmds";
    private static final String NINE_OF_DIAMONDS = "9 Dmds";
    private static final String TEN_OF_DIAMONDS = "10 Dmds";
    private static final String JACK_OF_DIAMONDS = "Jack Dmds";
    private static final String QUEEN_OF_DIAMONDS = "Queen Dmds";
    private static final String KING_OF_DIAMONDS = "King Dmds";
    
    private static final String ACE_OF_CLUBS = "Ace Clbs";
    private static final String TWO_OF_CLUBS = "2 Clbs";
    private static final String THREE_OF_CLUBS = "3 Clbs";
    private static final String FOUR_OF_CLUBS = "4 Clbs";
    private static final String FIVE_OF_CLUBS = "5 Clbs";
    private static final String SIX_OF_CLUBS = "6 Clbs";
    private static final String SEVEN_OF_CLUBS = "7 Clbs";
    private static final String EIGHT_OF_CLUBS = "8 Clbs";
    private static final String NINE_OF_CLUBS = "9 Clbs";
    private static final String TEN_OF_CLUBS = "10 Clbs";
    private static final String JACK_OF_CLUBS = "Jack Clbs";
    private static final String QUEEN_OF_CLUBS = "Queen Clbs";
    private static final String KING_OF_CLUBS = "King Clbs";
    
    private static final String ACE_OF_HEARTS = "Ace Hrts";
    private static final String TWO_OF_HEARTS = "2 Hrts";
    private static final String THREE_OF_HEARTS = "3 Hrts";
    private static final String FOUR_OF_HEARTS = "4 Hrts";
    private static final String FIVE_OF_HEARTS = "5 Hrts";
    private static final String SIX_OF_HEARTS = "6 Hrts";
    private static final String SEVEN_OF_HEARTS = "7 Hrts";
    private static final String EIGHT_OF_HEARTS = "8 Hrts";
    private static final String NINE_OF_HEARTS = "9 Hrts";
    private static final String TEN_OF_HEARTS = "10 Hrts";
    private static final String JACK_OF_HEARTS = "Jack Hrts";
    private static final String QUEEN_OF_HEARTS = "Queen Hrts";
    private static final String KING_OF_HEARTS = "King Hrts";
    
    private static final String ACE_OF_SPADES = "Ace Spds";
    private static final String TWO_OF_SPADES = "2 Spds";
    private static final String THREE_OF_SPADES = "3 Spds";
    private static final String FOUR_OF_SPADES = "4 Spds";
    private static final String FIVE_OF_SPADES = "5 Spds";
    private static final String SIX_OF_SPADES = "6 Spds";
    private static final String SEVEN_OF_SPADES = "7 Spds";
    private static final String EIGHT_OF_SPADES = "8 Spds";
    private static final String NINE_OF_SPADES = "9 Spds";
    private static final String TEN_OF_SPADES = "10 Spds";
    private static final String JACK_OF_SPADES = "Jack Spds";
    private static final String QUEEN_OF_SPADES = "Queen Spds";
    private static final String KING_OF_SPADES = "King Spds";
    
    private static Set<Node> nodes = new HashSet<Node>();
    
    
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
//    public static void pokerInterp(byte[] buffer, int playerNum) {
//    	//begin deserialize and receive
//    	//DeckState newDeckState = new DeckState();
//    	try {
//    		mDeckState = (DeckState)deserialize(buffer);
//    	} 
//    	//TODO: CATCH EXCEPTIONS
//    	catch (IOException e) {/*System.out.println("IOException");*/} catch (ClassNotFoundException e) {/*System.out.println("ClassNotFoundException");*/}
//    	//end deserialize and receive
//    	
//    	
//    	/*
//    	 * Instead of infinite loop, just run if statements on the cases that matter.  
//    	 * The UI should handle the rest.
//    	 */
//    	
//    	//TODO: remove this scanner
////    	Scanner scan = new Scanner(System.in);
////    	System.out.println("Player " + playerNum);
//    	
//    	int[] hand;
//    	boolean whileFlag = true;
//    	int bid = 0;
//    	//boolean whileFlag = true;
//    	//switch on the phase.
//    	switch (mDeckState.getPhase()) {
//    	case DEAL_PHASE:
//    		hand = new int[5];
//    		//mDeckState.getHand(hand, playerNum);
//    		if(mDeckState.getPlayerUpdate(playerNum) == FALSE) {
//    			//TODO: undo this following comment
//    			mDeckState.dealCards(hand, mDeckState.getUsedCards(), PLAYER_NUM);
//    			
////    			/*
////    			 * TODO: THIS IS THE TESTING PART
////    			 */
////    			switch(playerNum) {
////    			case 0:
////    				testStraight(hand);
////    				break;
////    			case 1:
////    				testFlush(hand);
////    				break;
////    			case 2:
////    				testFullHouse(hand);
////    				break;
////    			case 3:
////    				testStraightFlush(hand);
////    				break;
////    			}
//    			mDeckState.setHand(hand, playerNum);
//    			//add dealt cards to usedCards var
//    			mDeckState.setHand(hand, playerNum);
//    			//add dealt cards to usedCards var
//    			mDeckState.setUsedCards(hand);
//    			mDeckState.setPlayerUpdate(playerNum, TRUE);
//    		}
//    		if(mDeckState.isUpdated()) {
//    			mDeckState.setPhase(BET1_PHASE);
//    			mDeckState.initPlayerUpdate();
//    		}
//    		break;
//    	case BET1_PHASE: //Place bet or fold
//    		
//    		break;
//    	case DRAW_PHASE: 
//    		break;
//    	case BET2_PHASE: 
//
//    		break;
//    	case FINAL_PHASE: 
//    		/*
//    		 * In the final phase, check to see if there is only 1 player left.
//    		 * If so, that player gets all the winnings.
//    		 * If not, run function to compare hand against each other.
//    		 * Finally, reset things so that they can go to the deal phase again.
//    		 */
//    		
//    		int winningPlayer = 52;
//    		
//    		//Check to see if there is more than 1 player left.  If not, that player gets all the winnings.
//    		if(mDeckState.checkPlayerState()) { //meaning there is more than 1 player left
//    			//run function to compare hands against each other and assign the winner to winning player
//    			//TODO: stuff with winning hand and winning player
//    			mDeckState.calcWinningHand();
//    			winningPlayer = mDeckState.getWinningPlayer();
//			}
//    		else {
//    			winningPlayer = mDeckState.getRemainingPlayer(); //there is only one person in the round
//    		}
//    		
//    		
//    		//distribute winnings to winning player
//    		mDeckState.distributeWinnings(winningPlayer);
//    		
//    		//TODO: fix reset stuff
//    		mDeckState.setPhase(DEAL_PHASE);
//    		break;
//    	default: break;
//    	}
//    	
//    }
    
    
    /*
     * Helper functions for last section
     */
    public static String findCardString(int card) {
    	switch(card) {
    	case 0:
    		return ACE_OF_DIAMONDS;
    	case 1:
    		return TWO_OF_DIAMONDS;
    	case 2:
    		return THREE_OF_DIAMONDS;
    	case 3:
    		return FOUR_OF_DIAMONDS;
    	case 4:
    		return FIVE_OF_DIAMONDS;
    	case 5:
    		return SIX_OF_DIAMONDS;
    	case 6:
    		return SEVEN_OF_DIAMONDS;
    	case 7:
    		return EIGHT_OF_DIAMONDS;
    	case 8:
    		return NINE_OF_DIAMONDS;
    	case 9:
    		return TEN_OF_DIAMONDS;
    	case 10:
    		return JACK_OF_DIAMONDS;
    	case 11:
    		return QUEEN_OF_DIAMONDS;
    	case 12:
    		return KING_OF_DIAMONDS;
    	case 13:
    		return ACE_OF_CLUBS;
    	case 14:
    		return TWO_OF_CLUBS;
    	case 15:
    		return THREE_OF_CLUBS;
    	case 16:
    		return FOUR_OF_CLUBS;
    	case 17:
    		return FIVE_OF_CLUBS;
    	case 18:
    		return SIX_OF_CLUBS;
    	case 19:
    		return SEVEN_OF_CLUBS;
    	case 20:
    		return EIGHT_OF_CLUBS;
    	case 21:
    		return NINE_OF_CLUBS;
    	case 22:
    		return TEN_OF_CLUBS;
    	case 23:
    		return JACK_OF_CLUBS;
    	case 24:
    		return QUEEN_OF_CLUBS;
    	case 25:
    		return KING_OF_CLUBS;
    	case 26:
    		return ACE_OF_HEARTS;
    	case 27:
    		return TWO_OF_HEARTS;
    	case 28:
    		return THREE_OF_HEARTS;
    	case 29:
    		return FOUR_OF_HEARTS;
    	case 30:
    		return FIVE_OF_HEARTS;
    	case 31:
    		return SIX_OF_HEARTS;
    	case 32:
    		return SEVEN_OF_HEARTS;
    	case 33:
    		return EIGHT_OF_HEARTS;
    	case 34:
    		return NINE_OF_HEARTS;
    	case 35:
    		return TEN_OF_HEARTS;
    	case 36:
    		return JACK_OF_HEARTS;
    	case 37:
    		return QUEEN_OF_HEARTS;
    	case 38:
    		return KING_OF_HEARTS;
    	case 39:
    		return ACE_OF_SPADES;
    	case 40:
    		return TWO_OF_SPADES;
    	case 41:
    		return THREE_OF_SPADES;
    	case 42:
    		return FOUR_OF_SPADES;
    	case 43:
    		return FIVE_OF_SPADES;
    	case 44:
    		return SIX_OF_SPADES;
    	case 45:
    		return SEVEN_OF_SPADES;
    	case 46:
    		return EIGHT_OF_SPADES;
    	case 47:
    		return NINE_OF_SPADES;
    	case 48:
    		return TEN_OF_SPADES;
    	case 49:
    		return JACK_OF_SPADES;
    	case 50:
    		return QUEEN_OF_SPADES;
    	case 51:
    		return KING_OF_SPADES;
		default:
			return "";
			//TODO: Change this return value on default
    	}
    }
    
    
    
	/*
	 * This last section concerns the android part of the application
	 * (the onCreate, context switches and stuff)
	 */
    TextView money;
    TextView phase;
    TextView p_num;
    
	TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;
    
    Button deal;
    Button bid;
    Button fold;
    Button draw;
    
    Button play;
    Button register;
    
    TextView winner;
    
    TextView card1_p0;
    TextView card2_p0;
    TextView card3_p0;
    TextView card4_p0;
    TextView card5_p0;
    
    TextView card1_p1;
    TextView card2_p1;
    TextView card3_p1;
    TextView card4_p1;
    TextView card5_p1;
    
    TextView card1_p2;
    TextView card2_p2;
    TextView card3_p2;
    TextView card4_p2;
    TextView card5_p2;
    
    TextView card1_p3;
    TextView card2_p3;
    TextView card3_p3;
    TextView card4_p3;
    TextView card5_p3;
    
	/*
	 * FROM DEMO
	 */
    public static final String MESSAGES_KEY = "messages";
	BluetoothNodeService connectionService;
	boolean boundToService = false;
	ArrayAdapter<String> logAdapter;
	Handler uiHandler;
	/*
     * END FROM DEMO
     */
	
	FrameLayout frame;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(D) Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);
        
        frame = (FrameLayout)findViewById(R.id.frame);
        LayoutInflater li;
        frame.removeAllViews();
        li = getLayoutInflater();
        frame.addView( li.inflate(R.layout.create_view, null) );
        layoutID = R.layout.create_view;
        hasRegistered = false;
        populateCreateFields();
        
        /*
         * TODO: this needs to be done in the create portion of the game.
         * DO ALL OF THIS IN ONNODEENTER OR AN ACTIVITY BEFORE THIS ONE.  LIKE A POPULATION OF PLAYERS.
         * This should only be done once.
         * Check how many players are on the network.  Make sure there are no more 4.
         * Get the ids from each device and assign yourself a number, lowest one gets 0.
         * Have player 0 initialize the deck.
         */
        
        
        
        
        /*
         * FROM DEMO
         */
//        logAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
//        
//        ListView l = (ListView)findViewById(R.id.chat_log);
//        l.setAdapter(logAdapter);
//        
//        if(savedInstanceState != null){
//        	ArrayList<String> messages = savedInstanceState.getStringArrayList(MESSAGES_KEY);
//        	for(String s: messages){
//        		logAdapter.add(s);
//        	}
//        }
        
        uiHandler = new Handler();
        
        Intent intent = new Intent(this, BluetoothNodeService.class);
    	bindService(intent, connection, Context.BIND_AUTO_CREATE);
        /*
         * END FROM DEMO
         */
        
        
        
        
        //assign the values to different views on screen
//        if(mDeckState != null) { //assign the values to different views on screen
//        	populatePlayFields();	        
//    	}
    	if(D) Log.d(TAG, "end onCreate");
    }
    
    public void populateCreateFields() {
    	play = (Button)findViewById(R.id.play);
    	register = (Button)findViewById(R.id.register);
    	
    	play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hasRegistered) {
					try {
						String myAddress = connectionService.getLocalNode().getAddress(); //returns bluetooth address.  used for comparison
				        //List<Node> availableNodes = connectionService.getAvailableNodes(); //I don't know if this also returns my own node or not
				        //int networkSize = connectionService.getNetworkSize(); //I'm guessing availableNodes does return yourself because this is the size
						//nodes.add(connectionService.getLocalNode());
				        PLAYER_NUM = getMyPlayerNumber(myAddress);
	//			        if(PLAYER_NUM == 0 && mDeckState == null) {
	//			        	mDeckState = new DeckState();
	//			        	mDeckState.setNumPlayers(connectionService.getNetworkSize());
	//			        	//sendMessage to everyone updating the state of the deck.
	//			        }
					}catch(Exception e){
			        	Log.e(TAG, "Error on finding bluetooth connection. ", e);
			        }
					//setContentView(R.layout.play_view);
					uiHandler.post(new Runnable() {
						@Override
						public void run() {
							LayoutInflater li;
					        frame.removeAllViews();
					        li = getLayoutInflater();
					        frame.addView( li.inflate(R.layout.play_view, null) );
							layoutID = R.layout.play_view;
							populatePlayFields();
						}
					});
					
				}
				else {
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Please Register.", TOAST_DURATION);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.show();
				}
			}
		});
    	
    	register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!hasRegistered) {
					try {
						//String myAddress = connectionService.getLocalNode().getAddress(); //returns bluetooth address.  used for comparison
				        //List<Node> availableNodes = connectionService.getAvailableNodes(); //I don't know if this also returns my own node or not
				        //int networkSize = connectionService.getNetworkSize(); //I'm guessing availableNodes does return yourself because this is the size
						nodes.add(connectionService.getLocalNode());
				        //PLAYER_NUM = getMyPlayerNumber(myAddress);
	//			        if(PLAYER_NUM == 0 && mDeckState == null) {
	//			        	mDeckState = new DeckState();
	//			        	mDeckState.setNumPlayers(connectionService.getNetworkSize());
	//			        	//sendMessage to everyone updating the state of the deck.
	//			        }
					}catch(Exception e){
			        	Log.e(TAG, "Error on finding bluetooth connection. ", e);
			        }
					numPlayers++;
					mDeckState.addPlayer();
				}
				hasRegistered = true;
				sendRegisterMessage();
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, "Click play once all have registered.", TOAST_DURATION);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}
		});
    }
    
    public void populatePlayFields() {
    	money = (TextView)findViewById(R.id.money);
        phase = (TextView)findViewById(R.id.phase);
        p_num = (TextView)findViewById(R.id.player_num);
        
        card1 = (TextView)findViewById(R.id.card1);
        card2 = (TextView)findViewById(R.id.card2);
        card3 = (TextView)findViewById(R.id.card3);
        card4 = (TextView)findViewById(R.id.card4);
        card5 = (TextView)findViewById(R.id.card5);
        
        deal = (Button)findViewById(R.id.deal);
        bid = (Button)findViewById(R.id.bid);
        fold = (Button)findViewById(R.id.fold);
        draw = (Button)findViewById(R.id.draw);
        
    	money.setText("$" + Integer.toString(mDeckState.getPlayersMoney(PLAYER_NUM)));
    	setPhase();
    	p_num.setText("Player " + PLAYER_NUM);
        
//        if(((DeckState) getLastNonConfigurationInstance()) != null) {
//        	mDeckState = (DeckState)getLastNonConfigurationInstance();
//        }
        if (mDeckState == null) { //then this is a new game thing
        	//TODO: find someway to handle this
        	//mDeckState = new DeckState();
        }
        else {
        	if(mDeckState.hasBeenDealt(PLAYER_NUM)) { //this player already has a hand
        		
        		int[] hand = new int[5];
        		mDeckState.getHand(hand, PLAYER_NUM);
        		
        		refreshCardsView(hand);
        	}
        }
        //mDeckState.setPhase(FINAL_PHASE);
        
        
        
        
        card1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == DRAW_PHASE) {
					if(!card1Red/*card1.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.green_card).getConstantState().hashCode()*/) { //Crazy set of functions to find a comparison on stroke.  change to red
						//if(D) Log.d(TAG, "if1");
						//TODO: put this in the cards to be drawn
						//card1.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_card));
						card1Red = setCardColor(card1, card1Red, true);
						//if(D) Log.d(TAG, "card1.setBG");
					}
					else { //change to green
						//TODO: take this out of the cards to be drawn
						//card1.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
						card1Red = setCardColor(card1, card1Red, false);
					}
				}
			}
		});
        card2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == DRAW_PHASE) {
					if(!card2Red/*card2.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.green_card).getConstantState().hashCode()*/) { //Crazy set of functions to find a comparison on stroke.  change to red
						//if(D) Log.d(TAG, "if1");
						//TODO: put this in the cards to be drawn
						//card2.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_card));
						card2Red = setCardColor(card2, card2Red, true);
						//if(D) Log.d(TAG, "card1.setBG");
					}
					else { //change to green
						//TODO: take this out of the cards to be drawn
						//card2.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
						card2Red = setCardColor(card2, card2Red, false);
					}
				}
			}
		});
        card3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == DRAW_PHASE) {
					if(!card3Red/*card3.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.green_card).getConstantState().hashCode()*/) { //Crazy set of functions to find a comparison on stroke.  change to red
						//if(D) Log.d(TAG, "if1");
						//TODO: put this in the cards to be drawn
						//card3.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_card));
						card3Red = setCardColor(card3, card3Red, true);
						//if(D) Log.d(TAG, "card1.setBG");
					}
					else { //change to green
						//TODO: take this out of the cards to be drawn
						//card3.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
						card3Red = setCardColor(card3, card3Red, false);
					}
				}
			}
		});
        card4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == DRAW_PHASE) {
					if(!card4Red/*card4.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.green_card).getConstantState().hashCode()*/) { //Crazy set of functions to find a comparison on stroke.  change to red
						//if(D) Log.d(TAG, "if1");
						//TODO: put this in the cards to be drawn
						//card4.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_card));
						card4Red = setCardColor(card4, card4Red, true);
						//if(D) Log.d(TAG, "card1.setBG");
					}
					else { //change to green
						//TODO: take this out of the cards to be drawn
						//card4.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
						card4Red = setCardColor(card4, card4Red, false);
					}
				}
			}
		});
        card5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == DRAW_PHASE) {
					if(!card5Red/*card5.getBackground().getConstantState().hashCode() == getResources().getDrawable(R.drawable.green_card).getConstantState().hashCode()*/) { //Crazy set of functions to find a comparison on stroke.  change to red
						//if(D) Log.d(TAG, "if1");
						//TODO: put this in the cards to be drawn
						//card5.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_card));
						card5Red = setCardColor(card5, card5Red, true);
						//if(D) Log.d(TAG, "card1.setBG");
					}
					else { //change to green
						//TODO: take this out of the cards to be drawn
						//card5.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
						card5Red = setCardColor(card5, card5Red, false);
					}
				}
			}
		});
        
        deal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setDealOnClick();
			}
		}); 
        
        bid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = {"Check", "Call", "Raise"};

				AlertDialog.Builder builder = new AlertDialog.Builder(Poker2Activity.this);
				builder.setTitle("Options");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				   
				        if(mDeckState.getPhase() == BET1_PHASE || mDeckState.getPhase() == BET2_PHASE) {
							if(mDeckState.getPlayerUpdate(PLAYER_NUM) == FALSE) { //maker sure this player has not already updated
								int playerBid;
								switch(item) {
									case 0: //Check
							        	//check if current bid is higher than bid
					    				//if it is, replace currentBid with bid
					    				//if bid is good, set flag to false
					    				//else repeat and say why it didn't work
					    				playerBid = mDeckState.getPlayersBids(PLAYER_NUM);
					    				if(mDeckState.checkGoodCheck(playerBid)) {
					    					mDeckState.setCurrentBid(playerBid);
					    					mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
					    					setPhase();
					    				}
					    				else {
					    					Context context = getApplicationContext();
					    					Toast toast = Toast.makeText(context, "Cannot Check.", TOAST_DURATION);
					    					toast.setGravity(Gravity.BOTTOM, 0, 0);
					    					toast.show();
					    				}
					    				break;
							        
									case 1: //Call
							        	
							        	
						        		//check if current bid is higher than bid
					    				//if bid is good, set flag to false
					    				//else repeat and say why it didn't work
					    				playerBid = mDeckState.getCurrentBid();
					    				if(mDeckState.getPlayersBids(PLAYER_NUM) != playerBid) {
					    					//don't need to set current bid because he just called to the current highest bid.
					    					mDeckState.bidMoney(playerBid, PLAYER_NUM); //subtract this from his total money 
					    					mDeckState.setPlayersBids(playerBid, PLAYER_NUM);
					    					mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
					    					money.setText("$" + Integer.toString(mDeckState.getPlayersMoney(PLAYER_NUM)));
					    				}
					    				else {
					    					Context context = getApplicationContext();
					    					Toast toast = Toast.makeText(context, "Cannot Call.", TOAST_DURATION);
					    					toast.setGravity(Gravity.BOTTOM, 0, 0);
					    					toast.show();
					    				}
							        	break;
							        	
									case 2: //Raise
						        			//check if current bid is higher than bid
						    				//if bid is good, set flag to false
						    				//else repeat and say why it didn't work
						    				//TODO: check to see if they enter a valid amount.
						        			
						        			
						        			//BEGIN code to have user input bid
						        			AlertDialog.Builder alert = new AlertDialog.Builder(Poker2Activity.this);
						        			alert.setTitle("Raise");
						        			alert.setMessage("Please enter raise.");
						        			// Set an EditText view to get user input 
						        			final EditText input = new EditText(Poker2Activity.this);
						        			alert.setView(input);
						        			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						        				public void onClick(DialogInterface dialog, int whichButton) {
						        					int playerBid = Integer.parseInt(input.getText().toString());
						        					if(mDeckState.checkGoodRaise(playerBid)) {
								    					mDeckState.bidMoney(playerBid, PLAYER_NUM); //subtract this from his total money 
								    					mDeckState.setPlayersBids(playerBid, PLAYER_NUM);
								    					mDeckState.setCurrentBid(playerBid);
								    					mDeckState.setPlayerUpdateAndClear(PLAYER_NUM, TRUE);
								    					
								    					//check to see if all players involved have finished this phase
								    					
								    					if(mDeckState.isUpdated()) {
								    						if(mDeckState.getPhase() == BET1_PHASE) {
								    							mDeckState.setPhase(DRAW_PHASE);
								    						}
								    						else {
								    							mDeckState.setPhase(FINAL_PHASE);
								    						}
								    		    			mDeckState.initPlayerUpdate();
								    		    			setPhase();
								    		    		}
								    					
								    					//check to see if there are still more than one player who has not folded.
								    					//if there is not, set state to FINAL_PHASE
	//								    					if(!mDeckState.checkPlayerState()) {
	//								    						mDeckState.setPhase(FINAL_PHASE);
	//								    					}
								    					money.setText("$" + Integer.toString(mDeckState.getPlayersMoney(PLAYER_NUM)));
								    					sendMessage();
								    				}
								    				else {
								    					Context context = getApplicationContext();
								    					Toast toast = Toast.makeText(context, "Try Again.", TOAST_DURATION);
								    					toast.setGravity(Gravity.BOTTOM, 0, 0);
								    					toast.show();
								    				}
						        				}
						        			});
						        			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						        			 public void onClick(DialogInterface dialog, int whichButton) {
						        			     // Canceled.
						        			}
						        			});
						        			alert.show();
						        			//END code to have user input bid
							        			
							        }
							        	
								//check to see if all players involved have finished this phase
								if(mDeckState.isUpdated()) {
									if(mDeckState.getPhase() == BET1_PHASE) {
										mDeckState.setPhase(DRAW_PHASE);
									}
									else {
										mDeckState.setPhase(FINAL_PHASE);
									}
					    			mDeckState.initPlayerUpdate();
					    			setPhase();
					    			
					    		}
								
								//check to see if there are still more than one player who has not folded.
								//TODO: hack for testing
								if(mDeckState.getNumPlayers() > 1) {
									if(!mDeckState.checkPlayerState()) {
										mDeckState.setPhase(FINAL_PHASE);
										setPhase();
									}
								}
								setPhase();
								sendMessage();
					    		
							}
				        	else {
								//TODO: toast to the fact that they need to hold their horses until the other players do their thing
							}
				        }
				        else {
							Context context = getApplicationContext();
							Toast toast = Toast.makeText(context, "Invalid click: current phase is " + mDeckState.getPhaseName(), TOAST_DURATION);
							toast.setGravity(Gravity.BOTTOM, 0, 0);
							toast.show();				
						}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
        
        //registerForContextMenu(bid); 
        
        fold.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDeckState.getPhase() == BET1_PHASE || mDeckState.getPhase() == BET2_PHASE) {
					if(mDeckState.getPlayerUpdate(PLAYER_NUM) == FALSE) { //maker sure this player has not already updated
						//if bid is good, set flag to false
	    				mDeckState.setPlayerState(FOLD, PLAYER_NUM);
	    				mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
	    				
	    				sendMessage();
					}
					else {
						//TODO: toast to the fact that they need to hold their horses until the other players do their thing
					}
				}
				else {
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Invalid click: current phase is " + mDeckState.getPhaseName(), TOAST_DURATION);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.show();				}
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
							/*
							 * TODO: mutex on resources here.  Need to wait till this person's turn,
							 * or at least a lull in the network so that the update of usedCards does not mess up
							 */
							if(card1Red == true) {//check to see if the card is red
								hand[CARD1] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD1]); //insert new number into usedCards
							}
							if(card2Red == true) {//check to see if the card is red
								hand[CARD2] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD2]); //insert new number into usedCards
							}
							if(card3Red == true) {//check to see if the card is red
								hand[CARD3] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD3]); //insert new number into usedCards
							}
							if(card4Red == true) {//check to see if the card is red
								hand[CARD4] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD4]); //insert new number into usedCards
							}
							if(card5Red == true) {//check to see if the card is red
								hand[CARD5] = mDeckState.getRandomCard(mDeckState.getUsedCards(), hand); //insert random number into local hand, but not into usedCards or playersCards
				    			mDeckState.setUsedCard(hand[CARD5]); //insert new number into usedCards
							}
							mDeckState.setPlayersCards(hand, PLAYER_NUM); //replace cards in deckstate
							
							refreshCardsView(hand);
							resetCardColor();
							
				    		//update player
				    		mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
				    		
				    		//if everyone is updated, switch phases
				    		if(mDeckState.isUpdated()) {
				    			mDeckState.setPhase(BET2_PHASE);
				    			mDeckState.initPlayerUpdate();
				    		}
				    		
				    		setPhase();
				    		sendMessage();
						}
					}
					else {
						//TODO: toast to the fact that they need to hold their horses until the other players do their thing
					}
				}
				else {
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Invalid click: current phase is " + mDeckState.getPhaseName(), TOAST_DURATION);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.show();				}
			}
		});
    }
    
    public void setDealOnClick() {
		//TODO: make sure that only one person presses this??
		if(mDeckState.getPhase() == DEAL_PHASE || mDeckState.getPhase() == FINAL_PHASE) {
			/*
			 * Check to see if the layout view is correct
			 * TODO: if this is the case, the hands must be reset
			 * along with the usedCards
			 */
			
			
			if(layoutID != R.layout.play_view) {
				
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						LayoutInflater li;
				        frame.removeAllViews();
				        li = getLayoutInflater();
				        frame.addView( li.inflate(R.layout.play_view, null) );
						layoutID = R.layout.play_view;
						if(mDeckState.getPhase() != BET1_PHASE) {
							mDeckState.setPhase(DEAL_PHASE);
						}
						populatePlayFields();
						
					}
				});
				
				if(!mDeckState.isNewDeck()) {
					mDeckState.initPlayersFinalHand();
					mDeckState.initPlayersCards();
					mDeckState.initPlayersBids();
					mDeckState.initPlayerState();
					mDeckState.initUsedCards();
					mDeckState.initPlayerUpdate();
					mDeckState.initWinners();
					mDeckState.initCurrentBid();
					mDeckState.initFinalState();
					mDeckState.setPhase(DEAL_PHASE);
					mDeckState.setNewDeck(true);
					mDeckState.initPot();
				}
			}
			
			//TODO: action for dealing
			if(mDeckState.getPlayerUpdate(PLAYER_NUM) == FALSE) {
				if(mDeckState.isNewDeck()) {
					int[] hand = new int[CARDS_IN_HAND];
	    			mDeckState.dealCards(hand, mDeckState.getUsedCards(), PLAYER_NUM); //Only deal to that player
	    			mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
	    			//Refresh the cards and have them appear on the text view.
	    			if(layoutID == R.layout.play_view) {
	    				refreshCardsView(hand);
	    			}
				}
				
    			
    			
    			
    			
    			if(mDeckState.isUpdated()) {
    				//mDeckState.setNewDeck(false);
    				mDeckState.setNewDeck(false);
        			mDeckState.setPhase(BET1_PHASE);
        			mDeckState.initPlayerUpdate();
        		}
    			setPhase();
    			sendMessage();
			}
			else {
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, "Wait for others to deal." + mDeckState.getPhaseName(), TOAST_DURATION);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}
			
			sendMessage();
		}
		else {
			//TODO: make toast or something saying you can't click that yet.
			Context context = getApplicationContext();
			Toast toast = Toast.makeText(context, "Invalid click: current phase is " + mDeckState.getPhaseName(), TOAST_DURATION);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();
		}
    }
    
    public boolean setCardColor(TextView card, boolean cardBool, boolean value) {
    	if(cardBool == false) { //card should be green
    		card.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_card));
    	}
    	else {
    		card.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
    	}
    	return value;
    }
    
    public void refreshCardsView(int[] hand) {
    	//Refresh the cards and have them appear on the text view.
    	card1.setText(findCardString(hand[0]));
		card2.setText(findCardString(hand[1]));
		card3.setText(findCardString(hand[2]));
		card4.setText(findCardString(hand[3]));
		card5.setText(findCardString(hand[4]));
    }
    
    public void refreshCardsViewFinal(int numPlayers) {
    	switch(numPlayers) {
    	case 1:
    		refreshCardsViewP0();
    		break;
    	case 2:
    		refreshCardsViewP0();
    		refreshCardsViewP1();
    		break;
    	case 3:
    		refreshCardsViewP0();
    		refreshCardsViewP1();
    		refreshCardsViewP2();
    		break;
    	case 4:
    		refreshCardsViewP0();
    		refreshCardsViewP1();
    		refreshCardsViewP2();
    		refreshCardsViewP3();
    		break;
		default:
			break;
    	}
    }
    
    public void refreshCardsViewP0() {
    	int[] hand = new int[5];
    	mDeckState.getHand(hand, 0);
    	
    	card1_p0.setText(findCardString(hand[0]));
        card2_p0.setText(findCardString(hand[1]));
        card3_p0.setText(findCardString(hand[2]));
        card4_p0.setText(findCardString(hand[3]));
        card5_p0.setText(findCardString(hand[4]));
    }
    
    public void refreshCardsViewP1() {
    	int[] hand = new int[5];
    	mDeckState.getHand(hand, 1);
    	
    	card1_p1.setText(findCardString(hand[0]));
        card2_p1.setText(findCardString(hand[1]));
        card3_p1.setText(findCardString(hand[2]));
        card4_p1.setText(findCardString(hand[3]));
        card5_p1.setText(findCardString(hand[4]));
    }

    public void refreshCardsViewP2() {
    	int[] hand = new int[5];
    	mDeckState.getHand(hand, 2);
    	
    	card1_p2.setText(findCardString(hand[0]));
        card2_p2.setText(findCardString(hand[1]));
        card3_p2.setText(findCardString(hand[2]));
        card4_p2.setText(findCardString(hand[3]));
        card5_p2.setText(findCardString(hand[4]));
    }
    public void refreshCardsViewP3() {
    	int[] hand = new int[5];
    	mDeckState.getHand(hand, 3);
    	
    	card1_p3.setText(findCardString(hand[0]));
        card2_p3.setText(findCardString(hand[1]));
        card3_p3.setText(findCardString(hand[2]));
        card4_p3.setText(findCardString(hand[3]));
        card5_p3.setText(findCardString(hand[4]));
    }
    
    public void refreshPhase() {
    	money.setText("$" + Integer.toString(mDeckState.getPlayersMoney(PLAYER_NUM)));
    	phase.setText(" :" + mDeckState.getPhaseName() + " Phase");
    }
    
    public void resetCardColor() {
    	card1.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
    	card2.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
    	card3.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
    	card4.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
    	card5.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_card));
    	
    	card1Red = false;
    	card2Red = false;
    	card3Red = false;
    	card4Red = false;
    	card5Red = false;
    }

    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
    	super.onCreateContextMenu(menu, v, menuInfo);  
    	
        menu.setHeaderTitle("Options");  
        menu.add(0, v.getId(), 0, "Check");  
        menu.add(0, v.getId(), 0, "Call");  
        menu.add(0, v.getId(), 0, "Raise");
    }  
  
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  
    	if(mDeckState.getPhase() == BET1_PHASE || mDeckState.getPhase() == BET2_PHASE) {
			if(mDeckState.getPlayerUpdate(PLAYER_NUM) == FALSE) { //maker sure this player has not already updated
		       
				if(item.getTitle()=="Check"){
		        	//check if current bid is higher than bid
    				//if it is, replace currentBid with bid
    				//if bid is good, set flag to false
    				//else repeat and say why it didn't work
    				int playerBid = mDeckState.getPlayersBids(PLAYER_NUM);
    				if(mDeckState.checkGoodCheck(playerBid)) {
    					mDeckState.setCurrentBid(playerBid);
    					mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
    					setPhase();
    				}
    				else {
    					Context context = getApplicationContext();
    					Toast toast = Toast.makeText(context, "Cannot Check.", TOAST_DURATION);
    					toast.setGravity(Gravity.BOTTOM, 0, 0);
    					toast.show();
    				}
		        }  
		        else {
		        	
		        	if(item.getTitle()=="Call"){
		        		//check if current bid is higher than bid
	    				//if bid is good, set flag to false
	    				//else repeat and say why it didn't work
	    				int playerBid = mDeckState.getCurrentBid();
	    				if(mDeckState.getPlayersBids(PLAYER_NUM) != playerBid) {
	    					//don't need to set current bid because he just called to the current highest bid.
	    					mDeckState.bidMoney(playerBid, PLAYER_NUM); //subtract this from his total money 
	    					mDeckState.setPlayersBids(playerBid, PLAYER_NUM);
	    					mDeckState.setPlayerUpdate(PLAYER_NUM, TRUE);
	    					money.setText("$" + Integer.toString(mDeckState.getPlayersMoney(PLAYER_NUM)));
	    				}
	    				else {
	    					Context context = getApplicationContext();
	    					Toast toast = Toast.makeText(context, "Cannot Call.", TOAST_DURATION);
	    					toast.setGravity(Gravity.BOTTOM, 0, 0);
	    					toast.show();
	    				}
		        	}
		        	else {
		        		
		        		if(item.getTitle()=="Raise"){
		        			//check if current bid is higher than bid
		    				//if bid is good, set flag to false
		    				//else repeat and say why it didn't work
		    				//TODO: check to see if they enter a valid amount.
		        			
		        			
		        			//BEGIN code to have user input bid
		        			AlertDialog.Builder alert = new AlertDialog.Builder(this);
		        			alert.setTitle("Raise");
		        			alert.setMessage("Please enter raise.");
		        			// Set an EditText view to get user input 
		        			final EditText input = new EditText(this);
		        			alert.setView(input);
		        			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        				public void onClick(DialogInterface dialog, int whichButton) {
		        					int playerBid = Integer.parseInt(input.getText().toString());
		        					if(mDeckState.checkGoodRaise(playerBid)) {
				    					mDeckState.bidMoney(playerBid, PLAYER_NUM); //subtract this from his total money 
				    					mDeckState.setPlayersBids(playerBid, PLAYER_NUM);
				    					mDeckState.setCurrentBid(playerBid);
				    					mDeckState.setPlayerUpdateAndClear(PLAYER_NUM, TRUE);
				    					
				    					//check to see if all players involved have finished this phase
				    					
				    					if(mDeckState.isUpdated()) {
				    						if(mDeckState.getPhase() == BET1_PHASE) {
				    							mDeckState.setPhase(DRAW_PHASE);
				    						}
				    						else {
				    							mDeckState.setPhase(FINAL_PHASE);
				    						}
				    		    			mDeckState.initPlayerUpdate();
				    		    			setPhase();
				    		    		}
				    					
				    					//check to see if there are still more than one player who has not folded.
				    					//if there is not, set state to FINAL_PHASE
//				    					if(!mDeckState.checkPlayerState()) {
//				    						mDeckState.setPhase(FINAL_PHASE);
//				    					}
				    					money.setText("$" + Integer.toString(mDeckState.getPlayersMoney(PLAYER_NUM)));
				    					sendMessage();
				    				}
				    				else {
				    					Context context = getApplicationContext();
				    					Toast toast = Toast.makeText(context, "Try Again.", TOAST_DURATION);
				    					toast.setGravity(Gravity.BOTTOM, 0, 0);
				    					toast.show();
				    				}
		        				}
		        			});
		        			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        			 public void onClick(DialogInterface dialog, int whichButton) {
		        			     // Canceled.
		        			}
		        			});
		        			alert.show();
		        			//END code to have user input bid
		        			
		        		}
		        		else {return false;}  
		        	}
		        }
				//check to see if all players involved have finished this phase
				if(mDeckState.isUpdated()) {
					if(mDeckState.getPhase() == BET1_PHASE) {
						mDeckState.setPhase(DRAW_PHASE);
					}
					else {
						mDeckState.setPhase(FINAL_PHASE);
					}
	    			mDeckState.initPlayerUpdate();
	    			setPhase();
	    			
	    		}
				
				//check to see if there are still more than one player who has not folded.
				//TODO: hack for testing
				if(mDeckState.getNumPlayers() > 1) {
					if(!mDeckState.checkPlayerState()) {
						mDeckState.setPhase(FINAL_PHASE);
						setPhase();
					}
				}
				setPhase();
				sendMessage();
	    		
			}
        	else {
				//TODO: toast to the fact that they need to hold their horses until the other players do their thing
			}
        }
        else {
			Context context = getApplicationContext();
			Toast toast = Toast.makeText(context, "Invalid click: current phase is " + mDeckState.getPhaseName(), TOAST_DURATION);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();				
		}
    return true;  
    }  
    
    /**
     * Sets the phase and checks to see if the phase is final. 
     * If it is final, display the winner and all the hands on the screen.
     * Then have some sort of refresh button to continue to Deal_Phase again.
     */
    public void setPhase() {
    	int mmPhase = mDeckState.getPhase();
    	phase.setText(" :" + mDeckState.getPhaseName() + " Phase");
    	if(mmPhase == FINAL_PHASE) {
			
    		
    		uiHandler.post(new Runnable() {
				@Override
				public void run() {
					LayoutInflater li;
			        frame.removeAllViews();
			        li = getLayoutInflater();
			        
	    		switch(mDeckState.getNumPlayers()) {
	//    		case 1: //TODO: hack to test one player
	//    			setContentView(R.layout.final_view_two_player);
	//    			layoutID = R.layout.final_view_two_player;
	//    			populateTwoPlayer();
	//    			break;
	    		case 2:
//	    			uiHandler.post(new Runnable() {
//	    				@Override
//	    				public void run() {
//	    					LayoutInflater li;
//	    			        frame.removeAllViews();
//	    			        li = getLayoutInflater();
	    			        frame.addView( li.inflate(R.layout.final_view_two_player, null) );
	    	    			layoutID = R.layout.final_view_two_player;
	    	    			populateTwoPlayer();
//	    				}
//	    			});
	    			
	    			
	    			break;
	    		case 3:
//	    			uiHandler.post(new Runnable() {
//	    				@Override
//	    				public void run() {
//	    					LayoutInflater li;
//	    			        frame.removeAllViews();
//	    			        li = getLayoutInflater();
	    			        frame.addView( li.inflate(R.layout.final_view_three_player, null) );
	    	    			layoutID = R.layout.final_view_three_player;
	    	    			populateThreePlayer();
//	    				}
//	    			});
	    			
	    			break;
	    		case 4:
//	    			uiHandler.post(new Runnable() {
//	    				@Override
//	    				public void run() {
//	    					LayoutInflater li;
//	    			        frame.removeAllViews();
//	    			        li = getLayoutInflater();
	    			        frame.addView( li.inflate(R.layout.final_view_four_player, null) );
	    	    			layoutID = R.layout.final_view_four_player;
	    	    			populateFourPlayer();
//	    				}
//	    			});
	    			
	    			break;
				default: 
					break;
	    		}
	    		refreshCardsViewFinal(mDeckState.getNumPlayers());
	//    		winner.setText("Player " + mDeckState.getWinningPlayer() + " wins with " + mDeckState.getWinningHandString());

				}
			});
    	}
    	Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, mDeckState.getPhaseName(), 1000);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
		
    }
    
    public void populateTwoPlayer() {
    	money = (TextView)findViewById(R.id.money);
        phase = (TextView)findViewById(R.id.phase);
        
        card1_p0 = (TextView)findViewById(R.id.card1_p0);
        card2_p0 = (TextView)findViewById(R.id.card2_p0);
        card3_p0 = (TextView)findViewById(R.id.card3_p0);
        card4_p0 = (TextView)findViewById(R.id.card4_p0);
        card5_p0 = (TextView)findViewById(R.id.card5_p0);
        
        card1_p1 = (TextView)findViewById(R.id.card1_p1);
        card2_p1 = (TextView)findViewById(R.id.card2_p1);
        card3_p1 = (TextView)findViewById(R.id.card3_p1);
        card4_p1 = (TextView)findViewById(R.id.card4_p1);
        card5_p1 = (TextView)findViewById(R.id.card5_p1);
        
        deal = (Button)findViewById(R.id.deal);
        bid = (Button)findViewById(R.id.bid);
        fold = (Button)findViewById(R.id.fold);
        draw = (Button)findViewById(R.id.draw);
        
        winner = (TextView)findViewById(R.id.winner);
        
        //mDeckState.setPhase(DEAL_PHASE);
        
    	//setPhase();
    	mDeckState.setFinalState();
    	money.setText("$" + Integer.toString(mDeckState.getPlayersMoney(PLAYER_NUM)));
    	winner.setText("Player " + mDeckState.getWinningPlayer() + " wins with " + mDeckState.getWinningHandString());
    	
        
        deal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setDealOnClick();
			}
		}); 
        
        
    }
    
    public void populateThreePlayer() {
    	
    	populateTwoPlayer();
        
        card1_p2 = (TextView)findViewById(R.id.card1_p2);
        card2_p2 = (TextView)findViewById(R.id.card2_p2);
        card3_p2 = (TextView)findViewById(R.id.card3_p2);
        card4_p2 = (TextView)findViewById(R.id.card4_p2);
        card5_p2 = (TextView)findViewById(R.id.card5_p2);
    }
    
    public void populateFourPlayer() {
   
    	populateThreePlayer();
        
        card1_p3 = (TextView)findViewById(R.id.card1_p3);
        card2_p3 = (TextView)findViewById(R.id.card2_p3);
        card3_p3 = (TextView)findViewById(R.id.card3_p3);
        card4_p3 = (TextView)findViewById(R.id.card4_p3);
        card5_p3 = (TextView)findViewById(R.id.card5_p3);
    }
    
//    @Override
//    public Object onRetainNonConfigurationInstance() {
//        final DeckState state = Poker2Activity.mDeckState;
//        return state;
//    }
    
    /*
     * FROM DEMO
     */
    @Override
	public void onDestroy(){
		super.onDestroy();
		// MAKE SURE YOU REMOVE YOURSELF FROM THE LISTENER LIST
		// OTHERWISE YOU WILL LEAK MEMORY
		if(connectionService != null){
			connectionService.removeMessageListener(this);
			connectionService.removeNodeListener(this);
		}
		unbindService(connection);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		//begin deserialize and receive
    	//DeckState newDeckState = new DeckState();
    	try {
    		savedInstanceState.putByteArray("state", serialize(mDeckState));
    	} 
    	//TODO: CATCH EXCEPTIONS
    	catch (IOException e) {/*System.out.println("IOException");*/} 
    	//end deserialize and receive
    	
    	super.onSaveInstanceState(savedInstanceState);
//		ArrayList<String> backup = new ArrayList<String>();
//		for(int x = 0; x < logAdapter.getCount(); x++){
//			backup.add(logAdapter.getItem(x));
//		}
//		b.putStringArrayList(MESSAGES_KEY, backup);
	}
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	//begin deserialize and receive
    	//DeckState newDeckState = new DeckState();
    	try {
    		mDeckState = (DeckState)deserialize(savedInstanceState.getByteArray("state"));
    	} 
    	//TODO: CATCH EXCEPTIONS
    	catch (IOException e) {/*System.out.println("IOException");*/} catch (ClassNotFoundException e) {/*System.out.println("ClassNotFoundException");*/}
    	//end deserialize and receive
    	
    	
    	
    }
    @Override
    public void onPause() {
    	super.onPause();
    }
    @Override
    public void onResume() {
    	super.onResume();
    }
	
	private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            connectionService = binder.getService();
            connectionService.addMessageListener(Poker2Activity.this);
            connectionService.addNodeListener(Poker2Activity.this);
            boundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundToService = false;
        }
    };

    /**
     * Put into a list the Nodes or addresses of each device.  Remove in onNodeExit
     */
	@Override
	public void onNodeEnter(String node) {
		
//		final String txt = node;
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, "Someone entered the session.", 1000);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
//				logAdapter.add("Node " + txt + " has Connected to us.");
//				logAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Remove from list the Nodes or addresses of each device that leaves the network.
	 */
	@Override
	public void onNodeExit(String node) {
//		final String txt = node;
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, "Someone exited the session.", 1000);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
//				logAdapter.add("Node " + txt + " has disconnected from us.");
//				logAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Check to see that it is actually a part of my application. 
	 * Use a specific number to recognize that in the message.
	 */
	@Override
	public void onMessageReceived(Message message) {
//		final String from = message.getTransmitterName();
//		final String text = message.getText();
		//TODO: put in try block just in case this fails
		try{
			if(message != null && ((DeckState)message.getData()).ID == GAME_ID) {
				mDeckState = (DeckState)message.getData();
				String addr = message.getTransmitterAddress();
				Node node = new Node(addr);
				if (node != null) {
					String msg = message.getText();
					//if receive a message asking to connect, say you are connected
					if (msg.startsWith("connecting")) {
						sendRegisterComplete(node);
					}
					//if you get a node that is connected, put it in the nodes set
					else if (msg.startsWith("connected")) {
						//handle connect completion
						
						boolean isRegistered = false;
						for (Node n : nodes) {
							
							if (n.getAddress().equalsIgnoreCase(node.getAddress())) {
								isRegistered = true;
								break;
							}
//							if (n != node) {
//								if (((DeckState) message.getData()).ID == GAME_ID) {
//									temp.add(node);
//									Context context = getApplicationContext();
//									Toast toast = Toast.makeText(context, "Connected to node " + node.getAddress() + ".", TOAST_DURATION);
//									toast.setGravity(Gravity.BOTTOM, 0, 0);
//									toast.show();
//								}
//							}
						}
						if(!isRegistered) {
							Log.d(TAG, "node added");
							nodes.add(node);
						}
					}
				}
				if (layoutID == R.layout.play_view) {
					uiHandler.post(new Runnable() {
						@Override
						public void run() {
							//TODO: call the interpretation function here.  May need to update UI
			//				logAdapter.add(from + ": " + text);
			//				logAdapter.notifyDataSetChanged();
							//mDeckState = (DeckState)message.getData();
							//refreshPhase();
							setPhase();
							
						}
					});
					
				}
				
				
			}
			
			//final Message m = (DeckState)message.getData();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendRegisterMessage(){
		Log.d(TAG, "sendRegisterMessage()");
		connectionService.broadcastMessage("connecting", mDeckState);
	}
	
	public void sendRegisterComplete(Node n){
		Log.d(TAG, "sendRegisterComplete()");
		connectionService.sendMessage(n, "connected", mDeckState);
	}
	
	public void sendMessage(){

		connectionService.broadcastMessage(mDeckState);
	}
	/*
     * END FROM DEMO
     */
   
    private int getMyPlayerNumber(String myAddress) {
    	int playerNum = 0;
    	for(Node n : nodes){
    		if(myAddress.compareTo(n.getAddress()) > 0) { //myAddress is greater than other address
    			playerNum++;
    		}
    	}
    	return playerNum;
    }
    
}