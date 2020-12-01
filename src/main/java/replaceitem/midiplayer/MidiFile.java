package replaceitem.midiplayer;

import net.minecraft.sound.SoundEvent;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MidiFile {
    List<MidiNote> notes;
    String name;
    int duration; //in ticks
    public MidiFile(Path path) {
        name = path.getFileName().toString();
        notes = new ArrayList<>();
        File midiFile = path.toFile();
        try {
            Sequence seq = MidiSystem.getSequence(midiFile);
            Track[] tracks = seq.getTracks();
            duration = (int) (seq.getMicrosecondLength()/(1000*50));
            double tl = Double.valueOf(seq.getTickLength());
            double usl = Double.valueOf(seq.getMicrosecondLength());
            duration = (int) usl/(1000*50);
            double uspt = usl/tl;
            for (Track track : tracks) {
                for (int j = 0; j < track.size(); j++) {
                    if (track.get(j).getMessage() instanceof ShortMessage) {
                        ShortMessage m = (ShortMessage) track.get(j).getMessage();
                        int cmd = m.getCommand();
                        if (cmd == ShortMessage.NOTE_ON) {
                            long tick = track.get(j).getTick();
                            double us = uspt * tick;
                            int mctick = (int) Math.floor(us / (1000 * 50));
                            float pitch = Pitches.getPitch(m.getData1());
                            if (pitch != -1) {
                                notes.add(new MidiNote(pitch, mctick,Pitches.getInstrument(m.getData1())));

                            }
                        }
                    }
                }
            }
        }

        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MidiNote> getNotesInTick(int tick) {
        ArrayList<MidiNote> play = new ArrayList<>();
        notes.forEach((n)->{
            if(n.tick == tick) {
                play.add(n);
            };
        });
        return play;
    }

    class MidiNote {
        public float pitch;
        public int tick;
        public SoundEvent instrument;
        public MidiNote(float p, int t, SoundEvent i) {
            pitch = p;
            tick = t;
            instrument = i;
        }
    }
}
