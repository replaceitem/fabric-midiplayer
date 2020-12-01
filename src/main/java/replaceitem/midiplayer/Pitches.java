package replaceitem.midiplayer;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class Pitches {
    private static float[] pitches = new float[25];
    public static void init() {
        for(int i = 0; i < pitches.length; i++) {
            pitches[i] = calcPitch(i);
        }
    }
    public static float getPitch(int keyIn) {
        int key;
        if(keyIn < 42 && keyIn >= 0) {
            //lower than playable
            key = (keyIn+6)%12;
        } else if(keyIn > 42+24) {
            key = ((keyIn+6)%12+12);
        } else {
            key = keyIn - 42;
        }
        if(key >= 0 && key < pitches.length) {
            return pitches[key];
        }
        return -1;
    }
    public static SoundEvent getInstrument(int keyIn) {
        return SoundEvents.BLOCK_NOTE_BLOCK_PLING;

        /*
        if(keyIn < 42 && keyIn >= 0) {
            return SoundEvents.BLOCK_NOTE_BLOCK_BASS;
        } else if(keyIn > 42+24) {
            return SoundEvents.BLOCK_NOTE_BLOCK_BELL;
        } else {
            return SoundEvents.BLOCK_NOTE_BLOCK_PLING;
        }
         */
    }
    private static float calcPitch(int keyIn) {
        double key = Float.valueOf(keyIn);
        return (float)(Math.pow(2,(-12+key)/12));
    }
}
