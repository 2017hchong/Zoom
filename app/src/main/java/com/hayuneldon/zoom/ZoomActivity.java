package com.hayuneldon.zoom;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 2017hchong on 1/4/2016.
 */
public class ZoomActivity extends Activity  implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener
{
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    String MODE = "NAVIGATION";

    //Hello
  /*Modes: {NAVIGATION, READER, VISIBILITY}
   *
   *NAVIGATION - Only detect gestures like swipes in left, right, up and down directions.
   *Navigation mode allows the user to switch between comments.
   *
   *READER - Detect gestures to only allow scroll on EditText for very long texts.
   *Makes it easy to read long texts by locking all other gesture commands in order to
   *avoid accidental touches and altering of current reading experience.
   *
   * VISIBILITY - Only detect pinch in and pinch out to zoom in or out on text.
   * Visibility mode must be accompanied with a timer to toggle off this mode such as to
   * avoid resizing text on accidentally touching the screen, which happens a lot on large
   * amount of texts.
   * */

    Comments comment; //Comments data-object. You can use any of your own data objects as per requirement.

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

    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;

    private float flingMin = 100;
    private float velocityMin = 100;

    Context con;
    ImageButton imButton;

    private ActionBar actionBar;

    private final long startTime = 30000;
    private final long interval = 1000;
    private ParisCountDownTimer countDownTimer;
    private ProgressBar countdown_progress;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);


        actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.bar, null);
        TextView titleTV = (TextView) customView.findViewById(R.id.title);
        titleTV.setSelected(true);
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);
        titleTV.setText("Gesture Reader");


        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);

        comment = new Comments();
        con = this;

        mytv = (TextView) findViewById(R.id.mytv);
        ordinaryTV = (TextView) findViewById(R.id.mytv1);
        nametv = (TextView) findViewById(R.id.name);
        useriv = (ImageView) findViewById(R.id.imageView1);
        readerscreen = (View) findViewById(R.id.bottom_frame);
        titlescreen = (View) findViewById(R.id.top_frame);
        countdown_progress = (ProgressBar) findViewById(R.id.progressBarToday);

        countdown_progress.setVisibility(View.INVISIBLE);

        mytv.setTextSize(mRatio + 26);
        mytv.setVisibility(View.INVISIBLE);

        ordinaryTV.setTextSize(mRatio + 26);
        ordinaryTV.setVisibility(View.VISIBLE);

        //Dedicated method to switch colors on text which is set as Spannable Text.
        setColoredText(comment.getCommentData(pos),forecolor,backcolor);

        nametv.setText(comment.getCommentBy(pos));

        imButton = (ImageButton) findViewById(R.id.imButton1);
        imButton.setBackgroundResource(R.drawable.navigation_icon);
        imButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(MODE == "NAVIGATION")//Gesture based Navigation is On
                {
                    MODE = "READER";

                    ordinaryTV.setVisibility(View.INVISIBLE);
                    mytv.setVisibility(View.VISIBLE);

                    imButton.setBackgroundResource(R.drawable.reader_icon);
                    Toast.makeText(con, "Reader Mode Activated.", Toast.LENGTH_LONG).show();
                }
                else if(MODE == "VISIBILITY")
                {
                    MODE = "READER";

                    ordinaryTV.setVisibility(View.INVISIBLE);
                    mytv.setVisibility(View.VISIBLE);

                    imButton.setBackgroundResource(R.drawable.reader_icon);
                    countdown_progress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    MODE = "NAVIGATION";

                    ordinaryTV.setVisibility(View.VISIBLE);
                    mytv.setVisibility(View.INVISIBLE);

                    imButton.setBackgroundResource(R.drawable.navigation_icon);
                    Toast.makeText(con, "Navigation Mode Activated.", Toast.LENGTH_LONG).show();
                }
            }
        });

        imButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(MODE == "VISIBILITY")
                {
                    MODE = "READER";

                    ordinaryTV.setVisibility(View.INVISIBLE);
                    mytv.setVisibility(View.VISIBLE);

                    imButton.setBackgroundResource(R.drawable.reader_icon);
                    countDownTimer.cancel();
                    countdown_progress.setVisibility(View.INVISIBLE);

                    //Research on font sizes
                    float tsize = mytv.getTextSize();
                    float tmratio = mRatio;
                    String stats = "Text Size = "+tsize+" and mRatio = "+tmratio;
                    mytv.setText(stats);
                }
                else
                {
                    MODE = "VISIBILITY";
                    imButton.setBackgroundResource(R.drawable.navigation_icon);

                    ordinaryTV.setVisibility(View.VISIBLE);
                    mytv.setVisibility(View.INVISIBLE);

                    //***Setup Countdown Timer***//
                    countdown_progress.setVisibility(View.VISIBLE);
                    countdown_progress.setProgress(countdown_progress.getMax());
                    countDownTimer = new ParisCountDownTimer(startTime, interval);
                    countDownTimer.start();
                    //***Setup Countdown Timer***//
                }

                return true;
            }
        });

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
                    mytv.setTextSize(mRatio + 26);
                    ordinaryTV.setTextSize(mRatio + 26);
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

