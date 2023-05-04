package com.example.myapplication;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Xml;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//size of imageview needs to be changed according to screen size

public class MainActivity extends AppCompatActivity implements View.OnTouchListener,View.OnClickListener {
    private static final int LOCATION_REQUEST = 222;
    private static final int READ_REQUEST_CODE = 333;
    private static final int CAMERA_REQUEST = 1888;
    int increment = 0;
    Boolean binCreated = false;
    boolean bitmapCopied = false;
    int textIncrement = 0;
    Bitmap copy = null;
    Button btnSaveImage;
    boolean isDrawing;
    int globid;


    boolean textTime = false;
    boolean notesVisible = false;
    Uri ImageLink;
    int switchTouch;
    EditText fileNameBox;
    Button savebtn;
    TextView txtView;
    TextView XMLtxt;
    ImageView bin;

    ImageView imageResult;
    final int SELECT_PICTURE = 5;
    final int RQS_IMAGE1 = 1;
    private View newLayout;
    Uri source;
    Bitmap bitmapMaster;
    Canvas canvasMaster;
    int layoutWidth;
    int layoutHeight;
    int prvX, prvY;

    String labelText;
    int switchClick = 0;
    Paint paintDraw;
    private Stack<Bitmap> bitmapHistory = new Stack<>();
    boolean canDrag = false;
    FrameLayout frame_layout;
    RelativeLayout relativeLayout, relativeLayout1, relativeLayout2;
    Button viewmore, viewmore1, viewmore2, click1, paintViewMore;
    int height, height1, height2;
    Button yellowPaint, whitePaint, greenPaint, blackPaint, redPaint, bluePaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        imageResult = (ImageView) findViewById(R.id.result);

        paintDraw = new Paint();
        paintDraw.setStyle(Paint.Style.FILL);
        paintDraw.setColor(Color.BLACK);
        paintDraw.setStrokeWidth(10);


