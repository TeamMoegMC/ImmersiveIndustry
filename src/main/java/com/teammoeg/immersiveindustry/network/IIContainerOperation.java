package com.teammoeg.immersiveindustry.network;

import java.util.function.Supplier;

import com.teammoeg.immersiveindustry.util.IIBaseContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record IIContainerOperation(int containerId, short buttonId, int state) {

	public IIContainerOperation(FriendlyByteBuf buf) {
		this(buf.readVarInt(),buf.readShort(),buf.readVarInt());
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(containerId);
		buffer.writeShort(buttonId);
		buffer.writeVarInt(state);
	}

	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(()->{
			Context ctx=context.get();
			ServerPlayer player=ctx.getSender();
			if(player.containerMenu.containerId==containerId&&player.containerMenu instanceof IIBaseContainer container) {
				container.receiveMessage(buttonId, state);
			}
		});
	}

}
