/*
 * Copyright (c) 2021-2024 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.teammoeg.immersiveindustry;

import com.teammoeg.immersiveindustry.network.IIContainerDataSync;
import com.teammoeg.immersiveindustry.network.IIContainerOperation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class IINetwork {

    private static SimpleChannel CHANNEL;

    public static SimpleChannel get() {
        return CHANNEL;
    }

    private static int id = 0;
    public static void register() {
        String VERSION = ModList.get().getModContainerById(IIMain.MODID).get().getModInfo().getVersion().toString();
        CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(IIMain.MODID,"network"), () -> VERSION, VERSION::equals, VERSION::equals);
        //Fundamental Message
        CHANNEL.registerMessage(id++, IIContainerOperation.class,
        	IIContainerOperation::encode, IIContainerOperation::new,
        	IIContainerOperation::handle);
        CHANNEL.registerMessage(id++, IIContainerDataSync.class,
        	IIContainerDataSync::encode, IIContainerDataSync::new,
        	IIContainerDataSync::handle);
    }

    public static void sendPlayer(ServerPlayer p, Object message) {
    	CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), message);
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }
}