        relativeLayout = (RelativeLayout) findViewById(R.id.expandable);
        relativeLayout1 = (RelativeLayout) findViewById(R.id.expandable1);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.expandable2);
        frame_layout = (FrameLayout) findViewById(R.id.frame_layout);
        viewmore = (Button) findViewById(R.id.viewmore);
        paintViewMore = (Button) findViewById(R.id.paintViewMore);
        viewmore1 = (Button) findViewById(R.id.viewmore1);
        viewmore2 = (Button) findViewById(R.id.viewmore2);
        whitePaint = (Button) findViewById(R.id.whitePaint);
        bluePaint = (Button) findViewById(R.id.bluePaint);
        greenPaint = (Button) findViewById(R.id.greenPaint);
        blackPaint = (Button) findViewById(R.id.blackPaint);
        yellowPaint = (Button) findViewById(R.id.yellowPaint);
        redPaint = (Button) findViewById(R.id.redPaint);
        click1 = (Button) findViewById(R.id.click1);
        paintViewMore.setOnClickListener(this);
        viewmore.setOnClickListener(this);
        viewmore.setOnClickListener(this);
        viewmore1.setOnClickListener(this);
        viewmore2.setOnClickListener(this);
        imageResult.setOnTouchListener(this);


        //get information from welcome screen and either draw a blank image or get background image
        frame_layout.post(new Runnable() {
            @Override
            public void run() {
                blankImage();
                String filename = "";
                filename = intent.getStringExtra("file");
                if (filename != "" && filename != null) {
                    loadXMLFile(filename);
                }
                int digit = 0;
                digit = intent.getIntExtra("digit", digit);
                if (digit == 1) {
                    blankImage();
                } else if (digit == 2) {
                    Intent intent3 = new Intent();
                    intent3.setType("image/*");
                    intent3.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent3, "Select Picture"), RQS_IMAGE1);
                    ;

                }
                // layout has been drawn

            }
        });


        //sets up the expandable layouts

        relativeLayout.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        relativeLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        System.out.println("PREDRAW");
                        relativeLayout.setVisibility(View.GONE);
                        relativeLayout1.setVisibility(View.GONE);
                        relativeLayout2.setVisibility(View.GONE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        relativeLayout.measure(widthSpec, heightSpec);
                        height = relativeLayout.getMeasuredHeight();

                        height1 = relativeLayout.getMeasuredHeight();

                        height2 = relativeLayout.getMeasuredHeight();
                        return true;
                    }

                });


    }


    // when pin clicked - expand or minimise views
    View.OnClickListener pinClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            System.out.println(id);
            int newWidth = 500;
            int newHeight = 500;

            ImageView imageView = findViewById(id);
            EditText editText = findViewById(id + 1);
            System.out.print(id + 1);
            Button btn = findViewById(id + 2);
            ImageView addImg = findViewById(id + 3);
            Button imgbtn = findViewById(id + 4);
            Button photobtn = findViewById(id + 5);
            System.out.println("EDIT TEXT" + editText.getVisibility());
            if (editText.getVisibility() == View.GONE) {
                System.out.println("SET VISIBLE");
                editText.setVisibility(View.VISIBLE);

                imgbtn.setVisibility(View.VISIBLE);
                addImg.setVisibility(View.VISIBLE);
                photobtn.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);

            } else if (editText.getVisibility() == View.VISIBLE) {
                editText.setVisibility(View.GONE);
                System.out.println(editText.getVisibility());
                btn.setVisibility(View.GONE);
                imgbtn.setVisibility(View.GONE);
                photobtn.setVisibility(View.GONE);
                addImg.setVisibility(View.GONE);
            }

            System.out.println(editText.getVisibility());
        }
    };
    View.OnClickListener notesVisibility = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            System.out.println(id);
            Button btn = findViewById(id);
            EditText editText = findViewById(id - 1);
            EditText notes = findViewById(id + 4);
            ImageView img = findViewById(id - 2);
            Button imgbtn = findViewById(id + 2);
            Button photobtn = findViewById(id + 3);
            ImageView addImg = findViewById(id + 1);
            if (notesVisible == false) {
                btn.setText("Minimise Text");
                btn.setX(frame_layout.getMeasuredWidth() - 300);
                btn.setY(frame_layout.getMeasuredHeight() - 200);
                notes.bringToFront();

                btn.bringToFront();
                notes.setVisibility(View.VISIBLE);
                imgbtn.setVisibility(View.GONE);
                photobtn.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                notesVisible = true;


            } else if (notesVisible) {
                int newX = (int) img.getX();
                int y = (int) img.getY();

                int[] coords = createCoords(newX, y);

                imgbtn.setVisibility(View.VISIBLE);
                photobtn.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                btn.setText("Expand Text");
                btn.setX(img.getX() + coords[6]);
                btn.setY(img.getY() + coords[7]);

                notes.setVisibility(View.GONE);
                notesVisible = false;

            }
        }
    };


    //enlarge photo if clicked (image in pins)
    View.OnClickListener enlargeImg = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            System.out.println(id);
            ImageView enImg = findViewById(id);
            ImageView img = findViewById(id - 3);
            if (enImg.getX() != 0) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
                enImg.setLayoutParams(params);
                enImg.setX(0);
                enImg.setY(0);
                enImg.bringToFront();
                enImg.setElevation(10f);

            }
            //IF OVER HALFWAY CHANGE TEXT BOXES AND IMAGES TO OTHER SIDE OF PIN SO IT DOESNT GO OFF SCREEN
            else if (enImg.getX() == 0) {
                ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
                enImg.setLayoutParams(params);
                enImg.setX(img.getX() + 300);
                enImg.setY(img.getY() - 50);
                enImg.setElevation(5f);


            }

        }
    };


    View.OnClickListener addImgToPin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            globid = v.getId();

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);


        }
    };
    View.OnClickListener takePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            globid = view.getId();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    };


    final int HOLD_DURATION = 2000; // 2 seconds
    final Handler handler = new Handler();
    final Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            canDrag = true;
        }
    };

    // if user holds pin down for more than 2 seconds, drag, else, pinclick
    View.OnTouchListener pinTouch = new View.OnTouchListener() {
        float dX;
        float dY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            bin.setVisibility(View.VISIBLE);
            int x = (int) event.getX();
            int y = (int) event.getY();
            int id = v.getId();
            EditText editText = findViewById(id + 1);
            Button btn = findViewById(id + 2);
            ImageView imgView = findViewById(id + 3);
            Button imgbtn = findViewById(id + 4);
            Button photobtn = findViewById(id + 5);
            System.out.println(imgbtn.getText().toString());

            System.out.println(photobtn.getText().toString());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.postDelayed(longPressRunnable, HOLD_DURATION);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canDrag) {
                        int newX = (int) event.getRawX();

                        int[] coords = createCoords(newX, y);

                        v.animate()
                                .x(event.getRawX() - 20 + dX)
                                .y(event.getRawY() - 300 + dY)
                                .setDuration(0)
                                .start();
                        imgView.animate()
                                .x(event.getRawX() - 20 + coords[0] + dX)
                                .y(event.getRawY() - 300 - coords[1] + dY)
                                .setDuration(0)
                                .start();
                        imgbtn.animate()
                                .x(event.getRawX() - 20 + coords[2] + dX)
                                .y(event.getRawY() - 300 + coords[3] + dY)
                                .setDuration(0)
                                .start();
                        photobtn.animate()
                                .x(event.getRawX() - 20 + coords[8] + dX)
                                .y(event.getRawY() - 300 + coords[9] + dY)
                                .setDuration(0)
                                .start();
                        btn.animate()
                                .x(event.getRawX() - 20 + coords[6] + dX)
                                .y(event.getRawY() - 300 + coords[7] + dY)
                                .setDuration(0)
                                .start();
                        editText.animate()
                                .x(event.getRawX() - 20 + coords[4] + dX)
                                .y(event.getRawY() - 300 - coords[5] + dY)
                                .setDuration(0)
                                .start();

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int[] imageViewLocation = new int[2];
                    int[] binImageLocation = new int[2];
                    v.getLocationOnScreen(imageViewLocation);
                    bin.getLocationOnScreen(binImageLocation);
                    Rect imageRect = new Rect(imageViewLocation[0], imageViewLocation[1], imageViewLocation[0] + v.getWidth(), imageViewLocation[1] + v.getHeight());
                    Rect binRect = new Rect(binImageLocation[0], binImageLocation[1], binImageLocation[0] + bin.getWidth(), binImageLocation[1] + bin.getHeight());
                    if (Rect.intersects(imageRect, binRect)) {
                        frame_layout.removeView(v);
                        frame_layout.removeView(editText);
                        frame_layout.removeView(imgView);
                        frame_layout.removeView(imgbtn);
                        frame_layout.removeView(photobtn);
                        frame_layout.removeView(btn);
                        bin.setVisibility(View.GONE);

                    } else {
                        bin.setVisibility(View.GONE);
                    }


                case MotionEvent.ACTION_CANCEL:
                    try {
                        handler.removeCallbacks(longPressRunnable);
                        canDrag = false;

                        v.performClick();
                    } finally {
                        break;
                    }

                default:
                    return false;
            }
            return true;
        }
    };


