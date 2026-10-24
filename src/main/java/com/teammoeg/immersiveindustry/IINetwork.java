/*
 * Copyright (c) 2021-2024 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry;

import com.teammoeg.immersiveindustry.network.IIContainerDataSync;
import com.teammoeg.immersiveindustry.network.IIContainerOperation;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Networking.
 *
 * <p>NeoForge 1.21 removed the {@code SimpleChannel} API, and with it the manual
 * id/handler registration and the version handshake. Each message is now a
 * {@link CustomPacketPayload} carrying its own {@code StreamCodec}, and the mod registers
 * them once during {@link RegisterPayloadHandlersEvent}. The channel version string is
 * supplied to the registrar instead of being compared by hand.
 */
public class IINetwork {

    /** Bumped when the wire format changes; NeoForge rejects mismatched clients. */
    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(IIContainerOperation.TYPE, IIContainerOperation.STREAM_CODEC,
                IIContainerOperation::handle);
        registrar.playToClient(IIContainerDataSync.TYPE, IIContainerDataSync.STREAM_CODEC,
                IIContainerDataSync::handle);
    }

    public static void sendPlayer(ServerPlayer p, CustomPacketPayload message) {
        PacketDistributor.sendToPlayer(p, message);
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    /** Convenience for handlers that just need to know whether they run on the client. */
    static boolean isClient(IPayloadContext ctx) {
        return ctx.player() != null && ctx.player().level().isClientSide;
    }
}