/*
* onFling happens to be the most important gesture control available.
* It returns X,Y coordinates of the first touch event and then the last touch event
* when the user lifts his/ger finger.
*
* We use this X,Y coordinates for calculating the distance and velocity of fling.
*
* Accidental flings and touches tend to be of smaller coordinate differences and
* generally of lower velocity.
*
* Adjust the constraints as per your requirement and on device touch experience.
* You may need to save a log of possible gestures in your first prototype and then
* judge the flingMin and velocityMin parameters or even velocityMax and flingMax.
* */

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

/*
 * Determining Fling directions and corresponding behavior.
 *
 * I have used a standard English book behavior, where the Comments change
 * as per the pages of a book. Right to left denotes a page turn or Next_Item.
 * Left to right denotes a previous page turn or Previous_Item.
 *
 * Flings should be accompanied by audio cues (like sound of page turning etc)
 * such that the progression or change of data can be affirmed without distracting
 * from a decent reading experience.
 *
 * Flings Up and Down have been left out for the sake of understanding. They too
 * can be used to pull down the topic of discussion or a tool bar can be pulled
 * up from the bottom of the screen as per your requirement.
 *
 * An image tool bar example is available at -
 * http://stackoverflow.com/questions/26695864/how-to-add-a-one-side-border-and-background-into-one-drawable/26696547#26696547
 *
 *
 * */

        if(absHDiff>absVDiff && absHDiff>flingMin && absVelocityX>velocityMin) //Horizontal Flings
        {
            if(horizontalDiff>0)
            {
                //LEFT->RIGHT
                //Behavior = Show Previous Comment

                if(pos>0)
                {
                    pos--;
                    setColoredText(comment.getCommentData(pos),forecolor,backcolor);
                    nametv.setText(comment.getCommentBy(pos));
                }
                else
                {
                    //If first comment is reached then alert the user, if possible even ring a bell
                    //or some audio cue.
                    Toast.makeText(con, "Reached first comment.", Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                //LEFT<-RIGHT
                //Behavior = Show Next Comment;

                if(pos<comment.numberOfComments()-1)
                {
                    pos++;
                    setColoredText(comment.getCommentData(pos),forecolor,backcolor);
                    nametv.setText(comment.getCommentBy(pos));
                }
                else
                {
                    Toast.makeText(con, "No more comments.", Toast.LENGTH_LONG).show();
                }

            }
        }
        else if(absVDiff>flingMin && absVelocityY>velocityMin) //Vertical Flings
        {
            if(verticalDiff>0)
            {
                //UP->DOWN
                //Show Discussion Frame in Reader

                Toast.makeText(con, "No behavior defined.", Toast.LENGTH_LONG).show();
            }
            else
            {
                //DOWN->UP
                //Hide Discussion Frame from Reader if Visible
                //Suggestion: Show Comment at Current State such that the user does not
                //loose his/her place in the list of comments they are reading.

                Toast.makeText(con, "No behavior defined.", Toast.LENGTH_LONG).show();
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

/*
* In the methods below the TextView and the EditText have their UI defined.
* Each has to be set with the same foreground and background color in order to
* exhibit a consistent color scheme as set by the user.
*
* You may want to persist the color settings made by the user using Shared Preferences.
* A tutorial on using Shared Preferences can be found here -
* http://www.coders-hub.com/2013/10/how-to-use-shared-preferences-in-android.html#.VTIk7SGqqko
*
* */

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


    /*
    * A little bit of coordinate geometry.
    * The mathematically inclined would recognize the below formula as the Distance Formula or Pythagorean
    * Theorem. Distance formula is used to calculate the distance between two points in a coordinate space.
    *
    * There are 3-Dimensional, 2-Dimensional and multi-dimensional distance formulas as per the
    * nature of their coordinate space or environment. While, the Device screen is only 2-Dimensional
    * and flat the 2D Distance Equation is used.
    *
    * More about Distance formula here - http://en.wikipedia.org/wiki/Distance
    * */
    int getDistance(MotionEvent event)
    {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }


  /*
   * Third Party components built by Piotr Adamus are used here;
   *
   * The ColorPicker and ColorPickerDialog.
   *
   * More about it here - https://github.com/chiralcode/Android-Color-Picker/
   *
   * */

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



/*My custom Count down timer which sets the behavior of the circular/ring progress bar
*and reader mode switching behavior.
*
*Circular/Ring progress bar is discussed in this tutorial on my personal blog, being         *trivial to the scope of Coder's Hub.
*/

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
