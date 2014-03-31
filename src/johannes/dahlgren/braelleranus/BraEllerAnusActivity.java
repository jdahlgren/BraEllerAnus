package johannes.dahlgren.braelleranus;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import johannes.dahlgren.braelleranus.R;
import johannes.dahlgren.braelleranus.R.color;
import johannes.dahlgren.braelleranus.R.string;

public class BraEllerAnusActivity extends Activity {
	
	private BigTextButton braAnusButton;
	private boolean id;
	private View mDecorView;
	private Handler mHideHandler;
	private Runnable mHideRunnable;
	private Locale locale;
	private SharedPreferences settings;
	private int currentapiVersion;
	private boolean isMenuVisible;
	private boolean longClickActive;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mHideHandler = new Handler();
        mHideRunnable = new Runnable() {
    		public void run() {
    			hideSystemUI();
    		}
    	};
    	
    	mDecorView = getWindow().getDecorView();       
        getActionBar().hide();
        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        isMenuVisible = false;

        hideSystemUI();

        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

        	public void onSystemUiVisibilityChange(int visibility) {
        		if(visibility == 0){
        			//actionBar.show();
        			isMenuVisible = true;
        			mHideHandler.postDelayed(mHideRunnable, 5000);
        		}		
        	}
        });       	

        
        //Handle Locale
        settings = getSharedPreferences("prefs", 0);
        String loc = settings.getString("savedLoc", Locale.getDefault().toString());
        locale = new Locale(loc); 
        
		Resources res = getResources(); 
		DisplayMetrics dm = res.getDisplayMetrics(); 
		Configuration conf = res.getConfiguration(); 
		conf.locale = locale; 
		res.updateConfiguration(conf, dm); 
		//End handle locale
                
        setContentView(R.layout.main);
        
        
        id = true;
        longClickActive = false;
        
        braAnusButton = (BigTextButton) (findViewById(R.id.imageButton1));
        braAnusButton.setBackgroundColor(getResources().getColor(color.braBG));
        braAnusButton.setTexts(getResources().getText(string.bra));
        braAnusButton.setTextColors(getResources().getColor(color.bra));
        
        findViewById(R.id.imageButton1).setOnClickListener(braAnusButtonOnClickHandler);
        findViewById(R.id.imageButton1).setOnTouchListener(braAnusButtonTouchHandler);
    } 
    
    View.OnClickListener braAnusButtonOnClickHandler = new View.OnClickListener() {    	
        public void onClick(View v) {     
        	if(!isMenuVisible){
        		if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT){
        			hideSystemUI();
        		}
        	}
        	if(id)
        	{
        		braAnusButton.setBackgroundColor(getResources().getColor(color.anusBG));
        		braAnusButton.setTexts(getResources().getText(string.anus));
        		braAnusButton.setTextColors(getResources().getColor(color.anus));
        	}
        	else
        	{
        		braAnusButton.setBackgroundColor(getResources().getColor(color.braBG));
        		braAnusButton.setTexts(getResources().getText(string.bra));
        		braAnusButton.setTextColors(getResources().getColor(color.bra));
        	}        	
        	id=!id;
        }        
    };    
   
	View.OnTouchListener braAnusButtonTouchHandler = new View.OnTouchListener() {
		private static final int MIN_CLICK_DURATION = 1000;
	    private long startClickTime;
		
		public boolean onTouch(View v, MotionEvent event) {			
			switch (event.getAction()) {
	        case MotionEvent.ACTION_UP:
	            longClickActive = false;
	            break;
	        case MotionEvent.ACTION_DOWN:
	            if (longClickActive == false) {
	                longClickActive = true;
	                startClickTime = Calendar.getInstance().getTimeInMillis();
	            }
	            break;
	        case MotionEvent.ACTION_MOVE:
	            if (longClickActive == true) {
	            	long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
	            	if(event.getPointerCount() == 3){		                
		                if (clickDuration >= MIN_CLICK_DURATION) {
		                    openSettings();
		                    longClickActive = false;
		                    return true;
		                }
	            	}
	            	else if(event.getPointerCount() != 3 && clickDuration >= MIN_CLICK_DURATION){
	            		braAnusButton.setBackgroundColor(getResources().getColor(color.braAnusBG));
	    				braAnusButton.setTexts(getResources().getText(string.braAnus));
	    				braAnusButton.setTextColors(getResources().getColor(color.braAnus));
	    				id=!id;
	            	}
	            }
	            break;
	        }
	        return false;
		}
	};
	
	private void hideSystemUI() {
	    // Set the IMMERSIVE flag.
	    // Set the content to appear under the system bars so that the content
	    // doesn't resize when the system bars hide and show.
		isMenuVisible = false;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT){
			mDecorView.setSystemUiVisibility(
	    		View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	    		| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN	
	    		| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar		            
	            | View.SYSTEM_UI_FLAG_IMMERSIVE);
		}
		else{
			mDecorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_FULLSCREEN				
				);
		}
	}

	private void openSettings() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.pickLanguage);
		builder.setItems(R.array.lang, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					setLocale("sv");
					break;
				case 1:
					setLocale("en");
					break;
				default:
					break;
				}
			}
		});

		builder.create().show();
	}	
	
	public void setLocale(String lang) { 
		if(!getResources().getConfiguration().locale.toString().startsWith(lang)){

			Intent refresh = getIntent();
			finish();

			settings = getSharedPreferences("prefs", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("savedLoc", lang).commit();

			startActivity(refresh); 
		}
	} 
       
}