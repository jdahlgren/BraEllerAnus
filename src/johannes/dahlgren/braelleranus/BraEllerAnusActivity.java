package johannes.dahlgren.braelleranus;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import johannes.dahlgren.braelleranus.R;
import johannes.dahlgren.braelleranus.R.color;
import johannes.dahlgren.braelleranus.R.string;

public class BraEllerAnusActivity extends Activity {
	
	BigTextButton braAnusButton;
	boolean id;
	private View mDecorView;
	Handler mHideHandler;
	Runnable mHideRunnable;
	Locale locale;
	SharedPreferences settings;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        settings = getSharedPreferences("prefs", 0);
        String loc = settings.getString("savedLoc", Locale.getDefault().toString());

        locale = new Locale(loc); 
		Resources res = getResources(); 
		DisplayMetrics dm = res.getDisplayMetrics(); 
		Configuration conf = res.getConfiguration(); 
		conf.locale = locale; 
		res.updateConfiguration(conf, dm); 
                
        setContentView(R.layout.main);
        mHideHandler = new Handler();
        mHideRunnable = new Runnable() {
    		public void run() {
    			hideSystemUI();
    		}
    	};
        
        mDecorView = getWindow().getDecorView();
        hideSystemUI();
        getActionBar().hide();
        
        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			
        	public void onSystemUiVisibilityChange(int visibility) {
        		if(visibility == 0){
        			getActionBar().show();
        			mHideHandler.postDelayed(mHideRunnable, 5000);
        		}		
			}
		});
        
        id = true;
        
        braAnusButton = (BigTextButton) (findViewById(R.id.imageButton1));
        braAnusButton.setBackgroundColor(getResources().getColor(color.braBG));
        braAnusButton.setTexts(getResources().getText(string.bra));
        braAnusButton.setTextColors(getResources().getColor(color.bra));
        
        findViewById(R.id.imageButton1).setOnClickListener(braAnusButtonOnClickHandler);
        findViewById(R.id.imageButton1).setOnLongClickListener(braAnusButtonOnLongClickHandler);
    } 
    
    View.OnClickListener braAnusButtonOnClickHandler = new View.OnClickListener() {    	
        public void onClick(View v) {        	
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
    
    View.OnLongClickListener braAnusButtonOnLongClickHandler = new View.OnLongClickListener() {		
		public boolean onLongClick(View v) {
			braAnusButton.setBackgroundColor(getResources().getColor(color.braAnusBG));
			braAnusButton.setTexts(getResources().getText(string.braAnus));
			braAnusButton.setTextColors(getResources().getColor(color.braAnus));
			id=!id;
			return false;
		}
	};
	
	
	// This snippet hides the system bars.
	private void hideSystemUI() {
	    // Set the IMMERSIVE flag.
	    // Set the content to appear under the system bars so that the content
	    // doesn't resize when the system bars hide and show.
		getActionBar().hide();
	    mDecorView.setSystemUiVisibility(
	    		View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	    		| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            openSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
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