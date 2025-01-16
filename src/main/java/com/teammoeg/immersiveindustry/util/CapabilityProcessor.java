package com.teammoeg.immersiveindustry.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class CapabilityProcessor {
	private static interface CapabilityQuery<T>{
		StoredCapability<T> getCapabiltiy(CapabilityPosition pos);
		CapabilityQuery<T> append(CapabilityPosition pos,T capability);
		CapabilityQuery<T> append(CapabilityQuery<T> other);
		Stream<Pair<CapabilityPosition,StoredCapability<T>>> getAll();
		
	}
	private static record SingleCapabilityQuery<T>(CapabilityPosition pos,StoredCapability<T> cap) implements CapabilityQuery<T>{
		
		public SingleCapabilityQuery(CapabilityPosition pos, T obj) {
			this(pos,new StoredCapability<T>(obj));
		}

		@Override
		public StoredCapability<T> getCapabiltiy( CapabilityPosition pos) {
			if(this.pos.equals(pos))
				return cap;
			return null;
		}

		@Override
		public CapabilityQuery<T> append(CapabilityPosition pos, T capability) {
			MultipleCapabilityQuery<T> cap=new MultipleCapabilityQuery<>();
			cap.cap.put(this.pos,this.cap);
			return cap.append(pos, capability);
		}

		@Override
		public Stream<Pair<CapabilityPosition, StoredCapability<T>>> getAll() {
			return Stream.of(Pair.of(pos, cap));
		}
		@Override
		public CapabilityQuery<T> append(CapabilityQuery<T> other) {
			MultipleCapabilityQuery<T> rs= new MultipleCapabilityQuery<T>();
			other.getAll().forEach(t->rs.cap.put(t.getFirst(), t.getSecond()));
			return rs;
		};
		
	}
	private static class MultipleCapabilityQuery<T> implements CapabilityQuery<T>{
		Map<CapabilityPosition,StoredCapability<T>> cap=new HashMap<>();
		@Override
		public StoredCapability<T> getCapabiltiy( CapabilityPosition pos) {
			return cap.get(pos);
		}

		@Override
		public CapabilityQuery<T> append(CapabilityPosition pos, T capability) {
			cap.put(pos,new StoredCapability<T>( capability));
			return this;
		}

		@Override
		public Stream<Pair<CapabilityPosition, StoredCapability<T>>> getAll() {
			return cap.entrySet().stream().map(t->Pair.of(t.getKey(), t.getValue()));
		}
		@Override
		public CapabilityQuery<T> append(CapabilityQuery<T> other) {
			other.getAll().forEach(t->this.cap.put(t.getFirst(), t.getSecond()));
			return this;
		};
		
	}
	Map<Capability<?>,CapabilityQuery<?>> capabilities=new IdentityHashMap<>();
	public CapabilityProcessor() {
		
	}
	public record CapabilityBuilder<T>(CapabilityProcessor proc,Capability<T> cap){
		public CapabilityBuilder<T> addCapability(CapabilityPosition pos,T obj) {
			proc.addCapability(cap, pos, obj);
			return this;
		}
		public CapabilityBuilder<T> addCapability(CapabilityFacing pos,T obj) {
			proc.addCapability(cap, pos, obj);
			return this;
		}
	}
	public <T> CapabilityProcessor addCapability(Capability<T> cap,CapabilityPosition pos,T obj) {
		capabilities.merge(cap, new SingleCapabilityQuery<T>(pos,obj), (a,b)->a.append((CapabilityQuery)b));
		return this;
	}
	public <T> CapabilityProcessor addCapability(Capability<T> cap,CapabilityFacing pos,T obj) {
		capabilities.merge(cap, new SingleCapabilityQuery<T>(pos.capPos(),obj), (a,b)->a.append((CapabilityQuery)b));
		return this;
	}
	public <T> CapabilityBuilder<T> addCapabilities(Capability<T> cap){
		return new CapabilityBuilder<T>(this,cap);
	}
	public CapabilityBuilder<IItemHandler> itemHandler(){
		return addCapabilities(ForgeCapabilities.ITEM_HANDLER);
	}
	public CapabilityBuilder<IFluidHandler> fluidHandler(){
		return addCapabilities(ForgeCapabilities.FLUID_HANDLER);
	}
	public CapabilityBuilder<IEnergyStorage> energy(){
		return addCapabilities(ForgeCapabilities.ENERGY);
	}
	public <T> LazyOptional<T> getCapability(Capability<T> cap,CapabilityPosition pos,IMultiblockContext<?> ctx) {
		CapabilityQuery<?> qry=capabilities.get(cap);
		if(qry!=null) {
			StoredCapability<?> storedCap=qry.getCapabiltiy(pos);
			if(storedCap!=null)
				return storedCap.cast(ctx);
		}
		return LazyOptional.empty();
	}
}
