package v1.qalandia.qalandia;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import pareseService.ParseService;

public class Regions_Situation extends TabActivity {

    public static ParseObject [] statusObjects = new ParseObject[0];
    private int selected;
    ProgressBar ref;
    Intent getData;
    AlertDialog main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regions__situation);

        TabHost tabHost = getTabHost();

        getData = getIntent();

        TextView title = (TextView) findViewById(R.id.title_app_regions);

        title.setText(getData.getStringExtra("Title"));


        Intent everySitu = new Intent(this, everySituation.class);
        everySitu.putExtra("objectId", getData.getStringExtra("objectId"));

        Intent inSitu = new Intent(this, inSituation.class);
        inSitu.putExtra("objectId", getData.getStringExtra("objectId"));

        Intent outSitu = new Intent(this, outSituation.class);
        outSitu.putExtra("objectId", getData.getStringExtra("objectId"));


        tabHost.addTab(tabHost.newTabSpec("EverySit").setIndicator("الكل").setContent(everySitu));
        tabHost.addTab(tabHost.newTabSpec("InSit").setIndicator("الداخل").setContent(inSitu));
        tabHost.addTab(tabHost.newTabSpec("OutSit").setIndicator("الخارج").setContent(outSitu));


        LinearLayout back_arrow = (LinearLayout) findViewById(R.id.backButton);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Landing_Page = new Intent(getApplicationContext(), Landing_Page.class);
                startActivity(Landing_Page);
            }
        });

        ImageView plus = (ImageView) findViewById(R.id.addSituation);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder addNewstatus = new AlertDialog.Builder(getCurrentActivity());

                final View dialogStatus = getLayoutInflater().inflate(R.layout.add_status_dialog, null);

                ref = (ProgressBar) dialogStatus.findViewById(R.id.progressBar);

                ref.setVisibility(View.GONE);

                final LinearLayout stuatsTable = (LinearLayout) dialogStatus.findViewById(R.id.stuatsTable);


                for (int i = 0; i < statusObjects.length; i+=2) {
                    ParseObject [] data = new ParseObject[2];

                    data[0] = statusObjects[ i ];
                    data[1] = statusObjects[ i + 1 ];

                    stuatsTable.addView(statusButton(data));
                }



                addNewstatus.setView(dialogStatus)
                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                RadioGroup radioGroup = (RadioGroup) dialogStatus.findViewById(R.id.radioStatus);

                selected = radioGroup.getCheckedRadioButtonId();

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        selected = checkedId;
                        if (checkedId == R.id.radioButton) {
                            RadioButton radioClicked = (RadioButton) dialogStatus.findViewById(checkedId);
                            radioClicked.setTextColor(Color.WHITE);

                            RadioButton radioNotClicked = (RadioButton) dialogStatus.findViewById(R.id.radioButton2);
                            radioNotClicked.setTextColor(Color.parseColor("#499CDC"));
                        } else if (checkedId == R.id.radioButton2) {
                            RadioButton radioClicked = (RadioButton) dialogStatus.findViewById(checkedId);
                            radioClicked.setTextColor(Color.WHITE);

                            RadioButton radioNotClicked = (RadioButton) dialogStatus.findViewById(R.id.radioButton);
                            radioNotClicked.setTextColor(Color.parseColor("#499CDC"));

                        }

                    }
                });

                main = addNewstatus.create();
                main.show();
            }
        });
    }

    public LinearLayout statusButton (final ParseObject[] data) {


        LinearLayout buttonHolder = new LinearLayout(this);
        buttonHolder.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        buttonHolder.setGravity(Gravity.CENTER_HORIZONTAL);

        buttonHolder.setLayoutParams(param2);

        for (int i = 0; i < data.length; i++) {

            final View mainRowLayout = getLayoutInflater().inflate(R.layout.add_status_button_tmpl, null);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            param.setMargins(10, 10, 10, 10);
            param.weight = 1;
            mainRowLayout.setLayoutParams(param);


            final ParseObject dataStatus = data[ i ];

            String statusCode = dataStatus.getString("code");

            ImageView imageStatus = (ImageView) mainRowLayout.findViewById(R.id.status_tmp_image);


            if (statusCode.equals("FULL")) {
                imageStatus.setImageResource(R.mipmap.red);
            } else if (statusCode.equals("MID")) {
                imageStatus.setImageResource(R.mipmap.yellow);
            } else if (statusCode.equals("GOOD")) {
                imageStatus.setImageResource(R.mipmap.green);
            } else if (statusCode.equals("CLOSE")) {
                imageStatus.setImageResource(R.mipmap.red);
            }

            String nameStatus = dataStatus.getString("name");
            final TextView statusTxt = (TextView) mainRowLayout.findViewById(R.id.status_text_tmp);

            statusTxt.setText(nameStatus);

            final int index = i;

            mainRowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ref.setVisibility(View.VISIBLE);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("update");
                    query.orderByDescending("createdAt");
                    query.whereEqualTo("FromUser", ParseUser.getCurrentUser());


                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    Date x = new Date(cal.getTimeInMillis() - (5 * 60 * 1000));

                    query.whereGreaterThanOrEqualTo("createdAt", x);


                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {


                            String isPremiumString = ParseService.mainUser.getString("IsPremium");
                            boolean isPremium = false;

                            if (isPremiumString != null && isPremiumString.equals("Y")) {
                                isPremium = true;
                            }

                            if (isPremium || object == null) {

                                ParseObject updateObject = new ParseObject("update");

                                if (selected == R.id.radioButton) {
                                    updateObject.put("Direction", false);
                                } else if (selected == R.id.radioButton2) {
                                    updateObject.put("Direction", true);
                                }

                                ParseUser user = ParseUser.getCurrentUser();
                                ParseRelation<ParseObject> relationUser = updateObject.getRelation("FromUser");
                                relationUser.add(user);


                                ParseObject statusUpdate = ParseObject.createWithoutData("Status", data[index].getObjectId());
                                updateObject.put("Status", statusUpdate);

                                ParseRelation<ParseObject> r = updateObject.getRelation("CheckPoint");
                                final ParseObject checkPointUpdate = ParseObject.createWithoutData("CheckPoint", getData.getStringExtra("objectId"));


                                if (selected == R.id.radioButton) {
                                    checkPointUpdate.put("LastOut", statusUpdate);
                                } else if (selected == R.id.radioButton2) {
                                    checkPointUpdate.put("LastIn", statusUpdate);
                                }

                                r.add(checkPointUpdate);

                                updateObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            ref.setVisibility(View.GONE);
                                            checkPointUpdate.saveInBackground();
                                            main.cancel();
                                        } else {
                                            Log.d("err", e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                ref.setVisibility(View.GONE);
                                main.cancel();

                                final AlertDialog.Builder notAuth = new AlertDialog.Builder(getCurrentActivity());

                                notAuth.setTitle("عذرا");
                                notAuth.setMessage("لا يمكنك اضافة تحديث الا كل خمس دقائق، للحفاظ على صحة المعلومات.");


                                notAuth
                                        .setNegativeButton("بسيطة!", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                notAuth.show();
                            }
                        }
                    });
                }
            });

            buttonHolder.addView(mainRowLayout);
        }

        return buttonHolder;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regions__situation, menu);
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
