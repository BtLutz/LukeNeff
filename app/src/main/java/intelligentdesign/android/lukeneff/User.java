package intelligentdesign.android.lukeneff;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ericssonschroeter on 3/25/18.
 */

@IgnoreExtraProperties
public class User {
    public String email;

    public User() {
        //For datasnap shot
    }

    public User(String email) {
        this.email = email;
    }
}

