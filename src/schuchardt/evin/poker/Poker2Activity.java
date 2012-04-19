package schuchardt.evin.poker;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Poker2Activity extends Activity {
	
	//Debug
	private final static boolean D = true;
	private final static String TAG = "Poker2Activity";
	
	TextView card1;
    TextView card2;
    TextView card3;
    TextView card4;
    TextView card5;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(D) Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        //if(D) Log.d(TAG, "success super.onCreate(savedInstanceState);");
        setContentView(R.layout.play_view);
        //if(D) Log.d(TAG, "success setContentView(R.layout.play_view);");
        
        
        /*TextView */card1 = (TextView)findViewById(R.id.card1);
        /*TextView */card2 = (TextView)findViewById(R.id.card2);
        /*TextView */card3 = (TextView)findViewById(R.id.card3);
        /*TextView */card4 = (TextView)findViewById(R.id.card4);
        /*TextView */card5 = (TextView)findViewById(R.id.card5);
        
        card1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(Poker2Activity.this, MainActivity.class);
//				startActivity(i);
//				int one = card1.getBackground().getConstantState().hashCode();
//				int two = getResources().getDrawable(R.drawable.green_card).getConstantState().hashCode();
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
        
    }
}