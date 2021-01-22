package inspection.management.workplace.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class UserData implements Serializable {

    public final String FILENAME = "workplace";

    public String master_user_id = null;
    public String fname = null;
    public String lname = null;
    public String uname = null;
    public String email = null;
    public String is_del = null;
    public String gender = null;
    public String password = null;
    public String imageUri = null;
    public transient Context context;

    public UserData(Context context) {
        this.context = context;
    }

    public boolean saveToFile(UserData ud) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(ud);
            os.close();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserData readFromFile() {
        UserData ud = null;
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            ud = (UserData) is.readObject();
            is.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ud;
    }

    public boolean deleteFile() {
        try {
            return context.deleteFile(FILENAME);
        } catch (Exception e) {
            return false;
        }
    }
}