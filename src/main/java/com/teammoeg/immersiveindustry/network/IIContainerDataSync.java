package com.teammoeg.immersiveindustry.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.IIContainerData.OtherDataSlotEncoder;
import com.teammoeg.immersiveindustry.util.IIUtil;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record IIContainerDataSync(List<ContainerDataPair> data) {

	private static record ContainerDataPair(int slotIndex,OtherDataSlotEncoder<?> conv,Object data){
		public ContainerDataPair(FriendlyByteBuf buf,int slotIndex,OtherDataSlotEncoder<?> conv) {
			this(slotIndex,conv,conv.read(buf));
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void write(FriendlyByteBuf buffer) {
			buffer.writeVarInt(slotIndex);
			IIContainerData.encoders.write(buffer, conv);
			((OtherDataSlotEncoder)conv).write(buffer, data);
		}
	}
	
	public IIContainerDataSync() {
		this(new ArrayList<>());
	}
	public void add(int slotIndex,OtherDataSlotEncoder<?> conv,Object data) {
		this.data.add(new ContainerDataPair(slotIndex,conv,data));
	}
	
	public void forEach(BiConsumer<Integer,Object> t) {
		data.forEach(o->t.accept(o.slotIndex, o.data));
	}
	public boolean hasData() {
		return !this.data.isEmpty();
	}

	public IIContainerDataSync(FriendlyByteBuf buf) {
		this(IIUtil.readList(buf, t->new ContainerDataPair(buf,buf.readVarInt(),IIContainerData.encoders.read(buf))));
	}

	public void encode(FriendlyByteBuf buffer) {
		IIUtil.writeList(buffer, data, ContainerDataPair::write);
	}
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(()->{
			if(ImmersiveEngineering.proxy.getClientPlayer().containerMenu instanceof IIBaseContainer container) {
				container.processPacket(this);
				context.get().setPacketHandled(true);
			}
		});
	}

}
