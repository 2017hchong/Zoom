package com.hayuneldon.zoom;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Hayun Chong and Eldon Luk
 * Period 1
 */
public class ZoomActivity extends Activity  implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener,TextToSpeech.OnInitListener
{
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    String MODE = "READER";

    int pos = 0;

    final static float STEP = 200;

    TextView mytv;
    TextView ordinaryTV;
    int forecolor = Color.BLACK;
    int backcolor = android.R.color.background_light;

    TextView nametv;
    ImageView useriv;
    View readerscreen;
    View titlescreen;
    EditText textSize;

    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;
    int size = 50;

    private float flingMin = 100;
    private float velocityMin = 100;

    Context con;
    ImageButton imButton;

    private ActionBar actionBar;

    private final long startTime = 30000;
    private final long interval = 1000;
    private ParisCountDownTimer countDownTimer;
    private ProgressBar countdown_progress;


    private TextToSpeech tts;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.zoom);

        actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.bar, null);
        TextView titleTV = (TextView) customView.findViewById(R.id.title);
        titleTV.setSelected(true);
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);
        titleTV.setText("Zoom Reader");

        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);
        con = this;

        mytv = (TextView) findViewById(R.id.mytv);
        ordinaryTV = (TextView) findViewById(R.id.mytv1);
        nametv = (TextView) findViewById(R.id.name);
        useriv = (ImageView) findViewById(R.id.imageView1);
        readerscreen = (View) findViewById(R.id.bottom_frame);
        titlescreen = (View) findViewById(R.id.top_frame);
        countdown_progress = (ProgressBar) findViewById(R.id.progressBarToday);

        countdown_progress.setVisibility(View.INVISIBLE);

        mytv.setTextSize(mRatio + 12);
        mytv.setVisibility(View.VISIBLE);

        ordinaryTV.setTextSize(mRatio + 12);
        ordinaryTV.setVisibility(View.INVISIBLE);
        setColoredText("Paste any block of text to begin", forecolor, backcolor);


        tts = new TextToSpeech(this, this);

        imButton = (ImageButton) findViewById(R.id.imButton1);
        imButton.setBackgroundResource(R.drawable.reader_icon);
        imButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tts != null) {

                    String text =
                            mytv.getText().toString();
                    if (text != null) {
                        if (!tts.isSpeaking()) {
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }

                Toast.makeText(con, "Reading Aloud", Toast.LENGTH_LONG).show();



            }
        });

        imButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;

            }

        });

        textSize= (EditText)findViewById(R.id.textSize);

        findViewById(R.id.setSize).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        size = Integer.parseInt(textSize.getText().toString());

                        mytv.setTextSize(mRatio + size);
                        ordinaryTV.setTextSize(mRatio + size);
                    }
        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent1 = new Intent(ZoomActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int code) {
        if (code == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
        } else {
            tts = null;
            Toast.makeText(this, "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(MODE.equalsIgnoreCase("VISIBILITY")) //check if navigation is on.
        {
            if (event.getPointerCount() == 2) {
                int action = event.getAction();
                int pureaction = action & MotionEvent.ACTION_MASK;
                if (pureaction == MotionEvent.ACTION_POINTER_DOWN)
                {
                    mBaseDist = getDistance(event);
                    mBaseRatio = mRatio;
                }
                else
                {
                    float delta = (getDistance(event) - mBaseDist) / STEP;
                    float multi = (float) Math.pow(2, delta);
                    mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                    mytv.setTextSize(mRatio + size);
                    ordinaryTV.setTextSize(mRatio + size);
                }

                return false;
            }
        }
        else if(MODE.equalsIgnoreCase("NAVIGATION"))
        {
            this.mDetector.onTouchEvent(event);
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //No onDown event needed
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {

        //calculate the change in X position within the fling gesture
        float horizontalDiff = event2.getX() - event1.getX();
        //calculate the change in Y position within the fling gesture
        float verticalDiff = event2.getY() - event1.getY();

        float absHDiff = Math.abs(horizontalDiff);
        float absVDiff = Math.abs(verticalDiff);
        float absVelocityX = Math.abs(velocityX);
        float absVelocityY = Math.abs(velocityY);


        if(absHDiff>absVDiff && absHDiff>flingMin && absVelocityX>velocityMin) //Horizontal Flings
        {
            if(horizontalDiff>0)
            {
                //LEFT->RIGHT

            }
            else
            {

            }
        }
        else if(absVDiff>flingMin && absVelocityY>velocityMin) //Vertical Flings
        {
            if(verticalDiff>0)
            {
                //UP->DOWN
            }
            else
            {
                //DOWN->UP
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        //Not required here.
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        //Not required here.

        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Not required here.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        //Not required here.
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        //Not required here.
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        //Not required here.
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        //Not required here.
        return true;
    }

    private void setColoredText(String fulltext, int forecolor1, int backcolor1) {
        mytv.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) mytv.getText();
        str.setSpan(new ForegroundColorSpan(forecolor1), 0, mytv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new BackgroundColorSpan(backcolor1), 0, mytv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ordinaryTV.setText(fulltext, TextView.BufferType.SPANNABLE);
        str = (Spannable) ordinaryTV.getText();
        str.setSpan(new ForegroundColorSpan(forecolor1), 0, ordinaryTV.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new BackgroundColorSpan(backcolor1), 0, ordinaryTV.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColoredText( int forecolor1, int backcolor1) {
        Spannable str = (Spannable) mytv.getText();
        str.setSpan(new ForegroundColorSpan(forecolor1), 0, mytv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new BackgroundColorSpan(backcolor1), 0, mytv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        str = (Spannable) ordinaryTV.getText();
        str.setSpan(new ForegroundColorSpan(forecolor1), 0, ordinaryTV.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new BackgroundColorSpan(backcolor1), 0, ordinaryTV.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    int getDistance(MotionEvent event)
    {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }


    private void showColorPickerDialogDemo(final int inputtype) {

        int initialColor = Color.WHITE;

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                if(inputtype == 0)
                {
                    forecolor = color;
                }
                else
                {
                    backcolor = color;
                    readerscreen.setBackgroundColor(color);
                    titlescreen.setBackgroundColor(color);
                    mytv.setBackgroundColor(color);
                    ordinaryTV.setBackgroundColor(color);
                }

                setColoredText(forecolor, backcolor);
            }

        });

        colorPickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.reader_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.forecolor:

                showColorPickerDialogDemo(0);

                return true;

            case R.id.backcolor:

                showColorPickerDialogDemo(1);

                return true;
        }
        return false;
    }

    public class ParisCountDownTimer extends CountDownTimer
    {
        public ParisCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            MODE = "READER";
            imButton.setBackgroundResource(R.drawable.reader_icon);
            countdown_progress.setVisibility(View.INVISIBLE);

            ordinaryTV.setVisibility(View.INVISIBLE);
            mytv.setVisibility(View.VISIBLE);

        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            countdown_progress.setProgress(countdown_progress.getProgress()-1);
        }
    }

}
