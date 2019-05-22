package de.floresse.dagobert;

import android.util.Log;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import de.floresse.drivefile.DriveServiceHelper;

public class MyDriveServiceHelper extends DriveServiceHelper {
    private final String TAG = "MyDriveServiceHelper";

    public MyDriveServiceHelper(Drive drive) {
        super(drive);
    }

    @Override
    public Vector<Object> readFile(InputStream is) {
        String timestamp = null;
        DagosPlan dp = new DagosPlan();
        Vector<Object> readResult = new Vector<>();

        try (ObjectInputStream ois = new ObjectInputStream(is);)
        {
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

        } catch (Exception e) {
            Log.i(TAG, "Exception bei Drive-File read : " + e.toString());
            dp=null;
        }

        readResult.add(0, timestamp);
        readResult.add(1, dp);

        return readResult;
    }

    @Override
    public void saveFile(ByteArrayOutputStream baos, Vector<Object> inp) {

        Amount.setBlankzero(false);
        String timestamp = (String) inp.get(0);
        DagosPlan dp = (DagosPlan) inp.get(1);

        try (ObjectOutputStream oos = new ObjectOutputStream(baos);)
        {
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
        } catch (IOException e) {
            Log.i(TAG, "IOException Drive-File : " + e.toString());
        }

        Amount.setBlankzero(true);

    }


}
