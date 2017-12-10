package com.quantylix.badis.nsiri.omvr;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout front,backgrd;

    Button h1,h2,h3,h4,h5,h6,h7,h8,h9,h10;
    TextView time1,time2,time3,time4,time5,time6,time7,time8,time9,time10;

    Button[] timeLineTab = new Button[10];
    TextView[] clockTab = new TextView[10];

    TextView roomState;
    TextView txtToDayDate;
    public static final String FILE_NAME = "myCalendar.ics";
    /*
    String email;
    String password;
    String url;
    String urlPost;
    */

    String email="meetingroom@zeteam.tn";
    String password="123456789";
    String url = "https://zeteam.tn:7071/home/"+email+"/calendar?fmt=html&view=day";
    String urlPost = "https://zeteam.tn:7071/home/"+email+"/calendar?fmt=ics";
    Calendar cal;
    Calendar calendar;

    SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM HH:mm");
    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:00");
    SimpleDateFormat sdf3 = new SimpleDateFormat("HH:30");
    SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdf5 = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm ");

    private final int EVEREY_X_SECONDS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backgrd = (ConstraintLayout) findViewById(R.id.back);
        setToFullScreen(backgrd);
        front = (ConstraintLayout) findViewById(R.id.font);
        h1 = (Button) findViewById(R.id.tab1);
        h2 = (Button) findViewById(R.id.tab2);
        h3 = (Button) findViewById(R.id.tab3);
        h4 = (Button) findViewById(R.id.tab4);
        h5 = (Button) findViewById(R.id.tab5);
        h6 = (Button) findViewById(R.id.tab6);
        h7 = (Button) findViewById(R.id.tab7);
        h8 = (Button) findViewById(R.id.tab8);
        h9 = (Button) findViewById(R.id.tab9);
        h10 = (Button) findViewById(R.id.tab10);
        time1 = (TextView) findViewById(R.id.time1);
        time2 = (TextView) findViewById(R.id.time2);
        time3 = (TextView) findViewById(R.id.time3);
        time4 = (TextView) findViewById(R.id.time4);
        time5 = (TextView) findViewById(R.id.time5);
        time6 = (TextView) findViewById(R.id.time6);
        time7 = (TextView) findViewById(R.id.time7);
        time8 = (TextView) findViewById(R.id.time8);
        time9 = (TextView) findViewById(R.id.time9);
        time10 = (TextView) findViewById(R.id.time10);
        txtToDayDate = (TextView) findViewById(R.id.txtToDayDate);
        txtToDayDate.setTextSize(25);

        timeLineTab[0]=h1;
        timeLineTab[1]=h2;
        timeLineTab[2]=h3;
        timeLineTab[3]=h4;
        timeLineTab[4]=h5;
        timeLineTab[5]=h6;
        timeLineTab[6]=h7;
        timeLineTab[7]=h8;
        timeLineTab[8]=h9;
        timeLineTab[9]=h10;

        clockTab[0]=time1;
        clockTab[1]=time2;
        clockTab[2]=time3;
        clockTab[3]=time4;
        clockTab[4]=time5;
        clockTab[5]=time6;
        clockTab[6]=time7;
        clockTab[7]=time8;
        clockTab[8]=time9;
        clockTab[9]=time10;

        roomState = (TextView) findViewById(R.id.textView);
        roomState.setTextSize(120);
        initiateTimeLineTabs();
        /*
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        url = intent.getStringExtra("url");
        urlPost = "https://zeteam.tn:7071/home/"+email+"/calendar?fmt=ics";
        */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setToFullScreen(backgrd);
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else new ToDayReservation().execute();// Retrieve Data from Zimbra every 1 sec

                handler.postDelayed(this, EVEREY_X_SECONDS);
            }
        },EVEREY_X_SECONDS);

    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
        } else
        return false;
    }
    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onResume();
            }
        });

        return builder;
    }


    public void setTimeLine(){
        calendar = Calendar.getInstance();

        if(calendar.get(Calendar.MINUTE)<30){

            for (int i=0;i<clockTab.length;i+=2){
                clockTab[i].setText(sdf2.format(calendar.getTime()));
                clockTab[i+1].setText(sdf3.format(calendar.getTime()));
                calendar.add(Calendar.HOUR,1);
            }
        }

        if (calendar.get(Calendar.MINUTE)>=30){

            for (int i=0;i<clockTab.length;i+=2){
                clockTab[i].setText(sdf3.format(calendar.getTime()));
                calendar.add(Calendar.HOUR,1);
                clockTab[i+1].setText(sdf2.format(calendar.getTime()));

            }
        }

        switch (time1.getText().toString()){

            case "13:30" :
                for (int i =9;i<timeLineTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }
                break;

            case  "14:00" :
                for (int  i=8;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }
                break;
            case "14:30" :
                for (int  i=7;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }
                break;
            case "15:00" :
                for (int  i=6;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }
                break;
            case "15:30" :
                for (int  i=5;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }

                break;

            case "16:00" :
                for (int  i=4;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }
                break;

            case "16:30" :
                for (int  i=3;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }

                break;
            case "17:00" :
                for (int  i=2;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }

                break;

            case "17:30" :

                for (int  i=1;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }

                break;


            case "18:00" :
                for (int  i=0;i<clockTab.length;i++){
                    clockTab[i].setVisibility(View.INVISIBLE);
                    timeLineTab[i].setVisibility(View.INVISIBLE);
                }
                free();
                break;

            default:
                free();
                break;

        }

    }


    public void free(){
        roomState.setText("FREE");
        roomState.setTextColor(Color.GREEN);
        backgrd.setBackgroundColor(Color.GREEN);
    }

    public void busy(){
        roomState.setText("Busy");
        roomState.setTextColor(Color.RED);
        backgrd.setBackgroundColor(Color.RED);
    }

    private void setToFullScreen(ViewGroup v){
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


    }


    public int getColor(Button v){
        int color = Color.TRANSPARENT;
        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        return color;
    }


    public void setMeeting(Button h,TextView time){
        if (getColor(h)== Color.RED)
            h.setClickable(false);
        if (getColor(h)==Color.GREEN){
            final String startTime = time.getText().toString();
            System.out.println("el start time = " + startTime);

            h.setClickable(true);
            h.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("el start time which is clicked= " + startTime);
                    TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
                    TimeZone timeZone = registry.getTimeZone("Africa/Algiers");
                    VTimeZone tz = timeZone.getVTimeZone();
                    Calendar startDate = Calendar.getInstance();
                    Calendar parseCalendar = Calendar.getInstance();

                   try {
                        String out1 = sdf5.format(startDate.getTime());
                        System.out.println("el out1 = " + out1);

                        parseCalendar.setTime(sdf4.parse(startTime));
                        System.out.println("el parse Calendar = " +sdf4.format(parseCalendar.getTime()));
                        startDate.set(Calendar.HOUR,parseCalendar.get(Calendar.HOUR_OF_DAY));
                        startDate.set(Calendar.MINUTE,parseCalendar.get(Calendar.MINUTE));

                        String out = sdf1.format(startDate.getTime());
                        System.out.println("el out = " + out);
                        startDate.setTimeZone(timeZone);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTimeZone(timeZone);
                    endDate.setTime(parseCalendar.getTime());
                    endDate.add(Calendar.MINUTE, 30);
                    String out = sdf.format(endDate.getTime());
                    System.out.println("endDate = " + out);

                    //Create the event
                    String event ="Meeting from OMVR APP";
                    DateTime start = new DateTime(parseCalendar.getTime());
                    DateTime end = new DateTime(endDate.getTime());
                    VEvent meeting = new VEvent(start,end,event);

                    // Add the time zone info
                    meeting.getProperties().add(tz.getTimeZoneId());
                    Location location = new Location(email);
                    meeting.getProperties().add(location);

                    Attendee attendee1 = new Attendee(URI.create("mailto:"+email));
                    attendee1.getParameters().add(Role.REQ_PARTICIPANT);
                    attendee1.getParameters().add(new Cn("meetingroom"));
                    attendee1.getParameters().add(PartStat.ACCEPTED);
                    meeting.getProperties().add(attendee1);

                    Attendee attendee2 = new Attendee(URI.create("mailto:"+email));
                    attendee2.getParameters().add(new CuType("RESSOURCE"));
                    attendee2.getParameters().add(Role.NON_PARTICIPANT);
                    attendee2.getParameters().add(PartStat.NEEDS_ACTION);
                    attendee2.getParameters().add(Rsvp.TRUE);
                    meeting.getProperties().add(attendee2);

                    Organizer organizer = new Organizer(URI.create("mailto:"+email));
                    organizer.getParameters().add(new Cn("meetingroom"));
                    meeting.getProperties().add(organizer);

                    net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
                    icsCalendar.getProperties().add(new ProdId("Zimbra-Calendar-Provider"));
                    icsCalendar.getProperties().add(Version.VERSION_2_0);
                    icsCalendar.getProperties().add(Method.PUBLISH);

                    // Add the event and print
                    icsCalendar.getComponents().add(meeting);
                    System.out.println(icsCalendar);

                    String state;
                    state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {

                        File Root = Environment.getExternalStorageDirectory();
                        File Dir = new File(Root.getAbsolutePath() + "/MyAppFile");
                        if (!Dir.exists()) {
                            Dir.mkdir();
                        }
                        File file = new File(Dir, FILE_NAME);
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            CalendarOutputter outputter = new CalendarOutputter();
                            outputter.setValidating(false);
                            outputter.output(icsCalendar, outputStream);
                            outputStream.close();
                            System.out.println("c bon jawek behi");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            System.out.println("fama moshkla fel outputStream");
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("fama moshkla fel outputter");
                        }
                    }


                }
            });
        }
    }

    public void postMeetingIntoZimbra(){
        Ion.with(getApplicationContext())
                .load("POST",urlPost)
                .basicAuthentication(email,password)
                .setFileBody(new File(("/sdcard/MyAppFile/myCalendar.ics")))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        if (e!=null) System.out.println("Jawek behi fel POST déja t3adet");
                        else System.out.println(" el POST mat3adetech w haw 3lech");


                        File fdelete = new File("/sdcard/MyAppFile/myCalendar.ics");
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                System.out.println("file Deleted :");
                            } else {
                                System.out.println("file not Deleted :");
                            }
                        }

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen(backgrd);
    }

    public  void  initiateTimeLineTabs(){
        for (int i=0; i<timeLineTab.length;i++){
            timeLineTab[i].setBackgroundColor(Color.GREEN);
        }
    }

    public class ToDayReservation extends AsyncTask<String[],Void,ArrayList<String[]>>{

        ArrayList<String[]> list ;
        String[] meetings ;
        String startTime;
        Calendar c = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        ArrayList<Integer> redBoxes;
        ArrayList<Integer> greenBox = new ArrayList<>();

        public ToDayReservation(){

            list = new ArrayList<>();
            redBoxes = new ArrayList<>();
            for (int i=0;i<10;i++){
                greenBox.add(i);
            }
            setToFullScreen(backgrd);
        }

        @Override
        protected ArrayList<String[]> doInBackground(String[]... params) {
            setToFullScreen(backgrd);
            list = getToDayMeetings();
            for (int i=0;i<timeLineTab.length;i++){
                setMeeting(timeLineTab[i],clockTab[i]);
            }
            postMeetingIntoZimbra();
            return list;
        }

        private void InstantStateOfRoom() {
            if (getColor(timeLineTab[0])==Color.RED)
                busy();
            else free();
        }

        private void memorizeGreenTabsAmongRedTabs() {
            for (Integer r : redBoxes){
                System.out.println("redBox = "+r);
                for (int i=0;i<greenBox.size();i++){
                    if (greenBox.get(i)==r){
                        greenBox.remove(greenBox.get(i));
                    }
                }
            }

            for (Integer g : greenBox){
                System.out.println("greenBox = "+g);
            }
            for (Integer g : greenBox){
                for (int i=0;i<clockTab.length;i++){
                    if (i==g) timeLineTab[i].setBackgroundColor(Color.GREEN);
                }
            }
        }



        private void displayMeetingsOnTimeLine(ArrayList<String[]> list) {
            for (int i = 0; i< list.size(); i++){
                try {
                    String timeClockMeetingStart = list.get(i)[1];
                    System.out.println("timeClockMeetingStart"+timeClockMeetingStart);

                    Integer piecesOfHours = Integer.parseInt(list.get(i)[2])/30;
                    DateFormat sdfTry = new SimpleDateFormat("HH:mm ");
                    c.setTime(sdf4.parse(timeClockMeetingStart));
                    c.add(Calendar.MINUTE,piecesOfHours*30);
                    String timeClockMeetingEnd= sdfTry.format(c.getTime());
                    System.out.println("timeClockMeetingEnd"+timeClockMeetingEnd);


                    for (int j=0;j<clockTab.length;j++) {
                        boolean MATCHING = timeClockMeetingStart.equals(clockTab[j].getText().toString());

                        if (MATCHING) {
                            System.out.println("Matching MeetingStart = "+timeClockMeetingStart);
                            if (j + piecesOfHours > clockTab.length) {
                                System.out.println("j + piecesOfHours > clockTab.length and j= "+j+"" +
                                        " , piecesOfHours = "+piecesOfHours+" clockTab.length = "+clockTab.length);
                                for (int l = j; l < clockTab.length; l++) {
                                    timeLineTab[l].setBackgroundColor(Color.RED);
                                    redBoxes.add(l);

                                }
                            }
                            else {
                                System.out.println("j + piecesOfHours =< clockTab.length and j= "+j+"" +
                                        " , piecesOfHours = "+piecesOfHours+" clockTab.length = "+clockTab.length);
                                for (int l = j; l < j + piecesOfHours; l++) {
                                    timeLineTab[l].setBackgroundColor(Color.RED);
                                    redBoxes.add(l);
                                }
                            }
                        }
                    }

                    for (int j=0;j<clockTab.length;j++) {
                        boolean endTimeExists = timeClockMeetingEnd.equals(clockTab[j].getText().toString());

                        if (endTimeExists) {
                            System.out.println("Matching MeetingEnd");

                            if (j - piecesOfHours <0) {
                                System.out.println(" j = "+j+" and pieces = "+piecesOfHours);
                                for (int l = j-1; l >= 0; l--) {
                                    timeLineTab[l].setBackgroundColor(Color.RED);
                                    redBoxes.add(l);
                                }
                            }
                            else {
                                for (int l = j-1; l > j - piecesOfHours; l--) {
                                    timeLineTab[l].setBackgroundColor(Color.RED);
                                    redBoxes.add(l);
                                }
                            }
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        private ArrayList<String[]> getToDayMeetings() {

            Ion.with(getApplicationContext())
                    .load(url)
                    .basicAuthentication(email,password)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {

                            ArrayList<String[]> list = new ArrayList<>();
                                txtToDayDate.setText(sdf.format(calendar.getTime()));
                                Document document = Jsoup.parse(result);
                                Elements times = document.select("td[colspan=1]").select("td[class=OrangeLight]").select("td[valign=top]");
                                Elements links = document.getElementsByAttributeValueContaining("href", "instDuration");
                                System.out.println("times"+times);


                                for (int i = 0; i < (times.size()); i++) {

                                    meetings = new String[3];
                                    String duration = links.get(i).attr("href").substring(links.get(i).attr("href").indexOf("instDuration"));
                                    String exactDuration = duration.substring(duration.lastIndexOf("=")).replace("=", "");
                                    Integer durationInteger = Integer.parseInt(exactDuration) / 60000;
                                    meetings[0] = links.get(i).text(); // meeting description
                                    System.out.println("links i"+links.get(i).text().toString());
                                    startTime = times.get(i).text();
                                    meetings[1] = startTime; // meeting starting time
                                    meetings[2] = durationInteger.toString(); // meeting duration
                                    list.add(meetings);

                                }

                                for (String[] s : list) {
                                    for (int j = 0; j < s.length; j++)
                                        System.out.println("list s"+s[j]);
                                }
                                setTimeLine();
                                displayMeetingsOnTimeLine(list);
                                memorizeGreenTabsAmongRedTabs();
                                InstantStateOfRoom();

                            }

                    });
            return  list;
        }
    }
}