//allows text to be dragged

    View.OnTouchListener textTouch = new View.OnTouchListener() {

        float dX;
        float dY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            bin.setVisibility(View.VISIBLE);
            int x = (int) event.getX();
            int y = (int) event.getY();
            int id = v.getId();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.postDelayed(longPressRunnable, HOLD_DURATION);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canDrag) {
                        int newX = (int) event.getRawX();

                        v.animate()
                                .x(event.getRawX() - 20 + dX)
                                .y(event.getRawY() - 300 + dY)
                                .setDuration(0)
                                .start();

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int[] imageViewLocation = new int[2];
                    int[] binImageLocation = new int[2];
                    v.getLocationOnScreen(imageViewLocation);
                    bin.getLocationOnScreen(binImageLocation);
                    Rect imageRect = new Rect(imageViewLocation[0], imageViewLocation[1], imageViewLocation[0] + v.getWidth(), imageViewLocation[1] + v.getHeight());
                    Rect binRect = new Rect(binImageLocation[0], binImageLocation[1], binImageLocation[0] + bin.getWidth(), binImageLocation[1] + bin.getHeight());
                    if (Rect.intersects(imageRect, binRect)) {
                        frame_layout.removeView(v);

                        bin.setVisibility(View.GONE);

                    } else {
                        bin.setVisibility(View.GONE);
                    }

                case MotionEvent.ACTION_CANCEL:
                    try {
                        handler.removeCallbacks(longPressRunnable);
                        canDrag = false;
                        System.out.println("CANCEL");
                        v.performClick();
                    } finally {
                        break;
                    }
                default:
                    return false;
            }
            return true;
        }
    };

    //if canvas touched - either add pin, draw or draw straight line (depending on what feature selected)


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == imageResult) {
            if (switchTouch == 1) {
                if (binCreated == false) {
                    bin = new ImageView(this);
                    bin.setImageResource(R.drawable.bin);
                    bin.setTag("bin");

                }
                int action = event.getAction();


                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        EditText editText = new EditText(this);
                        ImageView img = new ImageView(this);
                        ImageView addImg = new ImageView(this);
                        addImg.setImageResource(R.drawable.white);
                        img.setImageResource(R.drawable.pin2);

                        Button btn = new Button(this);
                        Button imgbtn = new Button(this);
                        Button photobtn = new Button(this);

                        EditText notes = new EditText((this));
                        notes.setBackgroundColor(Color.WHITE);


                        editText.setBackground(getResources().getDrawable(R.drawable.boxwithborderxml));

                        imgbtn.setText("UPLOAD");
                        imgbtn.setTextSize(8);

                        photobtn.setText("PHOTO");
                        photobtn.setTextSize(8);
                        btn.setText("Expand Text");
                        btn.setTextSize(8);
                        int i = getIncrement();
                        plusIncrement();
                        img.setId(1000 + i);
                        editText.setId(1000 + i + 1);
                        btn.setId(1000 + i + 2);
                        addImg.setId(1000 + i + 3);
                        imgbtn.setId(1000 + i + 4);
                        photobtn.setId(1000 + i + 5);
                        notes.setId(1000 + i + 6);


                        int id = img.getId();
                        System.out.println(id);
                        System.out.println("TOUCH");
                        img.setOnClickListener(pinClick);
                        img.setOnTouchListener(pinTouch);
                        btn.setOnClickListener(notesVisibility);
                        imgbtn.setOnClickListener(addImgToPin);
                        addImg.setOnClickListener(enlargeImg);
                        photobtn.setOnClickListener(takePhoto);
                        notes.setVisibility(View.VISIBLE);
                        editText.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.VISIBLE);
                        imgbtn.setVisibility(View.VISIBLE);
                        addImg.setVisibility(View.VISIBLE);
                        photobtn.setVisibility(View.VISIBLE);
                        notes.setVisibility(View.GONE);
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        int[] coords = createCoords(x, y);
                        editText.setHint("TITLE");
                        addImg.setX(x + coords[0]);
                        addImg.setY(y - coords[1]);
                        imgbtn.setX(x + coords[2]);
                        imgbtn.setY(y + coords[3]);
                        editText.setX(x + coords[4]);
                        editText.setY(y - coords[5]);
                        editText.setTextSize(10);
                        btn.setX(x + coords[6]);
                        btn.setY(y + coords[7]);
                        photobtn.setX(x + coords[8]);
                        photobtn.setY(y + coords[9]);
                        notes.setX(0);
                        notes.setY(0);
                        notes.setGravity(Gravity.TOP);
                        img.setX(x);
                        img.setY(y);
                        Glide.with(this)
                                .load(R.drawable.pin2)
                                .centerCrop()
                                .into(img);
                        Glide.with(this)
                                .load(R.drawable.white)
                                .centerCrop()
                                .into(addImg);
                        if (binCreated == false) {
                            Glide.with(this)
                                    .load(R.drawable.bin)
                                    .centerCrop()
                                    .into(bin);
                            frame_layout.addView(bin, 100, 100);
                            bin.setVisibility(View.GONE);
                            binCreated = true;
                        }
                        frame_layout.addView(img, 90, 90);
                        frame_layout.addView(editText, 200, 100);
                        frame_layout.addView(btn, 150, 100);
                        frame_layout.addView(addImg, 200, 200);
                        frame_layout.addView(imgbtn, 150, 100);
                        frame_layout.addView(photobtn, 150, 100);
                        frame_layout.addView(notes, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                        notes.setVisibility(View.GONE);
                        break;
                }


                return true;
            }
            else if(textTime){
                int action = event.getAction();


                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (binCreated == false) {
                            bin = new ImageView(this);
                            bin.setImageResource(R.drawable.bin);
                            bin.setTag("bin");
                            Glide.with(this)
                                    .load(R.drawable.bin)
                                    .centerCrop()
                                    .into(bin);
                            frame_layout.addView(bin, 100, 100);
                            bin.setVisibility(View.GONE);
                            binCreated = true;
                        }
                        EditText editText = new EditText(this);
                        editText.setBackground(getResources().getDrawable(R.drawable.boxwithborderxml));
                        editText.setOnTouchListener(textTouch);
                        editText.setGravity(Gravity.CENTER);

                        int i = getTextIncrement();
                        plusTextIncrement();
                        editText.setId(2000+i);
                        editText.setText(labelText);
                        textTime = false;
                        System.out.println(textTime);
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        editText.setX(x);
                        editText.setY(y);
                        editText.setTextSize(12);

                        frame_layout.addView(editText,200,99);


                }

            }else {

                if (!isDrawing) {

                    int action = event.getAction();
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            if (!bitmapCopied) {
                                copy = bitmapMaster.copy(bitmapMaster.getConfig(), true);
                                bitmapHistory.push(copy);

                                bitmapCopied = true;
                            }

                            prvX = x;
                            prvY = y;
                            drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                            prvX = x;
                            prvY = y;
                            break;
                        case MotionEvent.ACTION_UP:
                            drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                            bitmapCopied = false;
                            break;
                    }

                }
                else {
                    // Draw a straight line


                    int action = event.getAction();
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            if (!bitmapCopied) {
                                copy = bitmapMaster.copy(bitmapMaster.getConfig(), true);
                                bitmapHistory.push(copy);

                                bitmapCopied = true;


                            }
                            prvX = x;
                            prvY = y;
                            break;
                        case MotionEvent.ACTION_MOVE:


                            // draw a new line from the starting point to the current touch point
                            drawStraightLineOnBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                            break;
                        case MotionEvent.ACTION_UP:

                            canvasMaster.drawBitmap(copy, 0, 0, null);
                            drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                            bitmapCopied = false;
                            break;

                    }

                }
                /*
                 * Return 'true' to indicate that the event have been consumed.
                 * If auto-generated 'false', your code can detect ACTION_DOWN only,
                 * cannot detect ACTION_MOVE and ACTION_UP.
                 */
                imageResult.invalidate();
                return true;
            }


        }
        return false;
    }

    @Override
    public void onClick(View v) {
        System.out.println("hi");
        switch (v.getId()) {
            case R.id.viewmore:
                if (relativeLayout.getVisibility() == View.GONE) {
                    expand(relativeLayout, height);
                } else {
                    collapse(relativeLayout);
                }
                break;

            case R.id.viewmore1:
                if (switchClick == 0) {
                    collapse(relativeLayout1);
                    collapse(relativeLayout);
                    collapse(relativeLayout2);
                    switchClick = 1;
                    Toast.makeText(this,"Please press the save button again",Toast.LENGTH_SHORT).show();

                } else if (switchClick == 1) {
                    try {
                        frame_layout.removeView(bin);
                    } finally {
                        getFileName(".xml", saveName);
                        switchClick = 0;
                    }

                }

                break;
            case R.id.paintViewMore:
                System.out.println("ASDASDFREFGRG");
                if (relativeLayout1.getVisibility() == View.GONE) {
                    System.out.println("HI");
                    expand(relativeLayout1, height);
                } else {
                    System.out.println("ASDASD");
                    collapse(relativeLayout1);
                }
                break;
            case R.id.viewmore2:
                Intent intent = new Intent(MainActivity.this, Load.class);
                startActivity(intent);

                break;
            case R.id.click1:
                switchTouch = toggle();
                break;
            case R.id.createPDFbutton:
                createFirstBitmap();

            case R.id.bluePaint:

                collapse(relativeLayout1);


                break;
            case R.id.drawStraightLine:
                isDrawingToggle();
                switchTouch = 0;
                break;
            case R.id.drawText:
                getFileName("text",saveName);

        }


    }

    public void undoLastDrawing(View v) {
        if (!bitmapHistory.isEmpty()) {
            bitmapMaster = bitmapHistory.pop();
            canvasMaster = new Canvas(bitmapMaster);
            imageResult.setImageBitmap(bitmapMaster);
            imageResult.invalidate();
            // update the UI)
        }
    }


    public int[] createCoords(int x, int y) {
        System.out.print("XCOORD : " + x);
        int halfway = frame_layout.getWidth() / 2;
        System.out.println("||");
        System.out.println(halfway);
        if (x <= halfway) {
            int[] coords = {350, 0, 250, 175, 100, 50, 150, 250, 100, 175};
            return coords;

        } else {
            int[] coords = {-450, 0, -250, 175, -250, 50, -150, 250, -100, 175};
            return coords;
        }

    }

    public void redColour(View v) {
        paintDraw.setColor(Color.RED);
    }

    public void whiteColour(View v) {
        paintDraw.setColor(Color.WHITE);
    }

    public void blackColour(View v) {
        paintDraw.setColor(Color.BLACK);

    }

    public void greenColour(View v) {
        paintDraw.setColor(Color.GREEN);
    }

    public void blueColour(View v) {
        paintDraw.setColor(Color.BLUE);
    }

    public void yellowColour(View v) {
        paintDraw.setColor(Color.YELLOW);
    }

    public int getTextIncrement() {
        return textIncrement;
    }

    public void plusTextIncrement() {
        System.out.println("PLUS INCREMENT");
        textIncrement = textIncrement + 1;
    }

    public int getIncrement() {
        return increment;
    }

    public void plusIncrement() {
        System.out.println("PLUS INCREMENT");
        increment = increment + 7;
    }


