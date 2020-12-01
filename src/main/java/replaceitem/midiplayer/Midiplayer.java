package replaceitem.midiplayer;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Midiplayer implements ModInitializer {
    static HashMap<ServerPlayerEntity,Playback> playbacks = new HashMap<>();
    @Override
    public void onInitialize() {
        Pitches.init();
    }
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        MidiCommand.register(dispatcher);
    }

    public static void startPlayback(ServerPlayerEntity player, Path path) {
        playbacks.put(player,new Playback(player,path));
    }

    public static void stopPlayback(ServerPlayerEntity player) {
        playbacks.remove(player);
    }

    public static void playbackInfo(ServerPlayerEntity player) {
        if(playbacks.containsKey(player)) {
            String name = playbacks.get(player).getName();
            LiteralText text = (LiteralText) (new LiteralText("Now playing: " + name).formatted(Formatting.BLUE));
            text.append(new LiteralText("\n" + playbacks.get(player).getTime() + " / " + playbacks.get(player).getDuration()).formatted(Formatting.YELLOW));
            player.sendMessage(text, false);
        } else {
            player.sendMessage(new LiteralText("There is no song playing").formatted(Formatting.GRAY),false);
        }
    }

    public static void playbackList(ServerPlayerEntity player) {
        LiteralText text = (LiteralText)(new LiteralText("Currently playing files:").formatted(Formatting.AQUA));
        playbacks.keySet().forEach((k)->{
            text.append(new LiteralText("\n"));
            text.append(((LiteralText)k.getDisplayName()));
            text.append(new LiteralText(" " + playbacks.get(k).getName()).formatted(Formatting.GOLD));
        });
        player.sendMessage(text,false);
    }

    public static void onTick() {
        ArrayList<ServerPlayerEntity> remove = new ArrayList<>();
        playbacks.keySet().forEach((key)->{
            if(playbacks.get(key).onTick()) {
                remove.add(key);
            }
        });
        remove.forEach((key) -> {
            playbacks.remove(key);
        });

    }

    public static List<String> listFiles(MinecraftServer server) {
        List<String> moduleNames = new ArrayList<>();
        File folder = server.getSavePath(WorldSavePath.ROOT).resolve("midi").toFile();
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null)  return moduleNames;
        for (File file : listOfFiles) {
            if (file.getName().endsWith(".mid")) {
                moduleNames.add(file.getName().replaceFirst("\\.mid",""));
            }
        }
        return moduleNames;
    }

}
