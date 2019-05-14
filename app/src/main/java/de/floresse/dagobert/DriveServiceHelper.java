package de.floresse.dagobert;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static de.floresse.dagobert.MainActivity.TAG;

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    public Task<String> createFile(String name) {
        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList("root"))
                    .setMimeType("text/plain")
                    .setName(name);

            File googleFile = mDriveService.files().create(metadata).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }

            return googleFile.getId();
        });
    }

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Vector<Object>> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the metadata as a File object.
            File metadata = mDriveService.files().get(fileId).execute();
            String name = metadata.getName();
            String timestamp;
            DagosPlan dp = new DagosPlan();
            Vector<Object> nameAndTsAndContent = new Vector<>();

            // Stream the file contents to a String.
            try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                ObjectInputStream ois = new ObjectInputStream(is)) {

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

                nameAndTsAndContent.add(0, name);
                nameAndTsAndContent.add(1, timestamp);
                nameAndTsAndContent.add(2, dp);

                return nameAndTsAndContent;
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(String fileId, String name, DagosPlan dp, String timestamp) {
        return Tasks.call(mExecutor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File().setName(name);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

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
            oos.writeInt(dp.getDisplayedChild());
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();
            Amount.setBlankzero(true);

            //byte [] arr = baos.toByteArray();
            // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = new ByteArrayContent("text/plain", baos.toByteArray());
            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute();
            return null;
        });
    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    public Task<FileList> queryFiles() {
        return Tasks.call(mExecutor, () ->
                mDriveService.files().list().setSpaces("drive").execute());
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Vector<Object>> openFileUsingStorageAccessFramework(
            ContentResolver contentResolver, Uri uri) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the document's display name from its metadata.
            Vector<Object> nameAndTSAndContent = new Vector();
            Log.i(TAG, "Uri : " + uri);
            String name;
            String timestamp;
            DagosPlan dp = new DagosPlan();
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    name = cursor.getString(nameIndex);
                } else {
                    throw new IOException("Empty cursor returned for file.");
                }
            }

            // Read the document's contents as a String.
            /*
            String content;
            try (InputStream is = contentResolver.openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                content = stringBuilder.toString();
            }
            */
            try (InputStream is = contentResolver.openInputStream(uri);
                 ObjectInputStream ois = new ObjectInputStream(is)) {

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

                Log.i(TAG, "Kontostand ist : " + dp.getKontostand());
                Log.i(TAG, "Kontostand (neu) ist : " + dp.getKontostandNeu());

            }
            nameAndTSAndContent.add(0, name);
            nameAndTSAndContent.add(1, timestamp);
            nameAndTSAndContent.add(2, dp);

            return nameAndTSAndContent;
        });
    }
}
