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

    private int DISPLAYX = 0;
    private int DISPLAYY = 0;

    private int NUMORBS = 9;

    private Drawable lastColor;
    private ImageView lastElement;
    private static final String IMAGEVIEW_TAG = "Android Logo";
    GridLayout gridLayout;

    private ImageView getFromGrid(LinearLayout grid, int x, int y){
        return (ImageView)((LinearLayout)(grid.getChildAt(x))).getChildAt(y);
    }

    private void changeOrb(View v, Drawable lastColor){
        v.setBackground(lastColor);
        v.setPivotX(getColPivot(lastColor));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] grid = new String[16];

        setContentView(R.layout.activity_android_sample);

        //get the sun View
        final ImageView grass = (ImageView) findViewById(R.id.grass);

        // get size of display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        DISPLAYX = size.x;
        DISPLAYY = size.y;

        //TODO Make board population prettier

        for (int h = 0; h <= 4; h ++){
            final LinearLayout ln = ((LinearLayout)findViewById(R.id.linearLayout1));
            final LinearLayout vl = new LinearLayout(this);
            vl.setId(2000 + h);
            vl.setOrientation(LinearLayout.VERTICAL);
            Log.d("whoa", "another add");
            for (int i = 0; i <= 4; i ++){
                final ImageView grass2 = new ImageView(getApplicationContext());


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

                grass2.setMinimumWidth(DISPLAYY/numSquares);
                grass2.setMinimumHeight(DISPLAYY/numSquares);

                grass2.setMaxWidth((int)((DISPLAYY/numSquares) * .7));
                grass2.setMaxHeight((int)((DISPLAYY/numSquares) * .7));

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

                                int x_cord = (int) event.getX();
                                int y_cord = (int) event.getY();

                                ImageView dv = (ImageView) event.getLocalState();
                                Drawable temp = v.getBackground();

                                // Change Colors
                                changeOrb(lastElement, temp);
                                changeOrb(v, lastColor);

                                // Set set pointer to lastElement
                                lastElement = (ImageView)v;
                                break;
                            case DragEvent.ACTION_DROP:
                                Log.d("msg2", "dropped");
                                x_cord = (int) v.getX();
                                y_cord = (int) v.getY();
                                dv = (ImageView) event.getLocalState();

                               // Change orb color
                                changeOrb(v, lastColor);
                               // v.setBackground(lastColor);
                               // v.setPivotX(getColPivot(lastColor));

                                v.invalidate();
                                dv.invalidate();

                                clearBoard(ln);
                                gravity(ln);
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

                same = getColPivot(lastEle) == v.getPivotX();

                if (same){
                    count++;
                }


                // If new element has been found, or if it is the last element, check and clear
                if (!same){
                    clearVerticalMatchAt(x,y,count,board);
                    count = 1; // We have seen one of this colour
                    lastEle = v.getBackground();
                }
            }
            clearVerticalMatchAt(x,(yLen),count,board);

        }


        // clear horizontal matches
        for (int y = 0; y < yLen; y++){
            count = 0;
            for (int x = 0; x < board.getChildCount(); x++){
                // Grab colour of current indices
                v = getFromGrid(board, x, y);
                vEle = v.getBackground();

                //Log.d("Status:", lastEle.getConstantState().equals(vEle.getConstantState()) + " " + count);
                //same = getColPivot(lastEle) == getColPivot(vEle);

                same = getColPivot(lastEle) == v.getPivotX();
                if (same){
                    count++;
                }


                // If new element has been found, or if it is the last element, check and clear
                if (!same){
                    clearHorizontalMatchAt(x,y,count,board);
                    count = 1;
                    lastEle = v.getBackground();

                }
            }
            clearHorizontalMatchAt(board.getChildCount(), y,count,board);

        }

    }
    /*
    * Drops all colored orbs to the bottom of the board
    * */
    private void gravity(LinearLayout board){
        int yLen = ((LinearLayout)(board.getChildAt(0))).getChildCount(); // y dimension of board
        Drawable drawableAbove;
        int index = 0;
        ImageView[] l = generateElementList(100);
        for (int x = 0; x < board.getChildCount(); x++) {
            //index = 0;
            for (int y = 0; y < yLen; y++) {
                if (getColPivot(getFromGrid(board,x,y).getBackground()) == 0){
                    for (int z = (y); z > -1; z --){
                        //getFromGrid(board,x,z).setVisibility(View.INVISIBLE);

                        if (z > 0){
                            drawableAbove = getFromGrid(board, x, z - 1).getBackground();
                            Log.d("This should print", " ");
                        }
                        else {
                            drawableAbove = l[index].getBackground();
                            index++;
                            //  Beginnings of implementing gravity. Have percolate colours to the lowest level
                            //  Need to implement animations.
                            // Improve randomization.

                        }
                        Log.d("ERROR", "" + x + " " + y + " " + z);
                        changeOrb(getFromGrid(board,x,z),drawableAbove);
                       // getFromGrid(board,x,z)
                    }
                }
            }
        }
    }

    private ImageView[] generateElementList(int size){
        int elements[] = new int[]{
                (R.drawable.grass),
                (R.drawable.fire),
                (R.drawable.sky),
                (R.drawable.air),
        };
        final ImageView[] l = new ImageView[size];
        for (int i = 0; i < size; i++){
            final ImageView orb = new ImageView(getApplicationContext());

            orb.setBackgroundResource(elements[(int)(Math.random()*elements.length)]);

            orb.setMinimumWidth(DISPLAYX/NUMORBS);
            orb.setMinimumHeight(DISPLAYY/NUMORBS);

            orb.setMaxWidth((int)((DISPLAYY/NUMORBS) * .7));
            orb.setMaxHeight((int)((DISPLAYY/NUMORBS) * .7));

            // Set pivot to certain value so it may be distinguished back end
            orb.setPivotX(getColPivot(orb.getBackground()));

            l[i] = orb;
        }
        //Log.d("COLORS: ", l[0] + "" );
        return l;
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
    private void clearVerticalMatchAt(int x, int y, int count, LinearLayout board) {
        if (count >= 3) {
            //clearMatchAtXYC(x, y, count, board);

            for (int i = 0; i < (count); i++) {
                ImageView vc = getFromGrid(board, x, (y - count) + i);
                vc.setBackgroundResource((R.drawable.sun));
                //vc.setPivotX(-1);
            }
        }
    }
    private void clearHorizontalMatchAt(int x, int y, int count, LinearLayout board) {
        if (count >= 3) {
            //clearMatchAtXYC(x, y, count, board);

            for (int i = 0; i < (count); i++) {
                ImageView vc = getFromGrid(board, (x - count) + i, y);
                vc.setBackgroundResource((R.drawable.sun));
                //vc.setPivotX(-1);
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
