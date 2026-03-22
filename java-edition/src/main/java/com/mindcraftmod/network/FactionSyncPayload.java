package com.mindcraftmod.network;

import com.mindcraftmod.MindcraftMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Server → Client payload: informs the client of the local player's current faction.
 *
 * Sent on login (ServerPlayConnectionEvents.JOIN) and whenever the player runs
 * /faction join or /faction leave so the HUD stays in sync.
 */
public record FactionSyncPayload(String factionName) implements CustomPayload {

    public static final CustomPayload.Id<FactionSyncPayload> ID =
            new CustomPayload.Id<>(Identifier.of(MindcraftMod.MOD_ID, "faction_sync"));

    public static final PacketCodec<PacketByteBuf, FactionSyncPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, FactionSyncPayload::factionName,
                    FactionSyncPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
