package com.example.matthewlei.myapplication;

import com.example.matthewlei.myapplication.R;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.view.View.OnDragListener;
import android.view.View.DragShadowBuilder;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;



public class AndroidSampleActivity extends ActionBarActivity {

    private float GRASS = 4;
    private float FIRE = 1;
    private float SKY = 2;
    private float AIR = 3;

    private Drawable lastColor;
    private ImageView lastElement;
    private static final String IMAGEVIEW_TAG = "Android Logo";
    GridLayout gridLayout;

    private ImageView getFromGrid(LinearLayout grid, int x, int y){
        return (ImageView)((LinearLayout)(grid.getChildAt(x))).getChildAt(y);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] grid = new String[16];


        setContentView(R.layout.activity_android_sample);



        //get the sun View
        final ImageView grass = (ImageView) findViewById(R.id.grass);
        // Load it
        //Animation sunRise = AnimationUtils.loadAnimation(this, R.anim.sun_rise);
        //apply the animation to the View
        //sun.startAnimation(sunRise);

        for (int h = 0; h <= 4; h ++){
            final LinearLayout ln = ((LinearLayout)findViewById(R.id.linearLayout1));
            final LinearLayout vl = new LinearLayout(this);
            vl.setId(2000 + h);
            vl.setOrientation(LinearLayout.VERTICAL);
            Log.d("whoa", "another add");
            for (int i = 0; i <= 4; i ++){
                final ImageView grass2 = new ImageView(getApplicationContext());

                // get size of display
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int x = size.x;
                int y = size.y;

                //Subect to change
                int numSquares = 9;

                // elements to randomize over
                int elements[] = new int[]{
                        (R.drawable.grass),
                        (R.drawable.fire),
                        (R.drawable.sky),
                        (R.drawable.air),

                };

                //Set background
                grass2.setBackgroundResource(elements[(int)(Math.random()*elements.length)]);

                grass2.setMinimumWidth(y/numSquares);
                grass2.setMinimumHeight(y/numSquares);

                grass2.setMaxWidth((int)((y/numSquares) * .7));
                grass2.setMaxHeight((int)((y/numSquares) * .7));

                // Set pivot to certain value so it may be distinguished back end
                grass2.setPivotX(getColPivot(grass2.getBackground()));


                grass2.setTag(IMAGEVIEW_TAG);
                grass2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());

                        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                        ClipData dragData = new ClipData(v.getTag().toString(),
                                mimeTypes, item);
                        // Instantiates the drag shadow builder.
                        View.DragShadowBuilder myShadow = new DragShadowBuilder(grass2);

                        // Starts the drag
                        lastColor = v.getBackground();
                        lastElement = (ImageView)v;
                        v.startDrag(dragData,  // the data to be dragged
                                myShadow,  // the drag shadow builder
                                v,      // no need to use local data
                                0          // flags (not currently used, set to 0)
                        );
                        return true;
                    }
                });

                grass2.setOnDragListener( new OnDragListener(){
                    @Override
                    public boolean onDrag(View v, DragEvent event){
                        switch(event.getAction()){
                            case DragEvent.ACTION_DRAG_ENTERED:
                               // Log.d("msg1", "entered");
                                int x_cord = (int) event.getX();
                                int y_cord = (int) event.getY();
                               // Log.d("X-cord on enter", "" +x_cord);
                                //Log.d("Y-cord on enter", "" +y_cord);
                                ImageView dv = (ImageView) event.getLocalState();
                                Drawable temp = v.getBackground();
                                v.setBackground(lastColor);
                                v.setPivotX(getColPivot(lastColor));
                                lastElement.setBackground(temp);
                                lastElement = (ImageView)v;
                                //lastColor = temp;
                                break;
                            case DragEvent.ACTION_DROP:
                                Log.d("msg2", "dropped");
                                x_cord = (int) v.getX();
                                y_cord = (int) v.getY();
                                dv = (ImageView) event.getLocalState();
                                v.setBackground(lastColor);
                                v.setPivotX(getColPivot(lastColor));
                                v.invalidate();
                                Log.d("obj", dv + "");
                                Log.d("x", x_cord + "");
                                Log.d("y", y_cord + "");
                                //dv.setBackgroundResource(R.drawable.grass);
                                // dv.setX(x_cord);
                                //dv.setY((v.getHeight()/2);
                                // dv.setX(x_cord-(dv.getWidth()/2));
                                //dv.setY(y_cord);
                                //redraw
                                dv.invalidate();
                                clearBoard(ln);
                                break;
                            //return true;
                        }
                        return true;
                    }

                });
                vl.addView(grass2);
            }
            ln.addView(vl);
            Log.d("ChildCount", ln.getChildCount() +"");

        }


        //(getFromGrid((LinearLayout)findViewById(R.id.linearLayout1), 0, 0)).setBackgroundResource(R.drawable.sun);

    }
    /*
    * Remove matched orbs
    *
    * */
    private void clearBoard(LinearLayout board){
        //first element orb
        int yLen = ((LinearLayout)(board.getChildAt(0))).getChildCount(); // y dimension of board
        ImageView v = getFromGrid(board, 0, 0);
        Drawable vEle; // = v.getBackground();
        Drawable lastEle = v.getBackground();
        boolean same;
        int count; // counts the number of consecutive elements
        //int ys = 0;

        // clear vertical matches
        for (int x = 0; x < board.getChildCount(); x++){
            count = 0;
            for (int y = 0; y < yLen; y++){
                // Grab colour of current indices
                v = getFromGrid(board, x, y);
                vEle = v.getBackground();

                //Log.d("Status:", lastEle.getConstantState().equals(vEle.getConstantState()) + " " + count);
                same = getColPivot(lastEle) == getColPivot(vEle);

                if (same){
                    count++;
                }

                /*
                * X
                * X
                * O
                * X
                * X
                * */
                // If new element has been found, or if it is the last element, check and clear
                if (!same){
                    clearHorizontalMatchAt(x,y,count,board);
                    count = 1;
                    lastEle = v.getBackground();
                }
            }
            clearHorizontalMatchAt(x,(yLen),count,board);

        }


        // clear horizontal matches
        for (int y = 0; y < yLen; y++){
            count = 0;
            for (int x = 0; x < board.getChildCount(); x++){
                // Grab colour of current indices
                v = getFromGrid(board, x, y);
                vEle = v.getBackground();

                //Log.d("Status:", lastEle.getConstantState().equals(vEle.getConstantState()) + " " + count);
                same = getColPivot(lastEle) == getColPivot(vEle);

                if (same){
                    count++;
                }

                /*
                * X
                * X
                * O
                * X
                * X
                * */
                // If new element has been found, or if it is the last element, check and clear
                if (!same){
                    clearVerticalMatchAt(x,y,count,board);
                    count = 1;
                    lastEle = v.getBackground();
                }
            }
            clearVerticalMatchAt(board.getChildCount(), y,count,board);

        }
    }

    /*
    * Returns the float value associated with the background.
    *
    * Returns 0 if the background is not a valid color (white, red, green or blue).
    * */
    private float getColPivot(Drawable bg){

        if ((getResources().getDrawable(R.drawable.air)).getConstantState().equals(bg.getConstantState())){
            return AIR;
        }

        if ((getResources().getDrawable(R.drawable.fire)).getConstantState().equals(bg.getConstantState())){
            return FIRE;
        }

        if ((getResources().getDrawable(R.drawable.grass)).getConstantState().equals(bg.getConstantState())){
            return GRASS;
        }

        if ((getResources().getDrawable(R.drawable.sky)).getConstantState().equals(bg.getConstantState())){
            return SKY;
        }

        return 0;

    }
    private void clearHorizontalMatchAt(int x, int y, int count, LinearLayout board) {
        if (count >= 3) {
            //clearMatchAtXYC(x, y, count, board);

            for (int i = 0; i < (count); i++) {
                ImageView vc = getFromGrid(board, x, (y - count) + i);
                vc.setBackgroundResource((R.drawable.sun));
                //vc.setPivotX(getColPivot(R.()));
                vc.setPivotX(0);
            }
        }
    }
    private void clearVerticalMatchAt(int x, int y, int count, LinearLayout board) {
        if (count >= 3) {
            //clearMatchAtXYC(x, y, count, board);

            for (int i = 0; i < (count); i++) {
                ImageView vc = getFromGrid(board, (x - count) + i, y);
                vc.setBackgroundResource((R.drawable.sun));
                vc.setPivotX(0);
            }
        }
    }
    /*
    *
    * Remove matched orbs
    *
    * Same as clearBoard, messier code
    *
    * */
    private void checkBoard(LinearLayout board){
        int count;
        boolean same;
        int offset;
        LinearLayout row, col;
        Drawable element;
        for (int h = 0; h < board.getChildCount(); h++){
            // Get row
            LinearLayout v = (LinearLayout)board.getChildAt(h);

            // Get colour of first value in column
            col = (LinearLayout)(board.getChildAt(h)); // col at h
            element = (Drawable)(col.getChildAt(0).getBackground()); // first val of first col
           // col.getChildAt(0).setBackgroundResource(R.drawable.sun);
            count = 0;
            for (int i = 0; i < v.getChildCount(); i++) {
                ImageView vi = (ImageView)v.getChildAt(i);
                Drawable bgi = vi.getBackground();
                same = (bgi.getConstantState().equals(element.getConstantState()));
                if (same) {
                    count ++;
                }

                // If the element is not consistent or we have reached the last element on the board
                if ((!same) || (i == (v.getChildCount() - 1))){
                    // Set offset to 0 when: last element, last element and no continuation.
                    // Exclude the case where it is the last element and continuation holds.
                    /* P = Last element, Q = element is a continuation
                    * [ P V (P & ~Q) ] & !( P & Q) ]
                    * */
                   // offset = ((i == (v.getChildCount() - 1) || !same))? 0:1;
                    if (count >= 3){
                        offset = (((i == (v.getChildCount() - 1)) || ((i == (v.getChildCount() - 1)) && !same))
                                && !(((i == (v.getChildCount() - 1))) && !same)) ? 0:1;
                        for (int j = offset; j < (count + offset); j ++){
                          //  Log.d("Index", "i = " + i + "; j = " + offset + "");
                            ImageView t = (ImageView)v.getChildAt(i - j);
                            t.setBackgroundResource((R.drawable.sun));
                        }
                    }
                   // Log.d("WOWOWOW", "a match");
                    count = 1;
                    element = bgi;
                }



            }

        }


        LinearLayout v = (LinearLayout)board.getChildAt(0); // Layout at col 0
        for (int y = 0; y < ((LinearLayout)board.getChildAt(0)).getChildCount(); y++){ // iterate y
            count = 0;
            col = (LinearLayout)(board.getChildAt(0)); // Val at y = h, x = i h
            element = (Drawable)(col.getChildAt(y).getBackground()); // First element
            //col.getChildAt(h).setBackgroundResource(R.drawable.sun);
            Log.d("count ", "" + board.getChildCount());
            for (int x = 0; x < board.getChildCount(); x++) { // iterate  y
                // Get child at x, y
                LinearLayout vi = (LinearLayout)(((LinearLayout)board.getChildAt(x)).getChildAt(y));
                Drawable bgi = vi.getBackground(); // get background
                same = (bgi.getConstantState().equals(element.getConstantState())); // compare bg

                if (same) {
                    count ++;
                }

                // If the element is not consistent or we have reached the last element on the board
                if ((!same) || (x == (v.getChildCount() - 1))){ // If end or new color
                    // Offset when...
                    if (count >= 3){
                        offset = 0;
                        Log.d("Found a match", "x: " + x + ", x: " + y + " offset: " + offset);
                        for (int j = offset; j < (count + offset); j ++){
                           // Log.d("Index", "i = " + i + "; j = " + j + "");
                           // LinearLayout rowView = ((LinearLayout)v.getChildAt(h));
                         //   LinearLayout colView = (LinearLayout)(board.getChildAt(i-j));

                         //   ImageView colVal = (ImageView)(colView.getChildAt(h));
                         //   colVal.setBackgroundResource((R.drawable.sun));
                            //colVal.setColorFilter(1);
                        }
                    }
                    //Log.d("WOWOWOW", "a match");
                    count = 1;
                    element = bgi;
                }


            v = (LinearLayout)board.getChildAt(y + 1);

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_android_sample, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return false;
    }
}
