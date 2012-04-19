package schuchardt.evin.poker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Poker2Activity extends Activity {
	
	//Debug
	private final static boolean D = true;
	private final static String TAG = "Poker2Activity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(D) Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        if(D) Log.d(TAG, "success super.onCreate(savedInstanceState);");
        setContentView(R.layout.play_view);
        if(D) Log.d(TAG, "success setContentView(R.layout.play_view);");
        
        
        ImageView card1 = (ImageView)findViewById(R.id.card1);
        card1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Poker2Activity.this, MainActivity.class);
				startActivity(i);
			}
		});
    }
}