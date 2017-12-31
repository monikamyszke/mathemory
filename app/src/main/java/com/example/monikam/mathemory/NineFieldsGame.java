package com.example.monikam.mathemory;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    int whichLevel;
    TextView task;
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
            new CountDownTimer(21000, 500) {
                public void onTick(long millisUntilFinished) {
                    timer.setText("pozostało: " + String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    task.setText("Wybierz " + String.valueOf(instruction));
                    timer.setText(null);
                    for (Button b : buttons) {
                        b.setEnabled(true);
                        b.setText(null);
                    }
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

                        final Dialog uncompleted = new Dialog(context);
                        uncompleted.setContentView(R.layout.uncompleted_level);
                        uncompleted.show();

                        Button repeatLevel = (Button) uncompleted.findViewById(R.id.button);
                        Button goBack = (Button) uncompleted.findViewById(R.id.button2);

                        repeatLevel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i;
                                i = new Intent(getApplicationContext(), NineFieldsGame.class);
                                i.putExtra("categoryName", categoryName);
                                i.putExtra("whichLevel", whichLevel);
                                uncompleted.dismiss();
                                startActivity(i);
                                finish();
                            }
                        });

                        goBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i;
                                i = new Intent(getApplicationContext(), LevelsMenu.class);
                                i.putExtra("categoryName", categoryName);
                                uncompleted.dismiss();
                                startActivity(i);
                                finish();
                            }
                        });
                    }

                    if (counter == 0) {

                        Game.completeLevel(category, whichLevel, stars);

                        final Dialog completed = new Dialog(context);
                        completed.setContentView(R.layout.completed_level);
                        completed.show();

                        ImageView starsResult = (ImageView) completed.findViewById(R.id.stars);
                        switch (stars) {
                            case 1: starsResult.setImageResource(R.drawable.one_yellow);
                                break;
                            case 2: starsResult.setImageResource(R.drawable.two_yellow);
                                break;
                            case 3: starsResult.setImageResource(R.drawable.three_yellow);
                                break;
                            default: starsResult.setImageResource(R.drawable.all_grey);
                                break;
                        }

                        Button nextLevel = (Button) completed.findViewById(R.id.button);
                        Button repeatLevel = (Button) completed.findViewById(R.id.button2);

                        nextLevel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i;
                                if (whichLevel == 1) {
                                    i = new Intent(getApplicationContext(), FourFieldsGame.class);
                                }
                                else if (whichLevel == 10) {
                                    i = new Intent(getApplicationContext(), MainActivity.class);
                                }
                                else {
                                    i = new Intent(getApplicationContext(), NineFieldsGame.class);
                                }
                                i.putExtra("categoryName", categoryName);
                                i.putExtra("whichLevel", whichLevel + 1);
                                completed.dismiss();
                                startActivity(i);
                                finish();
                            }
                        });

                        repeatLevel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i;
                                if (whichLevel == 1) {
                                    i = new Intent(getApplicationContext(), FourFieldsGame.class);
                                }
                                else {
                                    i = new Intent(getApplicationContext(), NineFieldsGame.class);
                                }
                                i.putExtra("categoryName", categoryName);
                                i.putExtra("whichLevel", whichLevel);
                                completed.dismiss();
                                startActivity(i);
                                finish();
                            }
                        });

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
