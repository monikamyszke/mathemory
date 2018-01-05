package com.example.monikam.mathemory;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NineFieldsGame extends AppCompatActivity {

    int fieldsNumber = 9;
    int counter;
    int counterIncorrect;
    String instruction;
    String[] sGenerated;
    List<Button> buttons = new ArrayList<>();
    CategoryClass category;
    Vibrator vib;
    MediaPlayer sound;
    String categoryName;
    int whichLevel;    TextView task;
    TextView timer;
    int stars = 3;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/righteous.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_nine_fields_game);

        categoryName = getIntent().getStringExtra("categoryName");
        whichLevel = getIntent().getIntExtra("whichLevel", 0);

        category = Game.getCategory(categoryName);

        TextView level = (TextView) findViewById(R.id.level);
        level.setText("Poziom: " + String.valueOf(whichLevel));

        instruction = category.getInstruction(whichLevel);
        task = (TextView) findViewById(R.id.task);

        if (whichLevel != 1) {
            task.setText("Zapamiętaj " + String.valueOf(instruction));
        }
        else {
            task.setText("Wybierz " + String.valueOf(instruction));
        }

        timer = (TextView) findViewById(R.id.timer);

        sGenerated = category.generateNumbers(fieldsNumber, whichLevel);
        counter = category.getCounter();
        counterIncorrect = 0;

        for (int i = 1; i < (fieldsNumber + 1); i++) {
            int id = getResources().getIdentifier("f"+i, "id", getPackageName());
            Button b = (Button) findViewById(id);
            buttons.add(b);
            if (categoryName.equals("Ułamki właściwe i niewłaściwe")) {
                b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            }
            b.setText(sGenerated[i-1]);
            b.setEnabled(false);
        }
    }

    protected void onResume() {
        super.onResume();

        if (whichLevel != 1) {
            new CountDownTimer(17000, 500) {
                public void onTick(long millisUntilFinished) {
                    timer.setText("pozostało: " + String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    task.setText("Wybierz " + String.valueOf(instruction));
                    timer.setText(null);
                    for (Button b : buttons) {
                        b.setText(null);
                        if (whichLevel != 10) {
                            b.setEnabled(true);
                        }
                    }

                    if (whichLevel == 10) {

                        Button b1, b2;
                        int b1Position, b2Position;
                        b1Position = new Random().nextInt(buttons.size());
                        b1 = buttons.get(b1Position);
                        do {
                            b2Position = new Random().nextInt(buttons.size());
                        }
                        while (b1Position == b2Position);
                        b2 = buttons.get(b2Position);

                        b1.animate().translationXBy(b2.getX() - b1.getX()).translationYBy(b2.getY() - b1.getY()).setDuration(1000).start();
                        b2.animate().translationXBy(b1.getX() - b2.getX()).translationYBy(b1.getY() - b2.getY()).setDuration(1000).start();

                    }

                    new CountDownTimer(1000,10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            for (Button b : buttons) {
                                if (whichLevel == 10) {
                                    b.setEnabled(true);
                                }
                            }
                        }
                    }.start();
                }

            }.start();
        }
        else {
            for (Button b : buttons) {
                b.setEnabled(true);
            }
        }

        for (final Button b : buttons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean correct;
                    correct = category.check(buttons.indexOf(b));

                    if (correct) {
                        counter --;
                        b.setText(sGenerated[buttons.indexOf(b)]);
                        sound = MediaPlayer.create(getApplicationContext(), R.raw.correct_answer);
                        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                               if(mp != null) {
                                    mp.release();
                                    sound = null;
                                }
                            }
                        });
                        sound.start();
                        b.setBackgroundResource(R.drawable.check_mark);
                        b.setEnabled(false);
                    }
                    else {
                        counterIncorrect ++;
                        stars --;
                        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(250);
                    }

                    if (counterIncorrect == 3) {

                        final DialogWindow uncompleted = new DialogWindow(context, whichLevel, categoryName);
                        uncompleted.show();
                    }

                    if (counter == 0) {

                        Game.completeLevel(category, whichLevel, stars);
                        final DialogWindow completed = new DialogWindow(context, whichLevel, categoryName, stars);
                        completed.show();
                    }

                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
