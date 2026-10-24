/*
 * Copyright (c) 2021 TeamMoeg
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

import net.neoforged.neoforge.capabilities.Capabilities;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerBlockEntity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Mod-bus hooks that only publish capabilities.
 *
 * <p>The 1.20.1 version of this class held {@code RegistryEvent} handlers, which NeoForge
 * replaced with {@code DeferredRegister} (already wired in {@code IIContent}). What is left
 * here is the capability registration that used to happen by overriding
 * {@code BlockEntity#getCapability}.
 */
@EventBusSubscriber(modid = IIMain.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        IINetwork.register(event);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ElectrolyzerBlockEntity.registerCapabilities(event);
    }
}
