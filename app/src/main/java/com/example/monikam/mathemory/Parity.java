package com.example.monikam.mathemory;

import com.example.monikam.mathemory.CategoryClass;

import java.util.Random;

/**
 * Created by MonikaM on 2017-11-24.
 */

class Parity extends CategoryClass {

    private int[] generated;

    @Override
    public String getInstruction(int curr_level) {

        String instruction;

        if (curr_level == 3 || curr_level == 6 || curr_level ==9){
            instruction = "Wybierz liczby nieparzyste:";
        }
        else {
            instruction = "Wybierz liczby parzyste:";
        }

        return instruction;
    }

    @Override
    public String[] generateNumbers(int fields_num, int curr_level) {

        generated = new int[fields_num];
        String[] sGenerated = new String[fields_num];
        Random r = new Random();

        for(int i = 0; i < fields_num; i++){
            generated[i] = r.nextInt(10);
            sGenerated[i] = String.valueOf(generated[i]);
        }

        return sGenerated;
    }

    @Override
    public boolean check(int sel_field) {

        return false;
    }
}
