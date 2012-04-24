package schuchardt.evin.poker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import ec.nem.bluenet.BuildNetworkActivity;

public class MainActivity extends Activity {
	
	//Debug
	private final static boolean D = true;
	private final static String TAG = "MainActivity";
		
	private static final int RESULT_BUILD_NETWORK = 3478344;
	
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
//				Intent i = new Intent(MainActivity.this, Poker2Activity.class);
				Intent i = new Intent(MainActivity.this, BuildNetworkActivity.class);
//				startActivity(i);
				startActivityForResult(i, RESULT_BUILD_NETWORK);
			}
		});
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == RESULT_BUILD_NETWORK){
			if(resultCode == RESULT_OK){
				Intent intent = new Intent(this, Poker2Activity.class);
				startActivity(intent);
			}
			else if(resultCode == RESULT_CANCELED){
				// Could not connect.
			}
		}
	}
}
