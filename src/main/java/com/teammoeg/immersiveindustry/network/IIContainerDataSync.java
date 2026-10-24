package com.teammoeg.immersiveindustry.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.IIUtil;
import com.teammoeg.immersiveindustry.util.IIContainerData.OtherDataSlotEncoder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server to client: refreshed values for the synced fields of an open container.
 *
 * <p>The pair type still resolves the encoder per slot, so the payload stays self-describing.
 * Only the transport changed: a {@link StreamCodec} built from the existing
 * {@code encode}/{@code read} pair, and an {@link IPayloadContext} handler.
 */
public class IIContainerDataSync implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<IIContainerDataSync> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "container_data_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IIContainerDataSync> STREAM_CODEC =
			StreamCodec.of((buf, msg) -> msg.encode(buf), IIContainerDataSync::new);

	private record ContainerDataPair(int slotIndex, OtherDataSlotEncoder<?> conv, Object data) {

		public ContainerDataPair(FriendlyByteBuf buf, int slotIndex, OtherDataSlotEncoder<?> conv) {
			this(slotIndex, conv, conv.read(buf));
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void write(FriendlyByteBuf buffer) {
			buffer.writeVarInt(slotIndex);
			IIContainerData.encoders.write(buffer, conv);
			((OtherDataSlotEncoder) conv).write(buffer, data);
		}
	}

	private final List<ContainerDataPair> data;

	public IIContainerDataSync() {
		this(new ArrayList<>());
	}

	private IIContainerDataSync(List<ContainerDataPair> data) {
		this.data = data;
	}

	public void add(int slotIndex, OtherDataSlotEncoder<?> conv, Object data) {
		this.data.add(new ContainerDataPair(slotIndex, conv, data));
	}

	public void forEach(BiConsumer<Integer, Object> t) {
		data.forEach(o -> t.accept(o.slotIndex(), o.data()));
	}

	public boolean hasData() {
		return !this.data.isEmpty();
	}

	public IIContainerDataSync(RegistryFriendlyByteBuf buf) {
		this(IIUtil.readList(buf, t -> new ContainerDataPair(buf, buf.readVarInt(), IIContainerData.encoders.read(buf))));
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void encode(RegistryFriendlyByteBuf buffer) {
		IIUtil.writeList(buffer, data, ContainerDataPair::write);
	}

	public static void handle(IIContainerDataSync msg, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (Minecraft.getInstance().player != null
					&& Minecraft.getInstance().player.containerMenu instanceof IIBaseContainer container) {
				container.processPacket(msg);
			}
		});
	}

}
