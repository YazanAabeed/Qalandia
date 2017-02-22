package v1.qalandia.qalandia;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class everySituation extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshPage;
    private ListView situations;
    private String objectIdCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.every_situation);

        Intent getData = getIntent();
        objectIdCheck = getData.getStringExtra("objectId");

        situations = (ListView) findViewById(R.id.ever_situation);

        refreshPage = (SwipeRefreshLayout) findViewById(R.id.every_Swipe);

        refreshPage.setOnRefreshListener(this);
        refresh();
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh() {

        situations.setAdapter(null);

        ParseQuery<ParseObject> checkQuery = ParseQuery.getQuery("CheckPoint");
        checkQuery.whereEqualTo("objectId", objectIdCheck);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("update");
        query.include("Status");

        query.whereMatchesQuery("CheckPoint", checkQuery);
        query.setLimit(20);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> CheckPoints, ParseException e) {
                if (e == null) {

                    String[] points = new String[CheckPoints.size()];
                    Integer[] imgeId = new Integer[CheckPoints.size()];
                    String[] inOut = new String[CheckPoints.size()];
                    String[] since = new String[CheckPoints.size()];

                    for (int i = 0; i < CheckPoints.size(); i++) {
                        ParseObject checkPoint = CheckPoints.get(i);
                        ParseObject status = checkPoint.getParseObject("Status");

                        boolean Direction = checkPoint.getBoolean("Direction");

                        if (Direction) {
                            inOut[i] = "داخل";
                        } else {
                            inOut[i] = "خارج";
                        }

                        points[i] = status.getString("name");

                        String code = status.getString("code");

                        if (code.equals("FULL")) {
                            imgeId[i] = R.mipmap.red;
                        } else if (code.equals("MID")) {
                            imgeId[i] = R.mipmap.yellow;
                        } else if (code.equals("GOOD")) {
                            imgeId[i] = R.mipmap.green;
                        } else if (code.equals("CLOSE")) {
                            imgeId[i] = R.mipmap.red;
                        }


                        Date str_date = checkPoint.getUpdatedAt();

                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(str_date.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, 0);

                        since[i] = timeAgo.toString();
                    }

                    ListAdapter adapter = new ListAdapter(everySituation.this, points, imgeId, since, inOut );
                    situations.setAdapter(adapter);

                    refreshPage.setRefreshing(false);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_every_situation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
