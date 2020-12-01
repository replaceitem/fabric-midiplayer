package replaceitem.midiplayer;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import java.nio.file.Path;

public class Playback {
    private MidiFile midi;
    int tick;
    ServerPlayerEntity player;
    public Playback(ServerPlayerEntity player,Path path) {
        this.player = player;
        tick = 0;
        midi = new MidiFile(path);
        //midi.notes.forEach((n) -> {
        //});
    }
    public boolean onTick() {
        midi.getNotesInTick(tick).forEach((note)-> {
            player.playSound(note.instrument, SoundCategory.MASTER,1, note.pitch);
        });
        tick++;
        return (tick >= midi.duration);
    }

    public String getName() {
        return midi.name.replaceFirst("\\.mid","");
    }

    public String getTime() {
        int s = tick/20;
        int sec = s%60;
        int min = s/60;
        return (min + ":" + String.format("%02d",sec));
    }

    public String getDuration() {
        int s = midi.duration/20;
        int sec = s%60;
        int min = s/60;
        return (min + ":" + String.format("%02d",sec));
    }
}
