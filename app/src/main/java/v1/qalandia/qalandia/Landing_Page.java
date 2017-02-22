package v1.qalandia.qalandia;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import pareseService.ParseService;

public class Landing_Page extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshPage;
    public LinearLayout tableData;
    public  ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.langing__page);

        refreshPage = (SwipeRefreshLayout) findViewById(R.id.swipe_view);

        refreshPage.setOnRefreshListener(this);

        refresh(false);
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        refresh(true);
    }

    public void refresh (final boolean refreshIt) {
        spinner = (ProgressBar)findViewById(R.id.loadingPar);

        if (!refreshIt) {
            spinner.setVisibility(View.VISIBLE);
        }

        tableData = (LinearLayout) findViewById(R.id.tableData);

        tableData.removeAllViews();

        if (Regions_Situation.statusObjects.length == 0 ) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> status, ParseException e) {
                    if (e == null) {
                        Regions_Situation.statusObjects = new ParseObject[status.size()];
                        for (int i = 0; i < Regions_Situation.statusObjects.length; i++) {
                            Regions_Situation.statusObjects[i] = status.get(i);
                        }

                        loadData(refreshIt);
                    } else {
                        Log.d("score", "Error: " + e.getMessage());

                        refreshPage.setRefreshing(false);
                        spinner.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            loadData(refreshIt);
        }

        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
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
                    ParseService.mainUser = object;
                }
            }
        });
    }

    public void loadData (final boolean refreshIt) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CheckPoint");

        query.include("LastIn");
        query.include("LastOut");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> CheckPoints, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < CheckPoints.size(); i++) {
                        tableData.addView(buildDynamicTableRow(CheckPoints.get(i), i, CheckPoints.size()));
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
                refreshPage.setRefreshing(false);
                spinner.setVisibility(View.GONE);
            }
        });
    }

    public View buildDynamicTableRow (final ParseObject data, int i, int size) {
        final View mainRowLayout = getLayoutInflater().inflate(R.layout.check_points_template, null);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        param.setMargins(0, 0, 5, 5);
        mainRowLayout.setLayoutParams(param);


        if (i < size - 1) {
            mainRowLayout.setBackground(getResources().getDrawable(R.drawable.border));
        }

        TextView checkPointTitle = (TextView) mainRowLayout.findViewById(R.id.checkPointTitle);

        final String name = data.getString("name");
        checkPointTitle.setText(name);

        // code in for cars
        ParseObject statusIn = data.getParseObject("LastIn");

        ImageView carIn = (ImageView) mainRowLayout.findViewById(R.id.inCar);

        TextView textIn = (TextView) mainRowLayout.findViewById(R.id.inText);
        textIn.setText(statusIn.getString("name"));

        String codeIn = statusIn.getString("code");

        if (codeIn.equals("FULL")) {
            carIn.setImageResource(R.mipmap.red);
        } else if (codeIn.equals("MID")) {
            carIn.setImageResource(R.mipmap.yellow);
        } else if (codeIn.equals("GOOD")) {
            carIn.setImageResource(R.mipmap.green);
        } else if (codeIn.equals("CLOSE")) {
            carIn.setImageResource(R.mipmap.red);
        }

        // code Out for cars
        ParseObject statusOut = data.getParseObject("LastOut");

        ImageView carOut = (ImageView) mainRowLayout.findViewById(R.id.outCar);

        TextView textOut = (TextView) mainRowLayout.findViewById(R.id.outText);
        textOut.setText(statusOut.getString("name"));

        String codeOut = statusOut.getString("code");

        if (codeOut.equals("FULL")) {
            carOut.setImageResource(R.mipmap.red);
        } else if (codeOut.equals("MID")) {
            carOut.setImageResource(R.mipmap.yellow);
        } else if (codeOut.equals("GOOD")) {
            carOut.setImageResource(R.mipmap.green);
        } else if (codeOut.equals("CLOSE")) {
            carOut.setImageResource(R.mipmap.red);
        }

        final String objId = data.getObjectId();
        mainRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent CheckPointPage = new Intent(getApplicationContext(), Regions_Situation.class);

                CheckPointPage.putExtra("Title", name);
                CheckPointPage.putExtra("objectId", objId);

                startActivity(CheckPointPage);
            }
        });

        return mainRowLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing__page, menu);
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