//toggles whether to add text or not
    public boolean textToggle(){
        isDrawing = false;
        switchTouch = 0;
        if (textTime){
            textTime = false;

        }
    else if (!textTime){
            textTime = true;
        }
        Toast.makeText(MainActivity.this,
                "Touch where you want text to be", Toast.LENGTH_SHORT).show();
        return textTime;
    }

    //toggles whether to add pin or not
    public int toggle() {
        isDrawing = false;
        if (switchTouch == 0) {
            switchTouch = 1;
        } else if (switchTouch == 1) {
            switchTouch = 0;
        }
        return switchTouch;
    }

    //toggles whether to draw straight line or not
    public boolean isDrawingToggle() {
        switchTouch = 0;
        if (!isDrawing) {
            isDrawing = true;
        } else if (isDrawing) {
            isDrawing = false;
        }
        return isDrawing;
    }


    //expand expandable layouts
    private void expand(RelativeLayout layout, int layoutHeight) {
        layout.setVisibility(View.VISIBLE);
        ValueAnimator animator = slideAnimator(layout, 0, layoutHeight);
        animator.start();
    }

    private void collapse(final RelativeLayout layout) {
        int finalHeight = layout.getHeight();
        ValueAnimator mAnimator = slideAnimator(layout, finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                layout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();


    }


    private ValueAnimator slideAnimator(final RelativeLayout layout, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.height = value;
                layout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    //gather string from user, email, savename, or text
    View.OnClickListener saveName = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String filenameText = fileNameBox.getText().toString();
            if (XMLtxt.getText().toString().equals(".xml")) {
                frame_layout.removeView(fileNameBox);
                frame_layout.removeView(savebtn);
                frame_layout.removeView(txtView);
                frame_layout.removeView(XMLtxt);

                saveXMLFile(filenameText);
            } else if (txtView.getText().toString().equals("Email:")) {
                frame_layout.removeView(fileNameBox);
                frame_layout.removeView(savebtn);
                frame_layout.removeView(txtView);
                frame_layout.removeView(XMLtxt);
                emailPDFFile(filenameText);
            }
            else{
                frame_layout.removeView(fileNameBox);
                frame_layout.removeView(savebtn);
                frame_layout.removeView(txtView);
                frame_layout.removeView(XMLtxt);
                labelText = filenameText;
                textToggle();
            }


        }
    };

    public void emailPDFFile(String filenameText) {
        File file = new File(getFilesDir() + "/PDF/", "Your_PDF_Report.pdf");
        Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.myapplication.fileprovider", file);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{filenameText});
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "_ Your PDF Report from SiteBuddy _");

        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void getFileName(String fileType, View.OnClickListener onClickListenerForFile) {
        fileNameBox = new EditText(this);
        savebtn = new Button(this);
        txtView = new TextView(this);
        XMLtxt = new TextView(this);
        XMLtxt.setX(520);
        XMLtxt.setY(100);
        savebtn.setX(50);
        savebtn.setY(400);
        fileNameBox.setBackgroundResource(R.drawable.boxwithborderxml);
        savebtn.setOnClickListener(onClickListenerForFile);
        fileNameBox.setX(50);
        fileNameBox.setY(100);
        txtView.setX(50);
        txtView.setY(0);
        txtView.setBackgroundColor(Color.WHITE);

        if (fileType.equals("text")){
                    txtView.setText("Label name : ");
                    savebtn.setText("OK");
                }
        else if (!fileType.equals(".pdf")) {
            XMLtxt.setText(fileType);
            frame_layout.addView(XMLtxt);
            txtView.setText("Save Name:");
            savebtn.setText("SAVE");
        }
       else {
            txtView.setText("Email:");
            savebtn.setText("SEND");
        }

        frame_layout.addView(fileNameBox, 500, 100);
        frame_layout.addView(txtView, 300, 100);

        frame_layout.addView(savebtn, 200, 125);

    }

    public void saveXMLFile(String filenameText) {


        String data = null;
        System.out.println("START SAVE");

        FileOutputStream fos;

        try {
            fos = openFileOutput(filenameText + ".xml", Context.MODE_PRIVATE);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "viewgroup");
            for (int i = 0; i < frame_layout.getChildCount(); i++) {
                View child = frame_layout.getChildAt(i);
                serializer.startTag(null, "view");
                // Set attributes for the view
                serializer.attribute(null, "type", child.getClass().getSimpleName());
                if (child instanceof EditText) {

                    serializer.attribute(null, "text", ((EditText) child).getText().toString());
                    child.setVisibility(View.VISIBLE);
                    serializer.attribute(null, "layout_x", Float.toString(child.getX()));
                    serializer.attribute(null, "layout_y", Float.toString(child.getY()));
                    serializer.attribute(null, "layout_width", Float.toString(child.getWidth()));
                    serializer.attribute(null, "layout_height", Float.toString(child.getHeight()));
                    serializer.attribute(null, "id", Integer.toString(child.getId()));
                    System.out.println(child.getVisibility());
                    if ((int)child.getY()==0){
                        child.setVisibility(View.GONE);
                    }
                }
                if (child instanceof ImageView) {
                    // Convert the image to a base64-encoded string
                    ImageView imageView = (ImageView) child;
                    System.out.println(child.getTag());

                    child.setVisibility(View.VISIBLE);
                    Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte[] imageBytes = outputStream.toByteArray();
                    String base64EncodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    serializer.attribute(null, "layout_x", Float.toString(child.getX()));
                    serializer.attribute(null, "src", base64EncodedImage);

                    serializer.attribute(null, "layout_y", Float.toString(child.getY()));
                    serializer.attribute(null, "layout_width", Float.toString(child.getWidth()));
                    serializer.attribute(null, "layout_height", Float.toString(child.getHeight()));
                    if (child.getClass().getName().equals("android.widget.ImageView")) {
                        serializer.attribute(null, "id", Integer.toString(child.getId()));
                    }


                    System.out.println(child.getVisibility());


                }
                if (child instanceof Button) {
                    serializer.attribute(null, "Button_text", ((Button) child).getText().toString());
                    child.setVisibility(View.VISIBLE);
                    serializer.attribute(null, "layout_x", Float.toString(child.getX()));
                    serializer.attribute(null, "layout_y", Float.toString(child.getY()));
                    serializer.attribute(null, "layout_width", Float.toString(child.getWidth()));
                    serializer.attribute(null, "layout_height", Float.toString(child.getHeight()));
                    serializer.attribute(null, "id", Integer.toString(child.getId()));
                    System.out.println(child.getVisibility());
                }
                // Add any other view-specific attributes here
                serializer.endTag(null, "view");
            }
            serializer.endTag(null, "viewgroup");

            serializer.endDocument();
            serializer.flush();
            fos.close();
            File file = new File(getFilesDir(), filenameText + ".xml");
            if (file.exists()) {
                System.out.println("File saved at: " + file.getAbsolutePath());
            } else {
                System.out.println("Error: File not found");
            }

            System.out.println("Success");
        } catch (Exception e) {
            System.out.println(e);

        }
    }

    private View createViewFromXml(XmlPullParser parser, int i) {
        String type = parser.getAttributeValue(null, "type");
        if (type.equals("Button")) {
            System.out.println(type);
            Button button = new Button(this);
            String text = parser.getAttributeValue(null, "Button_text");
            Float layoutx = Float.parseFloat(parser.getAttributeValue(null, "layout_x"));
            Float layouty = Float.parseFloat(parser.getAttributeValue(null, "layout_y"));
            float layoutwidth = Float.parseFloat(parser.getAttributeValue(null, "layout_width"));
            float layoutheight = Float.parseFloat(parser.getAttributeValue(null, "layout_height"));
            int id = Integer.parseInt(parser.getAttributeValue(null, "id"));
            button.setId(id);
            int a = (int) layoutheight;
            int b = (int) layoutwidth;
            button.setText(text);
            System.out.println(text);
            System.out.println(layoutheight);
            System.out.println(layoutwidth);
            button.setX(layoutx);
            button.setY(layouty);
            button.setWidth(b);
            button.setHeight(a);
            button.setVisibility(View.VISIBLE);

            if (text.equals("Expand Text") | text == "Minimise Text") {
                System.out.println("EXPANDTEXTBUTTON FOUND");
                button.setOnClickListener(notesVisibility);

            } else if (text.equals("Minimise Text")) {
                System.out.println("EXPANDTEXTBUTTON FOUND");
                button.setOnClickListener(notesVisibility);
            } else if (text.equals("UPLOAD")) {
                button.setOnClickListener(addImgToPin);
            } else if (text.equals("PHOTO")) {
                button.setOnClickListener(takePhoto);
            }
            button.setTextSize(8);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) layoutwidth, (int) layoutheight);
            button.setLayoutParams(layoutParams);

            // Set any other view-specific attributes here
            return button;
        } else if (type.equals("EditText")) {
            System.out.println(type);
            EditText textView = new EditText(this);
            String text = parser.getAttributeValue(null, "text");
            Float layoutx = Float.parseFloat(parser.getAttributeValue(null, "layout_x"));
            Float layouty = Float.parseFloat(parser.getAttributeValue(null, "layout_y"));
            float layoutwidth = Float.parseFloat(parser.getAttributeValue(null, "layout_width"));
            float layoutheight = Float.parseFloat(parser.getAttributeValue(null, "layout_height"));
            int id = Integer.parseInt(parser.getAttributeValue(null, "id"));
            int a = (int) layoutheight;
            int b = (int) layoutwidth;
            textView.setBackgroundColor(Color.WHITE);
            textView.setText(text);
            System.out.println(text);
            textView.setX(layoutx);
            textView.setY(layouty);
            textView.setGravity(Gravity.TOP);
            if (a <= 150){
                textView.setVisibility(View.VISIBLE);
                textView.setBackground(getResources().getDrawable(R.drawable.boxwithborderxml));
            }
            else{
                textView.setVisibility(View.GONE);
            }

            textView.setId(id);
            if (a < 100){
                textView.setOnTouchListener(textTouch);

            }



            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) layoutwidth, (int) layoutheight);
            textView.setLayoutParams(layoutParams);

            // Set any other view-specific attributes here
            return textView;
        } else if (type.equals("ImageView")) {
            System.out.println(type);
            ImageView imageView = new ImageView(this);
            // Retrieve the base64-encoded image string from the XML element
            String base64EncodedImage = parser.getAttributeValue(null, "src");
            // Decode the image string into a byte array
            byte[] imageBytes = Base64.decode(base64EncodedImage, Base64.DEFAULT);

// Create a Bitmap from the byte array
            //Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap imageBitmap;
// Set the Bitmap as the image for an ImageView
            Glide.with(this)
                    .load(imageBytes)
                    .centerCrop()
                    .into(imageView);


            float layoutx = Float.parseFloat(parser.getAttributeValue(null, "layout_x"));
            float layouty = Float.parseFloat(parser.getAttributeValue(null, "layout_y"));
            float layoutwidth = Float.parseFloat(parser.getAttributeValue(null, "layout_width"));
            float layoutheight = Float.parseFloat(parser.getAttributeValue(null, "layout_height"));
            int id = Integer.parseInt(parser.getAttributeValue(null, "id"));

            imageView.setId(id);
            System.out.println(layoutheight);
            System.out.println(layoutwidth);
            imageView.setX(layoutx);
            imageView.setY(layouty);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) layoutwidth, (int) layoutheight);
            imageView.setLayoutParams(layoutParams);
            imageView.setVisibility(View.VISIBLE);
            // Set any other view-specific attributes here

            if ((int) layoutheight == 90) {
                System.out.println("PIN FOUND");
                System.out.println(imageView.getId());
                imageView.setOnClickListener(pinClick);
                imageView.setOnTouchListener(pinTouch);
            } else {

                imageView.setOnClickListener(enlargeImg);
            }

            return imageView;
        } else if (type.equals("AppCompatImageView")) {
            System.out.println(type);
            Bitmap tempBitmap;
            bitmapMaster.recycle();

            String base64EncodedImage = parser.getAttributeValue(null, "src");
            // Decode the image string into a byte array
            byte[] bitmapdata = Base64.decode(base64EncodedImage, Base64.DEFAULT);
            tempBitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);


            Bitmap.Config config;
            if (tempBitmap.getConfig() != null) {
                config = tempBitmap.getConfig();
            } else {
                config = Bitmap.Config.RGB_565;
            }

            //bitmapMaster is Mutable bitmap
            bitmapMaster = Bitmap.createBitmap(
                    tempBitmap.getWidth(),
                    tempBitmap.getHeight(),
                    config);

            canvasMaster = new Canvas(bitmapMaster);
            canvasMaster.drawBitmap(tempBitmap, 0, 0, null);
            imageResult.setScaleType(ImageView.ScaleType.FIT_XY);
            imageResult.setAdjustViewBounds(true);
            imageResult.setImageBitmap(bitmapMaster);

            System.out.println(imageResult);

        }

        return null;
    }

    public void loadXMLFile(String filename) {
        int i = 0;
        int counter = 0;
        System.out.print("LOAD");
        bitmapMaster.recycle();

        try {
            System.out.println("START LOAD");
            XmlPullParser parser = Xml.newPullParser();
            File file = new File(getFilesDir(), filename);
            FileInputStream fis = new FileInputStream(file);
            parser.setInput(fis, null);
            System.out.println("KEEP GOING");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                System.out.println(name);
                eventType = parser.getEventType();
                parser.next();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        System.out.println("START TAG");
                        if (name.equals("viewgroup")) {
                            // This is the root element of the view hierarchy
                            // Iterate over the child elements and use their data to create views
                            while (parser.next() != XmlPullParser.END_TAG) {
                                System.out.println("THSTART LOOP");
                                if (parser.getEventType() != XmlPullParser.START_TAG) {
                                    continue;
                                }
                                name = parser.getName();
                                System.out.println(name);

                                if (name.equals("view")) {

                                    if (counter % 7 == 0) {
                                        System.out.println("INCREMENT");        //get last
                                        i = getIncrement();
                                        System.out.println(i);
                                        plusIncrement();

                                    }
                                    System.out.println(i);
                                    // Extract data from the XML element and use it to create a new view
                                    View view = createViewFromXml(parser, i);

                                    if (view != null) {
                                        int viewsize = 200;
                                        System.out.println("WIDTH");
                                        System.out.println(view.getWidth());
                                        if (binCreated == false) {
                                            bin = new ImageView(this);
                                            bin.setImageResource(R.drawable.bin);
                                            bin.setTag("bin");
                                            Glide.with(this)
                                                    .load(R.drawable.bin)
                                                    .centerCrop()
                                                    .into(bin);
                                            frame_layout.addView(bin, 100, 100);
                                            bin.setVisibility(View.GONE);
                                            binCreated = true;
                                        }
                                        frame_layout.addView(view);
                                        System.out.println("LOADED");
                                        counter += 1;
                                    }
                                    // Add the view to the view group

                                }
                                parser.next();


                            }
                        }
                }
            }
            fis.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(LOCATION_REQUEST)
    private void checkLocationRequest() {
        String[] perms = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Please grant permission",
                    LOCATION_REQUEST, perms);
        }
    }

    /*
    Project position on ImageView to position on Bitmap draw on it
     */
    public void blankImage() {
        // Get the display metrics of the device
        int layoutWidth = frame_layout.getMeasuredWidth();
        int layoutHeight = frame_layout.getMeasuredHeight();
        // Create a white bitmap with the screen width and height
        Bitmap whiteBitmap = Bitmap.createBitmap(layoutWidth, layoutHeight, Bitmap.Config.RGB_565);
        bitmapMaster = whiteBitmap;
        canvasMaster = new Canvas(bitmapMaster);
        canvasMaster.drawColor(Color.WHITE);
        // Set the white bitmap as the image source for the ImageView
        imageResult.setImageBitmap(bitmapMaster);


    }

    private void drawStraightLineOnBitMap(ImageView iv, Bitmap bm, int startX, int startY, int endX, int endY) {
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth((float) 0.5);
        canvas.drawLine(startX, startY, endX, endY, paint);
        iv.setImageBitmap(bm);
    }

    private void drawOnProjectedBitMap(ImageView iv, Bitmap bm,
                                       float x0, float y0, float x, float y) {
        if (x < 0 || y < 0 || x > iv.getWidth() || y > iv.getHeight()) {
            //outside ImageView
            return;
        } else {

            float ratioWidth = (float) bm.getWidth() / (float) iv.getWidth();
            float ratioHeight = (float) bm.getHeight() / (float) iv.getHeight();

            canvasMaster.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    paintDraw);
            imageResult.invalidate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap tempBitmap;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_IMAGE1:
                    source = data.getData();

                    try {
                        //tempBitmap is Immutable bitmap,
                        //cannot be passed to Canvas constructor
                        tempBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source));

                        Bitmap.Config config;
                        if (tempBitmap.getConfig() != null) {
                            config = tempBitmap.getConfig();
                        } else {
                            config = Bitmap.Config.RGB_565;
                        }

                        //bitmapMaster is Mutable bitmap
                        bitmapMaster = Bitmap.createBitmap(
                                frame_layout.getWidth(),
                                frame_layout.getHeight(),
                                config);

                        canvasMaster = new Canvas(bitmapMaster);
                        canvasMaster.drawBitmap(tempBitmap, 0, 0, null);

                        imageResult.setImageBitmap(bitmapMaster);
                        canvasMaster.drawBitmap(tempBitmap, null, new RectF(0, 0, frame_layout.getWidth(), frame_layout.getHeight()), null);


                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
                case SELECT_PICTURE: {
                    Uri imageUri = data.getData();

                    System.out.println("HELLOOOOOOO");
                    System.out.println(imageUri);
                    ImageLink = imageUri;

                    //int globid = data.getIntExtra("viewId", 0);
                    System.out.println(globid);
                    ImageView addImg = findViewById(globid - 1);
                    Glide.with(MainActivity.this)
                            .load(ImageLink)
                            .into(addImg);
                }
                break;
                case CAMERA_REQUEST: {
                    Uri photoUri = data.getData();
                    System.out.println(photoUri);
                    System.out.println(globid);
                    ImageView addImg = findViewById(globid - 2);
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    addImg.setImageBitmap(photo);
                }
            }
        }
    }


    public void createFirstBitmap() {
        Bitmap bmOverlay = null;
        Canvas canvas = null;
        int pinCounter = 1;
        for (int i = 0; i < frame_layout.getChildCount(); i++) {
            View child = frame_layout.getChildAt(i);

            System.out.println(child.getClass().getSimpleName());
            if (child.getClass().getName().equals("androidx.appcompat.widget.AppCompatImageView")) {
                ImageView imageView = (ImageView) child;
                Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                bmOverlay = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), imageBitmap.getConfig());
                canvas = new Canvas(bmOverlay);
                canvas.drawBitmap(imageBitmap, new Matrix(), null);

            }
            if (child.getClass().getSimpleName().equals("EditText")) {
                System.out.println("INTHERE");
                if (child.getHeight() == 99) {

                    float x = child.getX();
                    float y = child.getY();
                    System.out.println("EDITEXT");
                    System.out.println(((EditText) child).getText().toString());
                    TextPaint tp = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
                    tp.setColor(Color.BLACK);

                    tp.setTextSize(50);
                    tp.setShadowLayer(20, 2, 2, Color.CYAN);
                    StaticLayout sl = new StaticLayout(((EditText) child).getText().toString(), tp, 300,
                            Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true);

                    canvas.save();
                    canvas.translate(x, y); //position text on the canvas
                    sl.draw(canvas);
                    canvas.restore();
                }
            }
            if (child instanceof ImageView) {
                if (child.getClass().getName().equals("android.widget.ImageView")) {
                    if (child.getWidth() == 90) {
                        TextView tv1 = new TextView(this);
                        float x = child.getX();
                        float y = child.getY();

                        TextPaint tp = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
                        tp.setColor(Color.GRAY);

                        tp.setTextSize(100);
                        tp.setShadowLayer(20, 2, 2, Color.CYAN);
                        StaticLayout sl = new StaticLayout(Integer.toString(pinCounter), tp, 300,
                                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true);

                        canvas.save();
                        canvas.translate(x, y); //position text on the canvas
                        sl.draw(canvas);
                        canvas.restore();

                        pinCounter += 1;
                    }

                }
            }
        }
        Bitmap bmOverlay2 = Bitmap.createScaledBitmap(bmOverlay, 400, 600, true);
        createPdf(MainActivity.this, "Your_PDF_Report", bmOverlay2);


        // imageviewtoNumber


    }


    public void createPdf(Context context, String fileName, Bitmap bitmap) {
        // Create a new document
        PdfDocument document = new PdfDocument();

        // Create a new page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Load the image




        // Draw the image
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(595, 842, 2).create();

        int pageCounter = 1;
// Create a paint object for the text

        for (int i = 0; i < getIncrement(); i = i + 7) {

            int id = 1000 + i;
            EditText editText = findViewById(id + 1);
            if (editText != null) {
                PdfDocument.Page page2 = document.startPage(pageInfo2);
                Canvas canvas2 = page2.getCanvas();
                Button btn = findViewById(id + 2);
                ImageView imgView = findViewById(id + 3);
                Button imgbtn = findViewById(id + 4);
                Button photobtn = findViewById(id + 5);
                EditText notes = findViewById(id + 6);
                Paint titlePaint = new Paint();
                titlePaint.setColor(Color.BLACK);
                titlePaint.setUnderlineText(true);
                titlePaint.setTextSize(22);
                Paint textPaint = new Paint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(14);
                canvas2.drawText(Integer.toString(pageCounter), 20, 50, textPaint);
                canvas2.drawText(editText.getText().toString(), 20, 100, titlePaint);


                String text = notes.getText().toString();

                int lineCount = notes.getLineCount();
                for (int line = 0; line < lineCount; line++) {
                    int lineStart = notes.getLayout().getLineStart(line);
                    int lineEnd = notes.getLayout().getLineEnd(line);
                    String lineText = text.substring(lineStart, lineEnd);
                    canvas2.drawText(lineText, 20, 150 + line * textPaint.getTextSize(), textPaint);
                }
                Bitmap imgBM = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                imgBM = Bitmap.createScaledBitmap(imgBM, 400, 400, true);
                canvas2.drawBitmap(imgBM, 150, 150 + lineCount * textPaint.getTextSize(), null);
                pageCounter += 1;
                document.finishPage(page2);
                System.out.println("PAGE COMPLETE");
            }


// Draw the text on the canvas


        }
        // Finish the page


        // Save the document
        File dir = new File(getFilesDir(), "PDF");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(getFilesDir() + "/PDF/" + fileName + ".pdf");
        boolean test = file.exists();
        System.out.println(test);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            document.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the document
        document.close();
        getFileName(".pdf", saveName);
    }
}



