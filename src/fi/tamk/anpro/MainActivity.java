package fi.tamk.anpro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
    public static Context context;
    
    private boolean settingsLoaded = false;
    
    public static final String PREFS_NAME = "SharedPrefs";
    public static final String PREF_STRING = "PrefString";
    public static final String PREF_BOOL_PAR = "PrefBoolPar";
    public static final String PREF_BOOL_MUS = "PrefBoolMUS";
    public static final String PREF_BOOL_SOU = "PrefBoolSOU";
    private SharedPreferences mPrefs;
    
    private CheckBox particleCheckBox;
    private CheckBox musicCheckBox;
    private CheckBox soundCheckBox;
    
    /** Kutsutaan kun aktiviteetti luodaan. */
    @Override
    public void onCreate(Bundle _savedInstanceState)
    {	
        super.onCreate(_savedInstanceState);
        
        setContentView(R.layout.main);
        
        context = getApplicationContext();
        mPrefs = getSharedPreferences(PREFS_NAME,0);
        
        // Ladataan n�yt�n tiedot
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        // Ladataan Options k�ytt��n
        Options.getInstance().scaleConversion(dm.widthPixels, dm.heightPixels);

        // Ladataan SoundManager k�ytt��n ja alustetaan se
        SoundManager.getInstance();
        SoundManager.initSounds(this);
        
        // M��ritet��n aktiviteetin asetukset
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        // Ladataan valikon painikkeet k�ytt��n
        View storyButton = findViewById(R.id.button_story);
        storyButton.setOnClickListener(this);
        
        View survivalButton = findViewById(R.id.button_survival);
        survivalButton.setOnClickListener(this);
        
        View helpButton = findViewById(R.id.button_help);
        helpButton.setOnClickListener(this);
        
        View highscoresButton = findViewById(R.id.button_highscores);
        highscoresButton.setOnClickListener(this);
        
        View quitButton = findViewById(R.id.button_quit);
        quitButton.setOnClickListener(this);
        
        particleCheckBox = (CheckBox) findViewById(R.id.checkBoxParticle);
        particleCheckBox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks, depending on whether it's now checked
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(MainActivity.this, "Particles Enabled", Toast.LENGTH_SHORT).show();
                    // TODO Options.settings[0]=true;
                } else {
                    Toast.makeText(MainActivity.this, "Particles Disabled", Toast.LENGTH_SHORT).show();
                    // TODO  Options.settings[0]=false;
                }
            }
        });
        
        musicCheckBox = (CheckBox) findViewById(R.id.checkBoxMusic);
        musicCheckBox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks, depending on whether it's now checked
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(MainActivity.this, "Music Enabled", Toast.LENGTH_SHORT).show();
                    // TODO Options.settings[1]=false;
                    
                } else {
                    Toast.makeText(MainActivity.this, "Music Disabled", Toast.LENGTH_SHORT).show();
                    // TODO Options.settings[1]=false;
                }
            }
        });
        
        soundCheckBox = (CheckBox) findViewById(R.id.checkBoxSound);
        soundCheckBox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks, depending on whether it's now checked
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(MainActivity.this, "Sounds Enabled", Toast.LENGTH_SHORT).show();
                    // TODO Options.settings[2]=false;
                } else {
                    Toast.makeText(MainActivity.this, "Sounds Disabled", Toast.LENGTH_SHORT).show();
                    // TODO Options.settings[2]=false;
                }
            }
        });

        // ladataan asetukset, jos niit� ei ole ladattu 
        if (!settingsLoaded) {
            XmlReader reader = new XmlReader(getBaseContext());
            boolean[] settingsTemp = reader.readSettings();
            
            particleCheckBox.setChecked(settingsTemp[0]);
            musicCheckBox.setChecked(settingsTemp[1]);
            soundCheckBox.setChecked(settingsTemp[2]);
            
            settingsLoaded = true;
        }   
    }
    
    public void onClick(View _v) {
        switch(_v.getId()) {
            case R.id.button_story:
                SoundManager.playSound(2, 1);
                Intent i_story = new Intent(this, LevelSelectActivity.class);
                startActivity(i_story);
                super.onStop();
                break;
                
            case R.id.button_survival:
                SoundManager.playSound(2, 1);
                Intent i_game = new Intent(this, GameActivity.class);
                startActivity(i_game);
                super.onStop();
                break;
                
            case R.id.button_highscores:
                SoundManager.playSound(2, 1);
                Intent i_highscores = new Intent(this, HighScoresActivity.class);
                startActivity(i_highscores);
                super.onStop();
                break;
                
            case R.id.button_help:
                SoundManager.playSound(2, 1);
                Intent i_help = new Intent(this, AboutActivity.class);
                startActivity(i_help);
                super.onPause();
                break;
                
            case R.id.button_quit:
                SoundManager.playSound(2, 1);
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }
    
    @Override
    protected void onResume() {
    	particleCheckBox.setChecked(mPrefs.getBoolean(PREF_BOOL_PAR,false));
        musicCheckBox.setChecked(mPrefs.getBoolean(PREF_BOOL_MUS, false));
        soundCheckBox.setChecked(mPrefs.getBoolean(PREF_BOOL_SOU, false));
        super.onResume();
    }
    
    @Override
    protected void onPause() {
    	Editor e = mPrefs.edit();
    	e.putBoolean(PREF_BOOL_PAR, particleCheckBox.isChecked());
    	e.putBoolean(PREF_BOOL_MUS, musicCheckBox.isChecked());
    	e.putBoolean(PREF_BOOL_SOU, soundCheckBox.isChecked());
    	e.commit();
    	
    	Toast.makeText(this, "Settings Saved.", Toast.LENGTH_SHORT).show();
    	super.onPause();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
}
