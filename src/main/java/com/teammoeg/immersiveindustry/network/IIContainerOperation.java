package com.teammoeg.immersiveindustry.network;

import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.IIBaseContainer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client to server: a button press inside one of this mod's container menus.
 *
 * <p>Portable over NeoForge's payload system: the record itself carries the codec, and the
 * handler receives an {@link IPayloadContext} instead of the old {@code Supplier<Context>}.
 */
public record IIContainerOperation(int containerId, short buttonId, int state) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<IIContainerOperation> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "container_operation"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IIContainerOperation> STREAM_CODEC =
			StreamCodec.of((buf, msg) -> msg.encode(buf), IIContainerOperation::new);

	public IIContainerOperation(RegistryFriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readShort(), buf.readVarInt());
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void encode(RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(containerId);
		buffer.writeShort(buttonId);
		buffer.writeVarInt(state);
	}

	public static void handle(IIContainerOperation msg, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer player)) {
				return;
			}
			if (player.containerMenu.containerId == msg.containerId
					&& player.containerMenu instanceof IIBaseContainer container) {
				container.receiveMessage(msg.buttonId, msg.state);
			}
		});
	}

}
