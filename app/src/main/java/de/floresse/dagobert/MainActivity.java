package de.floresse.dagobert;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ViewFlipper;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends Activity
        implements MediaPlayer.OnCompletionListener {

    public static final String TAG = "dagobert";
    public static final String filename = "dagoprefs.txt";

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;

    private int R_arrow_back;
    private int R_arrow_forward;

    private ViewFlipper flipper;
    private DagosPlan dp = null;
    private DagosPlan dp_alt = null;
    private MenuItem mnitemfor = null;
    private MenuItem mnitemback = null;
    private MediaPlayer mp = null;

    private String timestamp;
    private String timestamp_vergl;
    private Boolean newFile = false;
    private float initialX;

    private int driveFilecount = 0;
    private String driveFileId;

    private DriveServiceHelper mDriveServiceHelper;

    // Preferences
    private Boolean pref_isFixSatz = false;
    private String pref_Tagessatz = null;
    private Boolean pref_isSoundOn = false;
    private Boolean pref_isSound_Aua = false;
    private Boolean pref_isSound_haha = false;
    private Boolean pref_isSound_moepse = false;
    private Boolean pref_isSound_werner = false;
    private String pref_fileuri = null;
    private ImageView locButton = null;
    private Animation rotate = null;
    private Context context = null;

    private int animation_variante = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String themeName = pref.getString("pref_theme", "AppTheme");
        if (themeName.equals("AppTheme")) {
            setTheme(R.style.AppTheme);
            getApplication().setTheme(R.style.AppTheme);
            R_arrow_back = R.drawable.arrow_back_white;
            R_arrow_forward = R.drawable.arrow_forward_white;
        }
        if (themeName.equals("AppTheme1")) {
            setTheme(R.style.AppTheme1);
            getApplication().setTheme(R.style.AppTheme1);
            R_arrow_back = R.drawable.arrow_back_white;
            R_arrow_forward = R.drawable.arrow_forward_white;
        }
        if (themeName.equals("AppTheme2")) {
            setTheme(R.style.AppTheme2);
            getApplication().setTheme(R.style.AppTheme2);
            R_arrow_back = R.drawable.arrow_back_black;
            R_arrow_forward = R.drawable.arrow_forward_black;
        }

        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        context = getApplicationContext();
        setContentView(R.layout.dagoberts_plan);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        setActionBar(toolbar);

        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Log.i(TAG, "Display realSize x :" + outSize.x + " y :" + outSize.y);

        Configuration config =  getResources().getConfiguration();
        Log.i(TAG, "Display width      :" + config.screenWidthDp);
        Log.i(TAG, "Display height     :" + config.screenHeightDp);

        ConstraintLayout grid1 = (ConstraintLayout)findViewById(R.id.Grid1);
        ConstraintLayout grid2 = (ConstraintLayout)findViewById(R.id.Grid2);

        flipper = (ViewFlipper)findViewById(R.id.pageFlipper);
        flipper.setAnimation(AnimationUtils.loadAnimation(this,R.anim.explode));
        flipper.setDisplayedChild(2);

        dp = new DagosPlan();
        dp_alt = new DagosPlan();

        getPrefs();

        setTextChangedListeners();

        Log.i(TAG, "Dago onCreate");

        requestSignIn();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Dago onStart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Dago onResume ");
        getPrefs();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "Dago onRestart ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Dago onPause");
        dp.setDisplayedChild(flipper.getDisplayedChild());

        //PreferenceManager.getDefaultSharedPreferences(this).edit()
        //        .putString("pref_fileid", pref_fileid).apply();

        if (!dp.equals(dp_alt)) {
            Log.i(TAG, "files are not equal : writeDriveFile");
            if (newFile) {
                //hier create und save
                createFile();
            } else {
                //readTimestamp und save
                readTimestamp(driveFileId);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Dago onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Dago onDestroy");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                if (initialX > finalX + 50) {
                    if (!(flipper.getDisplayedChild() == 1)) {
                        onForwardMenu(null);
                    }
                }
                if (initialX < finalX - 50) {
                    if (!(flipper.getDisplayedChild() == 0)) {
                        onBackwardMenu(null);
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mnitemfor = menu.findItem(R.id.menu_forward);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotation);
        locButton = (ImageView) menu.findItem(R.id.menu_forward).getActionView();
        locButton.setImageResource(R_arrow_forward);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flipper.getDisplayedChild()==0) {
                    onForwardMenu(null);
                } else {
                    onBackwardMenu(null);
                }
            }
        });

        rotate.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {

                if (flipper.getDisplayedChild()==0) {
                    locButton.setImageResource(R_arrow_forward);
                } else {
                    locButton.setImageResource(R_arrow_back);
                }

                switch (animation_variante) {
                    case 1:
                        flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_left));
                        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.out_to_right));
                        flipper.setDisplayedChild(0);
                        locButton.startAnimation(rotate);
                        break;
                    default:
                        //...
                        break;
                }
                animation_variante=0;
            }
        });
        mnitemback = menu.findItem(R.id.menu_backward);
        updateMenu();
        return true;
    }

    public void getPrefs() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        pref_isFixSatz = sharedPref.getBoolean("pref_isFixSatz", false);
        pref_Tagessatz = sharedPref.getString("pref_key_FixSatz", "0");
        pref_isSoundOn = sharedPref.getBoolean("pref_isSoundOn", false);
        pref_isSound_Aua = sharedPref.getBoolean("pref_isSound_Aua", false);
        pref_isSound_haha = sharedPref.getBoolean("pref_isSound_haha", false);
        pref_isSound_moepse = sharedPref.getBoolean("pref_isSound_moepse",
                false);
        pref_isSound_werner = sharedPref.getBoolean("pref_isSound_werner",
                false);
        EditText mETSatz = (EditText) findViewById(R.id.contSatz);
        if (pref_isFixSatz) {
            dp.setSatz(pref_Tagessatz);
            dp_alt.setSatz(pref_Tagessatz);
            mETSatz.setEnabled(false);
        } else {
            mETSatz.setEnabled(true);
        }
        //pref_fileid = sharedPref.getString("pref_fileid", "");
    }

    private void initMap() {
        //flipper.removeView(findViewById(R.id.Grid0));
        animation_variante=0;
        showValues();
        showResults();
        Log.i(TAG, "flipper displayedChild : " + flipper.getDisplayedChild());
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_right));
        if (dp.getDisplayedChild() == 0) {
            flipper.setDisplayedChild(1);
            animation_variante=1;
            locButton.startAnimation(rotate);
        } else {
            locButton.startAnimation(rotate);
            flipper.setDisplayedChild(1);
        }
        updateMenu();
        dp_alt = new DagosPlan(dp);
    }

    private void setTextChangedListeners() {

        new myTextWatcherWrap(R.id.contKontostand) {
            @Override
            public void setText(Editable s) {
                dp.setKontostand(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contMiete) {
            @Override
            public void setText(Editable s) {
                dp.setMiete(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contStrom) {
            @Override
            public void setText(Editable s) {
                dp.setStrom(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contTelefon) {
            @Override
            public void setText(Editable s) {
                dp.setTelefon(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contKonto) {
            @Override
            public void setText(Editable s) {
                dp.setKonto(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contZeitung) {
            @Override
            public void setText(Editable s) {
                dp.setZeitung(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contVersicherung) {
            @Override
            public void setText(Editable s) {
                dp.setVersicherung(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contRechnungen) {
            @Override
            public void setText(Editable s) {
                dp.setRechnungen(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contTVGeb) {
            @Override
            public void setText(Editable s) {
                dp.setTVGeb(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contSonstAus1) {
            @Override
            public void setText(Editable s) {
                dp.setSonstAus1(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contSonstAus2) {
            @Override
            public void setText(Editable s) {
                dp.setSonstAus2(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contKontostandNeu) {
            @Override
            public void setText(Editable s) {
                dp.setKontostandNeu(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contGehalt) {
            @Override
            public void setText(Editable s) {
                dp.setGehalt(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contGutschriften) {
            @Override
            public void setText(Editable s) {
                dp.setGutschriften(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contSonstEin1) {
            @Override
            public void setText(Editable s) {
                dp.setSonstEin1(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contSonstEin2) {
            @Override
            public void setText(Editable s) {
                dp.setSonstEin2(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contBares) {
            @Override
            public void setText(Editable s) {
                dp.setBares(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contTage) {
            @Override
            public void setText(Editable s) {
                dp.setTage(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.contSatz) {
            @Override
            public void setText(Editable s) {
                dp.setSatz(s.length() > 0 ? s.toString() : "0");
            }
        };

        new myTextWatcherWrap(R.id.erlMiete) {
            @Override
            public void setText(Editable s) {
                dp.seterlMiete(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlStrom) {
            @Override
            public void setText(Editable s) {
                dp.seterlStrom(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlTelefon) {
            @Override
            public void setText(Editable s) {
                dp.seterlTelefon(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlKonto) {
            @Override
            public void setText(Editable s) {
                dp.seterlKonto(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlZeitung) {
            @Override
            public void setText(Editable s) {
                dp.seterlZeitung(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlVersicherung) {
            @Override
            public void setText(Editable s) {
                dp.seterlVersicherung(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlRechnungen) {
            @Override
            public void setText(Editable s) {
                dp.seterlRechnungen(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlTVGeb) {
            @Override
            public void setText(Editable s) {
                dp.seterlTVGeb(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlSonstAus1) {
            @Override
            public void setText(Editable s) {
                dp.seterlSonstAus1(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlSonstAus2) {
            @Override
            public void setText(Editable s) {
                dp.seterlSonstAus2(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlGehalt) {
            @Override
            public void setText(Editable s) {
                dp.seterlGehalt(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlGutschriften) {
            @Override
            public void setText(Editable s) {
                dp.seterlGutschriften(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlSonstEin1) {
            @Override
            public void setText(Editable s) {
                dp.seterlSonstEin1(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlSonstEin2) {
            @Override
            public void setText(Editable s) {
                dp.seterlSonstEin2(s.toString());
            }
        };

        new myTextWatcherWrap(R.id.erlBares) {
            @Override
            public void setText(Editable s) {
                dp.seterlBares(s.toString());
            }
        };

    } // end function setTextChangedListeners

    public void checkETCB(int i, EditText et, CheckBox cb) {
        if (i == 1) {
            cb.setChecked(false);
            et.setEnabled(true);
        } else {
            cb.setChecked(true);
            et.setEnabled(false);
        }
    }

    public void showValues() {
        // alle Eingaben belegen
        EditText mETKontostand = (EditText) findViewById(R.id.contKontostand);
        mETKontostand.setText(dp.getKontostand());

        EditText mETMiete = (EditText) findViewById(R.id.contMiete);
        mETMiete.setText(dp.getMiete());
        CheckBox cbMiete = (CheckBox) findViewById(R.id.CBMiete);
        checkETCB(dp.getFaMiete(), mETMiete, cbMiete);

        EditText mETStrom = (EditText) findViewById(R.id.contStrom);
        mETStrom.setText(dp.getStrom());
        CheckBox cbStrom = (CheckBox) findViewById(R.id.CBStrom);
        checkETCB(dp.getFaStrom(), mETStrom, cbStrom);

        EditText mETTelefon = (EditText) findViewById(R.id.contTelefon);
        mETTelefon.setText(dp.getTelefon());
        CheckBox cbTelefon = (CheckBox) findViewById(R.id.CBTelefon);
        checkETCB(dp.getFaTelefon(), mETTelefon, cbTelefon);

        EditText mETKonto = (EditText) findViewById(R.id.contKonto);
        mETKonto.setText(dp.getKonto());
        CheckBox cbKonto = (CheckBox) findViewById(R.id.CBKonto);
        checkETCB(dp.getFaKonto(), mETKonto, cbKonto);

        EditText mETZeitung = (EditText) findViewById(R.id.contZeitung);
        mETZeitung.setText(dp.getZeitung());
        CheckBox cbZeitung = (CheckBox) findViewById(R.id.CBZeitung);
        checkETCB(dp.getFaZeitung(), mETZeitung, cbZeitung);

        EditText mETVersicherung = (EditText) findViewById(R.id.contVersicherung);
        mETVersicherung.setText(dp.getVersicherung());
        CheckBox cbVersicherung = (CheckBox) findViewById(R.id.CBVersicherung);
        checkETCB(dp.getFaVersicherung(), mETVersicherung, cbVersicherung);

        EditText mETRechnungen = (EditText) findViewById(R.id.contRechnungen);
        mETRechnungen.setText(dp.getRechnungen());

        EditText mETTVGeb = (EditText) findViewById(R.id.contTVGeb);
        mETTVGeb.setText(dp.getTVGeb());

        EditText mETSonstAus1 = (EditText) findViewById(R.id.contSonstAus1);
        mETSonstAus1.setText(dp.getSonstAus1());

        EditText mETSonstAus2 = (EditText) findViewById(R.id.contSonstAus2);
        mETSonstAus2.setText(dp.getSonstAus2());

        EditText mETKontostandNeu = (EditText) findViewById(R.id.contKontostandNeu);
        mETKontostandNeu.setText(dp.getKontostandNeu());

        EditText mETGehalt = (EditText) findViewById(R.id.contGehalt);
        mETGehalt.setText(dp.getGehalt());
        CheckBox cbGehalt = (CheckBox) findViewById(R.id.CBGehalt);
        checkETCB(dp.getFaGehalt(), mETGehalt, cbGehalt);

        EditText mETGutschriften = (EditText) findViewById(R.id.contGutschriften);
        mETGutschriften.setText(dp.getGutschriften());
        CheckBox cbGutschriften = (CheckBox) findViewById(R.id.CBGutschriften);
        checkETCB(dp.getFaGutschriften(), mETGutschriften, cbGutschriften);

        EditText mETSonstEin1 = (EditText) findViewById(R.id.contSonstEin1);
        mETSonstEin1.setText(dp.getSonstEin1());

        EditText mETSonstEin2 = (EditText) findViewById(R.id.contSonstEin2);
        mETSonstEin2.setText(dp.getSonstEin2());

        EditText mETBares = (EditText) findViewById(R.id.contBares);
        mETBares.setText(dp.getBares());

        EditText mETTage = (EditText) findViewById(R.id.contTage);
        mETTage.setText(dp.getTage());

        EditText mETSatz = (EditText) findViewById(R.id.contSatz);
        mETSatz.setText(dp.getSatz());

        EditText mETerlMiete = (EditText) findViewById(R.id.erlMiete);
        mETerlMiete.setText(dp.geterlMiete());

        EditText mETerlStrom = (EditText) findViewById(R.id.erlStrom);
        mETerlStrom.setText(dp.geterlStrom());

        EditText mETerlTelefon = (EditText) findViewById(R.id.erlTelefon);
        mETerlTelefon.setText(dp.geterlTelefon());

        EditText mETerlKonto = (EditText) findViewById(R.id.erlKonto);
        mETerlKonto.setText(dp.geterlKonto());

        EditText mETerlZeitung = (EditText) findViewById(R.id.erlZeitung);
        mETerlZeitung.setText(dp.geterlZeitung());

        EditText mETerlVersicherung = (EditText) findViewById(R.id.erlVersicherung);
        mETerlVersicherung.setText(dp.geterlVersicherung());

        EditText mETerlRechnungen = (EditText) findViewById(R.id.erlRechnungen);
        mETerlRechnungen.setText(dp.geterlRechnungen());

        EditText mETerlTVGeb = (EditText) findViewById(R.id.erlTVGeb);
        mETerlTVGeb.setText(dp.geterlTVGeb());

        EditText mETerlSonstAus1 = (EditText) findViewById(R.id.erlSonstAus1);
        mETerlSonstAus1.setText(dp.geterlSonstAus1());

        EditText mETerlSonstAus2 = (EditText) findViewById(R.id.erlSonstAus2);
        mETerlSonstAus2.setText(dp.geterlSonstAus2());

        EditText mETerlGehalt = (EditText) findViewById(R.id.erlGehalt);
        mETerlGehalt.setText(dp.geterlGehalt());

        EditText mETerlGutschriften = (EditText) findViewById(R.id.erlGutschriften);
        mETerlGutschriften.setText(dp.geterlGutschriften());

        EditText mETerlSonstEin1 = (EditText) findViewById(R.id.erlSonstEin1);
        mETerlSonstEin1.setText(dp.geterlSonstEin1());

        EditText mETerlSonstEin2 = (EditText) findViewById(R.id.erlSonstEin2);
        mETerlSonstEin2.setText(dp.geterlSonstEin2());

        EditText mETerlBares = (EditText) findViewById(R.id.erlBares);
        mETerlBares.setText(dp.geterlBares());

    } // end function showValues()

    public void showResults() {
        TextView mETSummeAus = (TextView) findViewById(R.id.contSummeAus);
        mETSummeAus.setText(dp.getSummeAus());

        TextView mETSummeEin = (TextView) findViewById(R.id.contSummeEin);
        mETSummeEin.setText(dp.getSummeEin());

        TextView mETBedarf = (TextView) findViewById(R.id.contBedarf);
        mETBedarf.setText(dp.getBedarf());

        TextView mETKopfhau = (TextView) findViewById(R.id.contKopfhau);
        //mETKopfhau.setTextColor(Color.WHITE);
        if (dp.getKopfhau().length() > 1) {
            if (dp.getKopfhau().substring(0, 1).compareTo("-") == 0) {
                //mETKopfhau.setTextColor(Color.RED);
            }
        }
        mETKopfhau.setText(dp.getKopfhau());

        if (getActionBar()!=null) {
            getActionBar().setTitle("Dago : " + dp.getKopfhau() + " €");
        }

    } // end function showResults()

    // im Layout aktiviert:
    public void onClickCBMiete(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaMiete(0);
        } else {
            dp.setFaMiete(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickCBStrom(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaStrom(0);
        } else {
            dp.setFaStrom(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickCBTelefon(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaTelefon(0);
        } else {
            dp.setFaTelefon(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickCBKonto(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaKonto(0);
        } else {
            dp.setFaKonto(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickCBZeitung(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaZeitung(0);
        } else {
            dp.setFaZeitung(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickCBVersicherung(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaVersicherung(0);
        } else {
            dp.setFaVersicherung(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickCBGehalt(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaGehalt(0);
        } else {
            dp.setFaGehalt(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickCBGutschriften(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            dp.setFaGutschriften(0);
        } else {
            dp.setFaGutschriften(1);
        }
        showValues();
        showResults();
        //
    }

    // im Layout aktiviert:
    public void onClickForward(View view) {

        onForwardMenu(null);
    }

    // im Layout aktiviert:
    public void onClickBackward(View view) {

        onBackwardMenu(null);
    }

    // im Menu Layout aktiviert:
    public void onForwardMenu(MenuItem item) {
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_left));
        locButton.startAnimation(rotate);
        flipper.showNext();
        updateMenu();

    }

    // im Menu Layout aktiviert:
    public void onBackwardMenu(MenuItem item) {
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_to_right));
        locButton.startAnimation(rotate);
        flipper.showPrevious();
        updateMenu();

    }

    public void viewInf(View v) {
        Log.i(TAG, "SizeMeas  x:" + v.getMeasuredWidth() + " y:" + v.getMeasuredHeight());
        Log.i(TAG, "Size      x:" + v.getWidth() + " y:" + v.getHeight());
        Log.i(TAG, "Scale     x:" + v.getScaleX() + " y:" + v.getScaleY());
    }

    // im Menu Layout aktiviert:
    public void onMinus1(MenuItem item) {
        dp.minus1();
        showValues();
        showResults();
        Toast.makeText(this, "minus 1", Toast.LENGTH_LONG).show();
    }

    public void updateMenu() {
        /*
        mnitemfor.setEnabled(true);
        mnitemfor.setVisible(true);
        mnitemback.setEnabled(false);
        mnitemback.setVisible(false);
        */
    } // end function updateMenu

    // im Menu Layout aktiviert:
    public void onSaveClicked(MenuItem item) {

        if (newFile) {
            //hier create und save
            createFile();
        } else {
            //readTimestamp und save
            readTimestamp(driveFileId);
        }
        dp_alt=dp;

    }

    // im Layout aktiviert:
    public void onDiscardClicked(MenuItem item) {
        dp = new DagosPlan(dp_alt);
        Toast.makeText(this, "discarded", Toast.LENGTH_LONG).show();
        showValues();
        showResults();
    }

    // im Layout aktiviert:
    public void onSettingsClicked(MenuItem item) {
        // Log.i(TAG, "Dago starting Settings ?");
        Intent intent = new Intent(this, DagosSettingsActivity.class);
        startActivity(intent);
    }

    // im Layout aktiviert:
    public void onMinus1Clicked(View v) {
        onMinus1(null);
    }

    // im Layout aktiviert:
    public void onDollarsClicked(View v) {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("de.floresse.pluto");
        startActivity(intent);
    }

    public void onKopfHauClicked(View v) {
        if (pref_isSoundOn) {
            if (pref_isSound_Aua) {
                mp = MediaPlayer.create(this, R.raw.au3);
                mp.setOnCompletionListener(this);
                mp.start();
            }
            if (pref_isSound_haha) {
                mp = MediaPlayer.create(this, R.raw.haha);
                mp.setOnCompletionListener(this);
                mp.start();
            }
            if (pref_isSound_moepse) {
                mp = MediaPlayer.create(this, R.raw.moepse);
                mp.setOnCompletionListener(this);
                mp.start();
            }
            if (pref_isSound_werner) {
                mp = MediaPlayer.create(this, R.raw.werner);
                mp.setOnCompletionListener(this);
                mp.start();
            }
        }

        //View animview = findViewById(R.id.Grid2);
        //animview.startAnimation(new Animation3DRotate(animview.getWidth()/2,animview.getHeight()/2,
        //		                    2500, 360, false ));
        v.startAnimation(new Animation3DRotate(v.getWidth()/2,v.getHeight()/2,
                2500, 360, false ));

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    if (resultData != null) {
                        Uri uri = resultData.getData();
                        if (uri != null) {
                            openFileFromFilePicker(uri);
                        }
                    } else {
                        //TODO neu anlegen

                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Dagobert")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);

                    queryFile();
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }

    /**
     * Opens the Storage Access Framework file picker using {@link #REQUEST_CODE_OPEN_DOCUMENT}.
     */
    private void openFilePicker() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.");

            Intent pickerIntent = mDriveServiceHelper.createFilePickerIntent();

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT);
        }
    }

    /**
     * Opens a file from its {@code uri} returned from the Storage Access Framework file picker
     * initiated by {@link #openFilePicker()}.
     */
    private void openFileFromFilePicker(Uri uri) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening " + uri.getPath());

            mDriveServiceHelper.openFileUsingStorageAccessFramework(getContentResolver(), uri)
                    .addOnSuccessListener(nameAndTSAndContent -> {
                        String name = (String) nameAndTSAndContent.get(0);
                        String timestamp = (String) nameAndTSAndContent.get(1);
                        DagosPlan dp = (DagosPlan) nameAndTSAndContent.get(2);
                        try {

                        } catch (Exception e) {

                        }
                        Log.i(TAG, "Name ist : " + name);
                        Log.i(TAG, "Kontostand ist : " + dp.getKontostand());
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to open file from picker.", exception));
        }
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void createFile() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.");

            mDriveServiceHelper.createFile(filename)
                    .addOnSuccessListener(fileId -> saveFile(fileId))
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
        }
    }

    private void readFile(String fileId) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Reading file " + fileId);

            mDriveServiceHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndTsAndContent -> {
                        String name = (String) nameAndTsAndContent.get(0);
                        timestamp = (String) nameAndTsAndContent.get(1);
                        dp = (DagosPlan) nameAndTsAndContent.get(2);

                        initMap();

                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't read file.", exception));
        }
    }

    private void readTimestamp(String fileId) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Reading timestamp from file " + fileId);

            mDriveServiceHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndTsAndContent -> {
                        timestamp_vergl = (String) nameAndTsAndContent.get(1);
                        if (timestamp.equals(timestamp_vergl)) {
                            saveFile(fileId);
                        } else {
                            Toast.makeText(this, "von anderem Dagobert überholt", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't read file.", exception));
        }
    }

    private void saveFile(String fileId) {
        if (mDriveServiceHelper != null && fileId != null) {
            Log.d(TAG, "Saving " + fileId);

            timestamp = new Timestamp(new Date().getTime()).toString();

            mDriveServiceHelper.saveFile(fileId, filename, dp, timestamp)
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to save file via REST.", exception));
        }
    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private void queryFile() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");
            driveFileId="";
            driveFilecount=0;
            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(fileList -> {
                        for (File file : fileList.getFiles()) {
                            Log.i(TAG, "queryFile filename : " + file.getName() + " " + file.getId());
                            if (file.getName().equalsIgnoreCase(filename)) {
                                driveFileId = file.getId();
                                driveFilecount++;
                            }
                        }
                        Log.i(TAG, filename + " count : " + driveFilecount);
                        switch(driveFilecount) {
                            case 0:
                                newFile=true;
                                initMap();
                                break;
                            case 1:
                                newFile=false;
                                readFile(driveFileId);
                                break;
                            default:
                                newFile=false;
                                Log.i(TAG, "Drive: zu viele Treffer");
                                Toast.makeText(this, "zu viele Treffer " + filename, Toast.LENGTH_LONG).show();
                                //TODO hier FilePicker aufrufen und irg.wie FileId rauskriegen
                        }
                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }

    private abstract class myTextWatcherWrap {
        public myTextWatcherWrap(int resID) {
            ((EditText)findViewById(resID)).addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    setText(s);
                    showResults();
                }
            });

        }

        public abstract void setText(Editable s);

    }  // end class TextWatcherWrap



}  // end class MainActivity
