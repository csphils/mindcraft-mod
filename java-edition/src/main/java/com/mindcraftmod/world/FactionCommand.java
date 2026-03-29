package com.mindcraftmod.world;

import com.mindcraftmod.network.FactionSyncPayload;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

/**
 * Registers the /faction command tree.
 *
 * Subcommands:
 *   /faction join allies       — joins the Allied faction
 *   /faction join central      — joins the Central Powers faction
 *   /faction leave             — returns to NONE
 *   /faction info              — shows your current faction
 *   /faction list              — shows online player count per faction
 */
public class FactionCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(FactionCommand::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         net.minecraft.command.CommandRegistryAccess registryAccess,
                                         net.minecraft.server.command.CommandManager.RegistrationEnvironment env) {
        dispatcher.register(
            CommandManager.literal("faction")
                .then(CommandManager.literal("join")
                    .then(CommandManager.argument("side", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("allies");
                            builder.suggest("central");
                            return builder.buildFuture();
                        })
                        .executes(ctx -> executeJoin(ctx.getSource(),
                                StringArgumentType.getString(ctx, "side")))))
                .then(CommandManager.literal("leave")
                        .executes(ctx -> executeLeave(ctx.getSource())))
                .then(CommandManager.literal("info")
                        .executes(ctx -> executeInfo(ctx.getSource())))
                .then(CommandManager.literal("list")
                        .executes(ctx -> executeList(ctx.getSource())))
        );
    }

    private static int executeJoin(ServerCommandSource source, String side) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        FactionManager factions = FactionManager.get(source.getServer());
        FactionManager.Faction chosen = switch (side.toLowerCase()) {
            case "allies"  -> FactionManager.Faction.ALLIES;
            case "central" -> FactionManager.Faction.CENTRAL_POWERS;
            default -> null;
        };

        if (chosen == null) {
            player.sendMessage(Text.literal("Unknown faction. Use 'allies' or 'central'."), false);
            return 0;
        }

        factions.setFaction(player.getUuid(), chosen);
        ServerPlayNetworking.send(player, new FactionSyncPayload(chosen.name()));
        source.getServer().getPlayerManager().broadcast(
                Text.literal(player.getName().getString()
                        + " has joined the " + chosen.displayName() + "!"), false);
        return 1;
    }

    private static int executeLeave(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        FactionManager factions = FactionManager.get(source.getServer());
        factions.setFaction(player.getUuid(), FactionManager.Faction.NONE);
        ServerPlayNetworking.send(player, new FactionSyncPayload("NONE"));
        player.sendMessage(Text.literal("You have left your faction."), false);
        return 1;
    }

    private static int executeInfo(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        FactionManager factions = FactionManager.get(source.getServer());
        FactionManager.Faction faction = factions.getFaction(player.getUuid());
        player.sendMessage(Text.literal("Your faction: " + faction.displayName()), false);
        return 1;
    }

    private static int executeList(ServerCommandSource source) {
        FactionManager factions = FactionManager.get(source.getServer());
        Map<FactionManager.Faction, Long> counts = factions.getOnlineCounts(source.getServer());

        long allies  = counts.getOrDefault(FactionManager.Faction.ALLIES, 0L);
        long central = counts.getOrDefault(FactionManager.Faction.CENTRAL_POWERS, 0L);
        long none    = counts.getOrDefault(FactionManager.Faction.NONE, 0L);

        source.sendFeedback(
                () -> Text.literal(String.format(
                        "Faction roster — Allies: %d | Central Powers: %d | No faction: %d",
                        allies, central, none)), false);
        return 1;
    }
}
