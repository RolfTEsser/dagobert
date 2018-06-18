package de.floresse.dagobert;

/*
import de.floresse.util.Animation3DRotate;
import de.floresse.util.Amount;
import de.floresse.util.ScaledFrameLayout;
*/
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;

public class MainActivity extends Activity
						  implements MediaPlayer.OnCompletionListener,
                          ConnectionCallbacks,
                          OnConnectionFailedListener {

	public static final String LogTAG = new String("dagobert");
	public static final String filename = "dagoprefs.txt";

	private ViewFlipper flipper;
	private DagosPlan dp = null;
	private DagosPlan dp_alt = null;
	private MenuItem mnitemfor = null;
	private MenuItem mnitemback = null;
	private MediaPlayer mp = null;
	private Boolean showSmall = false;
	private Float lastScaleFactor = 1f; 
	private ScaleGestureDetector scaleGestureDetector = null;
	private GestureDetector gestureDetector = null;
    private GoogleApiClient mGoogleApiClient;

	private DriveFile driveFile = null;
	private MetadataChangeSet changeSet = null;
	private String timestamp;
	private Boolean newFile = false;
	private float initialX;

    private static final int REQUEST_CODE_RESOLUTION = 3;

	// Preferences
	private Boolean pref_isFixSatz = false;
	private String pref_Tagessatz = null;
	private Boolean pref_isSoundOn = false;
	private Boolean pref_isSound_Aua = false;
	private Boolean pref_isSound_haha = false;
	private Boolean pref_isSound_moepse = false;
	private Boolean pref_isSound_werner = false;
	private Boolean pref_isScaleDisplay = false;
	private String pref_filename = null;
	private ImageView locButton = null;
	private Animation rotate = null;
	//private ViewPager mPager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		setContentView(R.layout.dagoberts_plan);
		//scaleGestureDetector = new ScaleGestureDetector(this, new simpleOnScaleGestureListener());
		//gestureDetector = new GestureDetector(this, new GestureListener());

		//
		Point outSize = new Point(); 
		getWindowManager().getDefaultDisplay().getSize(outSize);
		Log.i(LogTAG, "Display realSize x :" + outSize.x + " y :" + outSize.y);

		Configuration config =  getResources().getConfiguration();
		Log.i(LogTAG, "Display width      :" + config.screenWidthDp);
		Log.i(LogTAG, "Display height     :" + config.screenHeightDp);

		ConstraintLayout grid1 = (ConstraintLayout)findViewById(R.id.Grid1);
		ConstraintLayout grid2 = (ConstraintLayout)findViewById(R.id.Grid2);
		//ScaledFrameLayout flP1 = (ScaledFrameLayout)findViewById(R.id.flP1);
		//ScaledFrameLayout flP2 = (ScaledFrameLayout)findViewById(R.id.flP2);
		
		//HorizontalScrollView svhP1 = (HorizontalScrollView)findViewById(R.id.svhP1);
		//HorizontalScrollView svhP2 = (HorizontalScrollView)findViewById(R.id.svhP2);
		flipper = (ViewFlipper)findViewById(R.id.pageFlipper);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		pref_isScaleDisplay = sharedPref.getBoolean("pref_isScaleDisplay", false);
		lastScaleFactor = sharedPref.getFloat("pref_scaleFactor", 1f);
		pref_filename = sharedPref.getString("pref_filename", filename);
		
		//	if (outSize.x < 800) {
		/*
		if (true) {
			if (pref_isScaleDisplay) {
				//zoom(lastScaleFactor = outSize.x / 800f);
				zoom(lastScaleFactor);
				showSmall = true;
				setOnTouchListeners();
			}
		} else {
		*/
			//flP1.removeAllViews();  // grid1 von parent lösen
			//flP2.removeAllViews();  // grid2 dto.
			//flipper.removeAllViews();
			//flipper.addView(grid1);  // grid1 neu einhängen
			//flipper.addView(grid2);  // grid2 dt.
            /*
			findViewById(R.id.der_plan)
				.setOnTouchListener(new MyOnTouchListener());  // Touch selber handeln 
            */
		//}
		flipper.setAnimation(AnimationUtils.loadAnimation(this,R.anim.explode));

		dp = new DagosPlan();
		dp_alt = new DagosPlan();

		setTextChangedListeners();

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
		//		.addScope(Drive.SCOPE_APPFOLDER)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
        mGoogleApiClient.connect();

		Log.i(LogTAG, "Dago onCreate");
		
		//Toast.makeText(this, "Dago started ", Toast.LENGTH_LONG)
		//       .show()
		       ;
		//
        /*
		//listens for a change in the scrollView and then calls the scrollBy method.
		ViewTreeObserver vto1 = findViewById(R.id.Grid1).getViewTreeObserver();
		vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				Log.i(LogTAG, "Dago onLayout DagosPlan");
				sizeInf(findViewById(R.id.Grid1));
			}});
		ViewTreeObserver vto2 = findViewById(R.id.Grid2).getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				Log.i(LogTAG, "Dago onLayout DagosPlan");
				sizeInf(findViewById(R.id.Grid2));
			}});
		*/
        //if (false) 		mPager.getCurrentItem();
	}

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LogTAG, "Dago onStart ");
    }

    @Override
    public void onConnected(Bundle ch) {
        Log.i(LogTAG, "onConnected: GoogleDrive connected");
        readDriveFile();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LogTAG, "GoogleDrive connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LogTAG, "GoogleDrive connection failed");
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
	protected void onResume() {
		super.onResume();
		Log.i(LogTAG, "Dago onResume ");
		loadFromPrefs("PrefTemp");
		setPrefs();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(LogTAG, "Dago onRestart ");
	}

	@Override
	protected void onPause() {
		super.onPause();
        Log.i(LogTAG, "Dago onPause");
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	sharedPref.edit().putFloat("pref_scaleFactor", lastScaleFactor).commit();
		saveToPrefs("PrefTemp");
		//Log.i(LogTAG, "alt : " + dp_alt.getKontostand() + " neu : " + dp.getKontostand());
		//Log.i(LogTAG, "alt : " + dp_alt.geterlMiete() + " neu : " + dp.geterlMiete());
		if (!dp.equals(dp_alt)) {
			// Log.i(LogTAG, "files are not equal : writeDriveFile");
			writeDriveFile();
		}
 	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(LogTAG, "Dago onStop");
		//mGoogleApiClient.disconnect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(LogTAG, "Dago onDestroy");
		saveToPrefs("PrefTemp");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//scaleGestureDetector.onTouchEvent(event);
		//return gestureDetector.onTouchEvent(event);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		//if (showSmall) {
			getMenuInflater().inflate(R.menu.activity_main_s, menu);
		//} else {
		//	getMenuInflater().inflate(R.menu.activity_main, menu);
		//}
		mnitemfor = menu.findItem(R.id.menu_forward);
		locButton = (ImageView) menu.findItem(R.id.menu_forward).getActionView();
		rotate = AnimationUtils.loadAnimation(this, R.anim.rotation);
		locButton.setImageResource(R.drawable.navigation_forward_s);
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
					locButton.setImageResource(R.drawable.navigation_forward_s);
				} else {
					locButton.setImageResource(R.drawable.navigation_back_s);
				}
			}
		});
		mnitemback = menu.findItem(R.id.menu_backward);
		updateMenu();
		return true;
	}


	public void setPrefs() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		pref_isFixSatz = sharedPref.getBoolean("pref_isFixSatz", false);
		pref_isScaleDisplay = sharedPref.getBoolean("pref_isScaleDisplay", false);
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
			mETSatz.setFocusable(false);
			mETSatz.setTextColor(Color.GRAY);
		} else {
			mETSatz.setFocusable(true);
			mETSatz.setTextColor(Color.WHITE);
		}
		pref_filename = sharedPref.getString("pref_filename", filename);
	}

	public void writeDriveFile() {
		// create new contents resource
		if (newFile){
			Log.i(LogTAG, "creating newDriveContents");
			Drive.DriveApi.newDriveContents(mGoogleApiClient)
					.setResultCallback(driveContentsCallback);
		} else {
			if (driveFile!=null) {
				new RetrieveTSDriveFileContentsAsyncTask().execute(driveFile);
			}
		}
	}

	public void readDriveFile() {
		// create new contents resource
		Query query = new Query.Builder()
				.addFilter(Filters.contains(SearchableField.TITLE, filename))
				.build();
		Drive.DriveApi.query(mGoogleApiClient, query)
				.setResultCallback(metadataCallback);
	}

	// [START drive_contents_callback]
	final private ResultCallback<DriveContentsResult> driveContentsCallback =
			new ResultCallback<DriveContentsResult>() {
				@Override
				public void onResult(DriveContentsResult result) {
					if (!result.getStatus().isSuccess()) {
						Log.i(LogTAG, "Error while trying to create new file contents");
						return;
					}
					changeSet = new MetadataChangeSet.Builder()
							.setTitle(filename)
							.setMimeType("text/plain")
							.build();
					Drive.DriveApi.getRootFolder(mGoogleApiClient)
							.createFile(mGoogleApiClient, changeSet, result.getDriveContents())
							.setResultCallback(fileCallback);
				}
			};
	// [END drive_contents_callback]

	final private ResultCallback<DriveFileResult> fileCallback = new
			ResultCallback<DriveFileResult>() {
				@Override
				public void onResult(DriveFileResult result) {
					if (!result.getStatus().isSuccess()) {
						Log.i(LogTAG, "Error while trying to create the file");
						return;
					}
					Log.i(LogTAG, "Created a file in App Folder: "
							+ result.getDriveFile().getDriveId());
					DriveFile driveFile = result.getDriveFile();
                    new EditContentsAsyncTask().execute(driveFile);
 			    }
			};

	final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
			new ResultCallback<DriveApi.MetadataBufferResult>() {
				@Override
				public void onResult(DriveApi.MetadataBufferResult result) {
					if (!result.getStatus().isSuccess()) {
						Log.e(LogTAG, "Problem while retrieving results");
						return;
					}
					int vers = 0;
					MetadataBuffer mdb = null;
					try {
						mdb = result.getMetadataBuffer();
						for (Metadata md : mdb) {
							if (md == null || !md.isDataValid() || md.isTrashed()) continue;
                            vers++;
							// collect files
                            DriveId driveId = md.getDriveId();
							Log.i(LogTAG, "found " + driveId);
							Drive.DriveApi.fetchDriveId(mGoogleApiClient, driveId.getResourceId())
										.setResultCallback(idCallback);
						}
					} finally {
						if (mdb != null) mdb.release();
					}
                    switch(vers) {
                        case 0:
							newFile=true;
                            break;
                        case 1:
                            newFile=false;
                            break;
                        default:
                        	newFile=false;
                            Log.e(LogTAG, "Drive: zu viele Treffer");
                    }
                    Log.i(LogTAG, "Anz Treffer : " + vers + " " + newFile);
				}
			};

    public class EditContentsAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DriveFile... args) {
            DriveFile file = args[0];
			newFile=false;
            try {
                DriveContentsResult driveContentsResult = file.open(
                        mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
				if (!driveContentsResult.getStatus().isSuccess()) {
					return false;
				}
				String timestamp = new Timestamp(new Date().getTime()).toString();
				MainActivity.this.timestamp = timestamp;

                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream os = driveContents.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);

				Amount.setBlankzero(false);

				oos.writeObject(timestamp);
				oos.writeObject(dp.getKontostand());
				oos.writeObject(dp.getMiete());
				oos.writeObject(dp.getStrom());
				oos.writeObject(dp.getTelefon());
				oos.writeObject(dp.getKonto());
				oos.writeObject(dp.getZeitung());
				oos.writeObject(dp.getVersicherung());
				oos.writeObject(dp.getRechnungen());
				oos.writeObject(dp.getTVGeb());
				oos.writeObject(dp.getSonstAus1());
				oos.writeObject(dp.getSonstAus2());
				oos.writeInt(dp.getFaMiete());
				oos.writeInt(dp.getFaStrom());
				oos.writeInt(dp.getFaTelefon());
				oos.writeInt(dp.getFaKonto());
				oos.writeInt(dp.getFaZeitung());
				oos.writeInt(dp.getFaVersicherung());
				oos.writeObject(dp.getKontostandNeu());
				oos.writeObject(dp.getGehalt());
				oos.writeObject(dp.getGutschriften());
				oos.writeObject(dp.getSonstEin1());
				oos.writeObject(dp.getSonstEin2());
				oos.writeInt(dp.getFaGehalt());
				oos.writeInt(dp.getFaGutschriften());
				oos.writeObject(dp.getBares());
				oos.writeObject(dp.getTage());
				oos.writeObject(dp.getSatz());
				oos.writeObject(dp.geterlMiete());
				oos.writeObject(dp.geterlStrom());
				oos.writeObject(dp.geterlTelefon());
				oos.writeObject(dp.geterlKonto());
				oos.writeObject(dp.geterlZeitung());
				oos.writeObject(dp.geterlVersicherung());
				oos.writeObject(dp.geterlRechnungen());
				oos.writeObject(dp.geterlTVGeb());
				oos.writeObject(dp.geterlSonstAus1());
				oos.writeObject(dp.geterlSonstAus2());
				oos.writeObject(dp.geterlGehalt());
				oos.writeObject(dp.geterlGutschriften());
				oos.writeObject(dp.geterlSonstEin1());
				oos.writeObject(dp.geterlSonstEin2());
				oos.writeObject(dp.geterlBares());
				oos.writeInt(flipper.getDisplayedChild());
				oos.flush();
				oos.close();
				Amount.setBlankzero(true);

				com.google.android.gms.common.api.Status status =
                        driveContents.commit(mGoogleApiClient, changeSet).await();
                return status.getStatus().isSuccess();
            } catch (IOException e) {
                Log.e(LogTAG, "IOException while writing to the output stream", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Log.e(LogTAG, "Error while editing contents");
                return;
            }
            Log.i(LogTAG, "Successfully edited contents");
        }
    } // end class AsyncTask

    final private ResultCallback<DriveIdResult> idCallback = new ResultCallback<DriveIdResult>() {
        @Override
        public void onResult(DriveIdResult result) {
			driveFile = result.getDriveId().asDriveFile();
            new RetrieveDriveFileContentsAsyncTask().execute(driveFile);
        }
    };

    final private class RetrieveDriveFileContentsAsyncTask
            extends AsyncTask<DriveFile, Boolean, String> {

        @Override
        protected String doInBackground(DriveFile... args) {
            String contents = null;
            DriveFile file = args[0];
            DriveContentsResult driveContentsResult =
                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            try {
                ObjectInputStream ois = new ObjectInputStream(driveContents.getInputStream());

				timestamp = (String) ois.readObject();

				dp.setKontostand((String) ois.readObject());
				dp.setMiete((String) ois.readObject());
				dp.setStrom((String) ois.readObject());
				dp.setTelefon((String) ois.readObject());
				dp.setKonto((String) ois.readObject());
				dp.setZeitung((String) ois.readObject());
				dp.setVersicherung((String) ois.readObject());
				dp.setRechnungen((String) ois.readObject());
				dp.setTVGeb((String) ois.readObject());
				dp.setSonstAus1((String) ois.readObject());
				dp.setSonstAus2((String) ois.readObject());
				dp.setFaMiete(ois.readInt());
				dp.setFaStrom(ois.readInt());
				dp.setFaTelefon(ois.readInt());
				dp.setFaKonto(ois.readInt());
				dp.setFaZeitung(ois.readInt());
				dp.setFaVersicherung(ois.readInt());
				dp.setKontostandNeu((String) ois.readObject());
				dp.setGehalt((String) ois.readObject());
				dp.setGutschriften((String) ois.readObject());
				dp.setSonstEin1((String) ois.readObject());
				dp.setSonstEin2((String) ois.readObject());
				dp.setFaGehalt(ois.readInt());
				dp.setFaGutschriften(ois.readInt());
				dp.setBares((String) ois.readObject());
				dp.setTage((String) ois.readObject());
				dp.setSatz((String) ois.readObject());
				dp.seterlMiete((String) ois.readObject());
				dp.seterlStrom((String) ois.readObject());
				dp.seterlTelefon((String) ois.readObject());
				dp.seterlKonto((String) ois.readObject());
				dp.seterlZeitung((String) ois.readObject());
				dp.seterlVersicherung((String) ois.readObject());
				dp.seterlRechnungen((String) ois.readObject());
				dp.seterlTVGeb((String) ois.readObject());
				dp.seterlSonstAus1((String) ois.readObject());
				dp.seterlSonstAus2((String) ois.readObject());
				dp.seterlGehalt((String) ois.readObject());
				dp.seterlGutschriften((String) ois.readObject());
				dp.seterlSonstEin1((String) ois.readObject());
				dp.seterlSonstEin2((String) ois.readObject());
				dp.seterlBares((String) ois.readObject());
				dp.setDisplayedChild(ois.readInt());

                ois.close();
            } catch (IOException e) {
                Log.e(LogTAG, "IOException while reading the input stream", e);
            ///*
            } catch (ClassNotFoundException e) {
               Log.e(LogTAG, "ClassNotFoundException while reading the stream", e);
            //*/
            }
            driveContents.discard(mGoogleApiClient);
            return contents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            flipper.removeView(findViewById(R.id.Grid0));
			showValues();
			showResults();
			if (dp.getDisplayedChild() > flipper.getDisplayedChild()) {
				onForwardMenu(null);
			}
			if (dp.getDisplayedChild() < flipper.getDisplayedChild()) {
				onBackwardMenu(null);
			}
			dp_alt = new DagosPlan(dp);
        }
    }

	final private class RetrieveTSDriveFileContentsAsyncTask
			extends AsyncTask<DriveFile, Boolean, String> {

		@Override
		protected String doInBackground(DriveFile... args) {
			String timestamp = null;
			DriveFile file = args[0];
			DriveContentsResult driveContentsResult =
					file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
			if (!driveContentsResult.getStatus().isSuccess()) {
			    Log.e(LogTAG, "Error while reading the DriveFile " + driveContentsResult +
				"/" + driveContentsResult.getStatus().getStatusCode() +
				"/" + driveContentsResult.getStatus().getStatusMessage());
				return null;
			}
			DriveContents driveContents = driveContentsResult.getDriveContents();
			try {
				ObjectInputStream ois = new ObjectInputStream(driveContents.getInputStream());

				timestamp = (String) ois.readObject();

				ois.close();
			} catch (IOException e) {
				Log.e(LogTAG, "IOException while reading the input stream", e);
				///*
			} catch (ClassNotFoundException e) {
				Log.e(LogTAG, "ClassNotFoundException while reading the stream", e);
				//*/
			}
			driveContents.discard(mGoogleApiClient);
			return timestamp;
		}

		@Override
		protected void onPostExecute(String timestamp) {
			super.onPostExecute(timestamp);
			//Log.i(LogTAG, "Timestamp (alt) :" + MainActivity.this.timestamp);
			//Log.i(LogTAG, "Timestamp (neu) :" + timestamp);
			if (timestamp.equals(MainActivity.this.timestamp)) {
				new EditContentsAsyncTask().execute(driveFile);
			} else {
				try {
					Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
					r.play();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.i(LogTAG, "DriveFile - überholt");
				Toast.makeText(MainActivity.this, "Timestamp - Error Google DriveFile", Toast.LENGTH_LONG).show();
			}
		}
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
			et.setFocusable(true);
			et.setFocusableInTouchMode(true);
			et.setTextColor(Color.WHITE);
		} else {
			cb.setChecked(true);
			et.setFocusable(false);
			et.setFocusableInTouchMode(false);
			et.setTextColor(Color.GRAY);
		}
		// et.refreshDrawableState();
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
		mETKopfhau.setTextColor(Color.WHITE);
		if (dp.getKopfhau().length() > 1) {
			if (dp.getKopfhau().substring(0, 1).compareTo("-") == 0) {
				mETKopfhau.setTextColor(Color.RED);
			}
		}
		mETKopfhau.setText(dp.getKopfhau());
		
		getActionBar().setTitle("Dago : " + dp.getKopfhau() + " €");

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
		
		// Symptom-Korrektur :
		//   ggf. (nach AnimationSlide ist ScrollX nicht zurückgesetzt ???)
		
		//findViewById(R.id.Grid1).setScrollX(0);
		//findViewById(R.id.Grid2).setScrollX(0);

		onForwardMenu(null);
	}

	// im Layout aktiviert:
	public void onClickBackward(View view) {
		
		//findViewById(R.id.Grid1).setScrollX(0);
		//findViewById(R.id.Grid2).setScrollX(0);
		
		onBackwardMenu(null);
	}

	// im Menu Layout aktiviert:
	public void onForwardMenu(MenuItem item) {
		
		//findViewById(R.id.Grid1).setScrollX(0);
		//findViewById(R.id.Grid2).setScrollX(0);
		
		//flipper.setAnimation(AnimationUtils.loadAnimation(this,
		//		R.anim.slide_left));
		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_left));
		locButton.startAnimation(rotate);
		flipper.showNext();
		updateMenu();

	}

	// im Menu Layout aktiviert:
	public void onBackwardMenu(MenuItem item) {
		
		//findViewById(R.id.Grid1).setScrollX(0);
		//findViewById(R.id.Grid2).setScrollX(0);
		
		//flipper.setAnimation(AnimationUtils.loadAnimation(this,
		//		R.anim.slide_right));
		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_right));
		locButton.startAnimation(rotate);
		flipper.showPrevious();
		updateMenu();
		
	}
	
	public void viewInf(View v) {
		Log.i(LogTAG, "SizeMeas  x:" + v.getMeasuredWidth() + " y:" + v.getMeasuredHeight());
		Log.i(LogTAG, "Size      x:" + v.getWidth() + " y:" + v.getHeight());
		Log.i(LogTAG, "Scale     x:" + v.getScaleX() + " y:" + v.getScaleY());
	}
	
	public void zoom(float scaleFactor) {

        /*
		
		GridLayout grid1 = (GridLayout)findViewById(R.id.Grid1);
		GridLayout grid2 = (GridLayout)findViewById(R.id.Grid2);
		ScaledFrameLayout flP1 = (ScaledFrameLayout)findViewById(R.id.flP1);
		ScaledFrameLayout flP2 = (ScaledFrameLayout)findViewById(R.id.flP2);
		
		grid1.setPivotX(0);
		grid1.setPivotY(0);
		grid2.setPivotX(0);
		grid2.setPivotY(0);
		grid1.setScaleX(scaleFactor);
		grid1.setScaleY(scaleFactor);
		grid2.setScaleX(scaleFactor);
		grid2.setScaleY(scaleFactor);
		flP1.setScaleFactor(scaleFactor);
		flP2.setScaleFactor(scaleFactor);
		flP1.requestLayout();
		flP2.requestLayout();

		*/
		
	}

	// im Menu Layout aktiviert:
	public void onMinus1(MenuItem item) {
    	dp.minus1();
		showValues();
		showResults();
		Toast.makeText(this, "minus 1", Toast.LENGTH_LONG).show();
	}

	// im Menu Layout aktiviert:
	public void onZoominMenu(MenuItem item) {
		if (lastScaleFactor < 1.0f) {
			//zoom(lastScaleFactor += 0.1f);
		}
	}

	// im Menu Layout aktiviert:
	public void onZoomoutMenu(MenuItem item) {
		if (lastScaleFactor > 0.6f) {
			//zoom(lastScaleFactor -= 0.1f);
		}
	}

	public void updateMenu() {
		mnitemfor.setEnabled(true);
		mnitemfor.setVisible(true);
		mnitemback.setEnabled(false);
		mnitemback.setVisible(false);
    	/*
    	switch (flipper.getDisplayedChild()) {
			case 0:
				mnitemfor.setEnabled(true);
				mnitemfor.setVisible(true);
				mnitemback.setEnabled(false);
				mnitemback.setVisible(false);
				break;

			case x:
				mnitemfor.setEnabled(true);
				mnitemfor.setVisible(true);
				mnitemback.setEnabled(true);
				mnitemback.setVisible(true);
				break;

			case 1:
				mnitemfor.setEnabled(false);
				mnitemfor.setVisible(false);
				mnitemback.setEnabled(true);
				mnitemback.setVisible(true);
				break;
		}
		*/
	} // end function updateMenu

	// im Menu Layout aktiviert:
	public void onSaveClicked(MenuItem item) {
		saveToPrefs("PrefSave");
		writeDriveFile();
		Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
	}

	// im Layout aktiviert:
	public void onDiscardClicked(MenuItem item) {
		dp = new DagosPlan(dp_alt);
		loadFromPrefs("PrefSave");
		Toast.makeText(this, "discarded", Toast.LENGTH_LONG).show();
		showValues();
		showResults();
	}

	// im Layout aktiviert:
	public void onSettingsClicked(MenuItem item) {
		// Log.i(LogTAG, "Dago starting Settings ?");
		Intent intent = new Intent(this, DagosSettingsActivity.class);
		startActivity(intent);
	}

	// im Layout aktiviert:
	public void onDollarsClicked(View v) {
		onMinus1(null);
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

	public void saveToPrefs(String filename) {

		if (true) return;

		Amount.setBlankzero(false);

		SharedPreferences settings = getSharedPreferences(filename, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString("Kontostand", dp.getKontostand());
		editor.putString("Miete", dp.getMiete());
		editor.putString("Strom", dp.getStrom());
		editor.putString("Telefon", dp.getTelefon());
		editor.putString("Konto", dp.getKonto());
		editor.putString("Zeitung", dp.getZeitung());
		editor.putString("Versicherung", dp.getVersicherung());
		editor.putString("Rechnungen", dp.getRechnungen());
		editor.putString("TVGeb", dp.getTVGeb());
		editor.putString("SonstAus1", dp.getSonstAus1());
		editor.putString("SonstAus2", dp.getSonstAus2());
		editor.putInt("FaMiete", dp.getFaMiete());
		editor.putInt("FaStrom", dp.getFaStrom());
		editor.putInt("FaTelefon", dp.getFaTelefon());
		editor.putInt("FaKonto", dp.getFaKonto());
		editor.putInt("FaZeitung", dp.getFaZeitung());
		editor.putInt("FaVersicherung", dp.getFaVersicherung());
		editor.putString("KontostandNeu", dp.getKontostandNeu());
		editor.putString("Gehalt", dp.getGehalt());
		editor.putString("Gutschriften", dp.getGutschriften());
		editor.putString("SonstEin1", dp.getSonstEin1());
		editor.putString("SonstEin2", dp.getSonstEin2());
		editor.putInt("FaGehalt", dp.getFaGehalt());
		editor.putInt("FaGutschriften", dp.getFaGutschriften());
		editor.putString("Bares", dp.getBares());
		editor.putString("Tage", dp.getTage());
		editor.putString("Satz", dp.getSatz());
		editor.putString("erlMiete", dp.geterlMiete());
		editor.putString("erlStrom", dp.geterlStrom());
		editor.putString("erlTelefon", dp.geterlTelefon());
		editor.putString("erlKonto", dp.geterlKonto());
		editor.putString("erlZeitung", dp.geterlZeitung());
		editor.putString("erlVersicherung", dp.geterlVersicherung());
		editor.putString("erlRechnungen", dp.geterlRechnungen());
		editor.putString("erlTVGeb", dp.geterlTVGeb());
		editor.putString("erlSonstAus1", dp.geterlSonstAus1());
		editor.putString("erlSonstAus2", dp.geterlSonstAus2());
		editor.putString("erlGehalt", dp.geterlGehalt());
		editor.putString("erlGutschriften", dp.geterlGutschriften());
		editor.putString("erlSonstEin1", dp.geterlSonstEin1());
		editor.putString("erlSonstEin2", dp.geterlSonstEin2());
		editor.putString("erlBares", dp.geterlBares());

		// Commit the edits!
		editor.commit();
		
		Log.i(LogTAG, "Dago Settings saved to " + filename);

		Amount.setBlankzero(true);

	}

	public void loadFromPrefs(String filename) {

		if (true) return;

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(filename, Context.MODE_PRIVATE);

		dp.setKontostand(settings.getString("Kontostand", "0"));
		dp.setMiete(settings.getString("Miete", "0"));
		dp.setStrom(settings.getString("Strom", "0"));
		dp.setTelefon(settings.getString("Telefon", "0"));
		dp.setKonto(settings.getString("Konto", "0"));
		dp.setZeitung(settings.getString("Zeitung", "0"));
		dp.setVersicherung(settings.getString("Versicherung", "0"));
		dp.setRechnungen(settings.getString("Rechnungen", "0"));
		dp.setTVGeb(settings.getString("TVGeb", "0"));
		dp.setSonstAus1(settings.getString("SonstAus1", "0"));
		dp.setSonstAus2(settings.getString("SonstAus2", "0"));
		dp.setFaMiete(settings.getInt("FaMiete", 1));
		dp.setFaStrom(settings.getInt("FaStrom", 1));
		dp.setFaTelefon(settings.getInt("FaTelefon", 1));
		dp.setFaKonto(settings.getInt("FaKonto", 1));
		dp.setFaZeitung(settings.getInt("FaZeitung", 1));
		dp.setFaVersicherung(settings.getInt("FaVersicherung", 1));
		dp.setKontostandNeu(settings.getString("KontostandNeu", "0"));
		dp.setGehalt(settings.getString("Gehalt", "0"));
		dp.setGutschriften(settings.getString("Gutschriften", "0"));
		dp.setSonstEin1(settings.getString("SonstEin1", "0"));
		dp.setSonstEin2(settings.getString("SonstEin2", "0"));
		dp.setFaGehalt(settings.getInt("FaGehalt", 1));
		dp.setFaGutschriften(settings.getInt("FaGutschriften", 1));
		dp.setBares(settings.getString("Bares", "0"));
		dp.setTage(settings.getString("Tage", "0"));
		dp.setSatz(settings.getString("Satz", "0"));
		dp.seterlMiete(settings.getString("erlMiete", ""));
		dp.seterlStrom(settings.getString("erlStrom", ""));
		dp.seterlTelefon(settings.getString("erlTelefon", ""));
		dp.seterlKonto(settings.getString("erlKonto", ""));
		dp.seterlZeitung(settings.getString("erlZeitung", ""));
		dp.seterlVersicherung(settings.getString("erlVersicherung", ""));
		dp.seterlRechnungen(settings.getString("erlRechnungen", ""));
		dp.seterlTVGeb(settings.getString("erlTVGeb", ""));
		dp.seterlSonstAus1(settings.getString("erlSonstAus1", ""));
		dp.seterlSonstAus2(settings.getString("erlSonstAus2", ""));
		dp.seterlGehalt(settings.getString("erlGehalt", ""));
		dp.seterlGutschriften(settings.getString("erlGutschriften", ""));
		dp.seterlSonstEin1(settings.getString("erlSonstEin1", ""));
		dp.seterlSonstEin2(settings.getString("erlSonstEin2", ""));
		dp.seterlBares(settings.getString("erlBares", ""));

		Log.i(LogTAG, "Dago Settings loaded from " + filename);
	}
	

	private class MyOnTouchListener implements OnTouchListener {

		private float downXValue;
		private float moveXValue;
		private boolean isValidDirection;
		private ViewFlipper vf;
		private View grid1;
		private View grid2;
		private View grid;
		private int faktor;

		public boolean onTouch(View view, MotionEvent event) {

            /*
			if (findViewById(R.id.pageFlipper).getAnimation() != null)
				if (findViewById(R.id.pageFlipper).getAnimation().hasStarted() &
					!findViewById(R.id.pageFlipper).getAnimation().hasEnded()) {
					// Animation running: ignore Touch
					return false;
			}
			// Log.i(LogTAG, "Dago onTouch");
			
			// Get the action that was done on this touch event
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {

				vf = (ViewFlipper) findViewById(R.id.pageFlipper);
				grid1 = findViewById(R.id.Grid1);
				grid2 = findViewById(R.id.Grid2);
				if (vf.getDisplayedChild() == 0) {
					faktor = -1;
					grid = grid2;
				} else {
					faktor = 1;
					grid = grid1;
				}

				// show we catched it
				vf.setAlpha(0.8f);
				vf.clearAnimation();
				isValidDirection = true;

				grid1.scrollTo(0,  0);
				grid2.scrollTo(0,  0);
				grid.scrollTo((grid1.getWidth() * faktor), 0);
				grid.setVisibility(View.VISIBLE);

				// store the X value when the user's finger was pressed down
				downXValue = event.getX();
				moveXValue = event.getX();
				break;
			}

			case MotionEvent.ACTION_MOVE: {

				if ((vf.getDisplayedChild() == 0) & (downXValue < (event.getX()-20))) {
					// invalid direction : show shake
					if (isValidDirection) {
						grid1.startAnimation(new Animation3DRotate(grid1.getWidth()/2,grid1.getHeight()/2,
			                    600,  8, true ));
						//grid1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
						//		R.anim.shake));
					}
					isValidDirection = false;
					break;
				}
				if ((vf.getDisplayedChild() == 1) & (downXValue > (event.getX()+20))) {
					// invalid direction : show shake
					if (isValidDirection) {
						grid2.startAnimation(new Animation3DRotate(grid2.getWidth()/2,grid2.getHeight()/2,
			                    600, -8, true ));
						//grid2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
						//		R.anim.shake));
					}
					isValidDirection = false;
					break;
				}

				isValidDirection = true;

				if ((vf.getDisplayedChild() == 0 & grid1.getScrollX() < 800)
						| (vf.getDisplayedChild() == 1 & grid2.getScrollX() > -800)) {
					// scrollen um 2-fache Fingerbewegung
					grid1.scrollBy((int) (((moveXValue - event.getX()) * 2)), 0);
					grid2.scrollBy((int) (((moveXValue - event.getX()) * 2)), 0);
					if (grid1.getScrollX() > 800) {
						// zurück auf Rand
						grid1.scrollTo(800, 0);
						grid2.scrollTo(0, 0);
					}
					if (grid2.getScrollX() < -800) {
						grid1.scrollTo(0, 0);
						grid2.scrollTo(-800, 0);
					}
				}
				moveXValue = event.getX();
				break;
			}

			case MotionEvent.ACTION_UP: {
				// Get the X value when the user released his/her finger
				float currentX = event.getX();

				if (isValidDirection) {
					// going forwards: pushing stuff to the left
					if (downXValue > (currentX + 50)) {
						// Set the animation
						vf.startAnimation(new ViewAnimationSlide(grid2.getWidth()/2,grid2.getHeight()/2,
			                 500, grid2, grid1));
						break;
					}
					// going backwards: pushing stuff to the right
					if (downXValue < (currentX - 50)) {
						// Set the animation
						vf.startAnimation(new ViewAnimationSlide(grid1.getWidth()/2,grid1.getHeight()/2,
				                  500, grid1, grid2));
						break;
					}
				}
				vf.setAlpha(1.0f);
				grid1.scrollTo(0, 0);
				grid2.scrollTo(0, 0);
				grid.setVisibility(View.GONE);
				break;

			}  // end case
			}  // end switch
			
			// if you return false, these actions will not be recorded

    		*/

			return true;

		}  // end onTouch

	} // end class MyOnTouchListener
	
	private class ViewAnimationSlide extends Animation
	                                implements AnimationListener {
		float centerX, centerY;
		View view1, view2;
		int duration, fromX, li_re;
		//int TfromX;
		Camera camera = new Camera();
		
		public ViewAnimationSlide(float centerX, float centerY, int duration, View view1, View view2) {
			this.centerX = centerX;
			this.centerY = centerY;
			this.duration = duration;
			this.fromX = view1.getScrollX() * -1;
			//this.TfromX = view1.getScrollX();
			this.view1 = view1;
			this.view2 = view2;
			
			// links oder rechts anfügen :
			li_re = view2.getScrollX() - view1.getScrollX();
			
			setAnimationListener((AnimationListener)this); 
		}
		
		@Override
		public void initialize(int width, int height, int parentWidth, int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
			setDuration(duration);
			setFillAfter(false);
			setInterpolator(new LinearInterpolator());
		}  // end initialize
		
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			
		/* 	final Matrix matrix = t.getMatrix();
			camera.save();
			camera.translate((TfromX * interpolatedTime - TfromX), 0.0f, 0.0f);
			camera.getMatrix(matrix);
			matrix.preTranslate(-centerX, -centerY);
			matrix.postTranslate(centerX, centerY);
			camera.restore();
		*/	
			view1.scrollTo((int)(fromX * interpolatedTime - fromX), 0);
			view2.scrollTo((int)(fromX * interpolatedTime - fromX + li_re), 0);
			
		}  // end applyTransformation
		
		@Override
		public void onAnimationStart(Animation a) {
			//view1.setVisibility(View.VISIBLE);
		
		}  // end onAnimationStart

		@Override
		public void onAnimationEnd(Animation animation) {
			view1.scrollTo(0, 0);
			view2.scrollTo(0, 0);
			view1.setVisibility(View.GONE);
			findViewById(R.id.pageFlipper).setAlpha(1.0f);
			
			// flip
			flipper.showNext();
			updateMenu();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// nothing to do
		}
		
	}  // end class ViewAnimationSlide
	
	private class simpleOnScaleGestureListener extends SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			//

            /*
			lastScaleFactor *= detector.getScaleFactor();
			lastScaleFactor = Math.max(Math.min(lastScaleFactor, 2.0f),0.6f);
			//Log.i(LogTAG, "Scale Gesture :" + (String.valueOf(detector.getScaleFactor())));
			//Log.i(LogTAG, "Scale act :" + lastScaleFactor);
			findViewById(R.id.Grid1).setScaleX(lastScaleFactor);
			findViewById(R.id.Grid1).setScaleY(lastScaleFactor);
			findViewById(R.id.Grid2).setScaleX(lastScaleFactor);
			findViewById(R.id.Grid2).setScaleY(lastScaleFactor);
			((ScaledFrameLayout)findViewById(R.id.flP1)).setScaleFactor(lastScaleFactor);
			((ScaledFrameLayout)findViewById(R.id.flP2)).setScaleFactor(lastScaleFactor);
			findViewById(R.id.flP1).requestLayout();
			findViewById(R.id.flP2).requestLayout();

            */

			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			// 
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			//sizeInf(findViewById(R.id.Grid1));
			//sizeInf(findViewById(R.id.Grid2));
			// 
		}

	}  // end class simpleOnScaleGestureListener

	private final class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				//Log.i(LogTAG, " velocity " + diffX + " " + diffY + " " + Math.abs(velocityX));
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							onSwipeRight();
						} else {
							onSwipeLeft();
						}
						result = true;
					}
				}
				else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffY > 0) {
						onSwipeBottom();
					} else {
						onSwipeTop();
					}
					result = true;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return result;
		}
	}

	public void onSwipeRight() {
		if (((ViewFlipper) findViewById(R.id.pageFlipper)).getDisplayedChild()!=0) {
			onBackwardMenu(null);
		}
	}

	public void onSwipeLeft() {
		if (((ViewFlipper) findViewById(R.id.pageFlipper)).getDisplayedChild()!=1) {
			onForwardMenu(null);
		}
	}

	public void onSwipeTop() {	}

	public void onSwipeBottom() {	}

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
		
	}  // end class setListener


	
}  // end class MainActivity
