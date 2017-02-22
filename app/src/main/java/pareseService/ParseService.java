package pareseService;

import android.app.Application;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;

import java.util.List;

import v1.qalandia.qalandia.Regions_Situation;


/**
 * Created by yazan on 02/11/15.
 */
public class ParseService extends Application {

    public static ParseObject mainUser;

    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "PXlzZyk9QUnYgRuyPd496VRVbJAevKXODzyehips", "EdPBc0ZmpijBkjQ7tZxw3VFoyyTuVGMT7Tf5dQQD");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> status, ParseException e) {
                if (e == null) {
                    Regions_Situation.statusObjects = new ParseObject[status.size()];
                    for (int i = 0; i < Regions_Situation.statusObjects.length; i++) {
                        Regions_Situation.statusObjects[i] = status.get(i);
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });



        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null) {
                        updateUser(user);
                    }
                }
            });
        } else {
            updateUser(ParseUser.getCurrentUser());
        }

    }

    protected void updateUser(ParseUser user) {
        ParseUser.getQuery().getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    mainUser = object;
                }
            }
        });
    }
}
