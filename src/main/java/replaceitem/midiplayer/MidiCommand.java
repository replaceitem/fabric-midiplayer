package replaceitem.midiplayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import java.nio.file.Path;

import static net.minecraft.server.command.CommandManager.literal;

public class MidiCommand {

    private static final SuggestionProvider<ServerCommandSource> AVAILABLE_MIDI_FILES = (commandContext, suggestionsBuilder) -> {
        return CommandSource.suggestMatching(Midiplayer.listFiles(((ServerCommandSource)commandContext.getSource()).getMinecraftServer()), suggestionsBuilder);
    };


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal("midi").
            then(literal("play").
                then(CommandManager.argument("file", StringArgumentType.string()).suggests(AVAILABLE_MIDI_FILES).executes((commandContext) -> {
                    String name = StringArgumentType.getString(commandContext,"file");
                    Path file = (((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getSavePath(WorldSavePath.ROOT).resolve("midi").resolve(name+".mid"));
                    ServerPlayerEntity player = commandContext.getSource().getPlayer();
                    Midiplayer.startPlayback(player,file);
                    return 1;
                })))
            .then(literal("stop").executes((commandContext)->{
                Midiplayer.stopPlayback(commandContext.getSource().getPlayer());
                return 1;
            }))
            .then(literal("info").executes((commandContext)->{
                Midiplayer.playbackInfo(commandContext.getSource().getPlayer());
                return 1;
            }))
            .then(literal("list").executes((commandContext)->{
                Midiplayer.playbackList(commandContext.getSource().getPlayer());
                return 1;
            }))
        );
    }
}
