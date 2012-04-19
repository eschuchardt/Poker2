package schuchardt.evin.poker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	//Debug
	private final static boolean D = true;
	private final static String TAG = "MainActivity";
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(D) Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button play = (Button)findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(D) Log.d(TAG, "in onClick");
				Intent i = new Intent(MainActivity.this, Poker2Activity.class);
				startActivity(i);
			}
		});
    }
}
