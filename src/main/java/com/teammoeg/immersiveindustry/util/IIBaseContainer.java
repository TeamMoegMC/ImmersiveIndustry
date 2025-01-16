/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

import com.teammoeg.immersiveindustry.IINetwork;
import com.teammoeg.immersiveindustry.network.IIContainerDataSync;
import com.teammoeg.immersiveindustry.network.IIContainerOperation;
import com.teammoeg.immersiveindustry.util.IIContainerData.SyncableDataSlot;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;

public abstract class IIBaseContainer extends AbstractContainerMenu {
	public static class Validator implements Predicate<Player> {
		Vec3 initial;
		int distsqr;
		public Validator(BlockPos initial, int distance) {
			super();
			this.initial = Vec3.atCenterOf(initial);
			this.distsqr = distance*distance;
		}
		@Override
		public boolean test(Player t) {
			return t.position().distanceToSqr(initial)<=distsqr;
		}
		public Predicate<Player> and(BooleanSupplier supp){
			return this.and(t->supp.getAsBoolean());
		}
	}
	public static class QuickMoveStackBuilder{
			private static record Range(int start,int end,boolean reverse){
				private Range(int slot) {
					this(slot,slot+1,false);
				}
				
			}
			List<Range> ranges=new ArrayList<>();
			private QuickMoveStackBuilder() {}
			
			public static QuickMoveStackBuilder begin() {
				return new QuickMoveStackBuilder();
			}
			public static QuickMoveStackBuilder first(int slot) {
				return begin().then(slot);
			}
			public static QuickMoveStackBuilder first(int beginInclusive,int endExclusive) {
				return begin().then(beginInclusive,endExclusive);
			}
			public QuickMoveStackBuilder then(int slot) {
				ranges.add(new Range(slot));
				return this;
			}
			public QuickMoveStackBuilder then(int beginInclusive,int endExclusive) {
				ranges.add(new Range(beginInclusive,endExclusive,false));
				return this;
			}
			public QuickMoveStackBuilder then(int beginInclusive,int endExclusive,boolean reversed) {
				ranges.add(new Range(beginInclusive,endExclusive,reversed));
				return this;
			}
			public Function<ItemStack,Boolean> build(IIBaseContainer t){
				return i->{
					for(Range r:ranges) {
						if(t.moveItemStackTo(i, r.start(), r.end(), r.reverse()))
							return true;
					}
					return false;
				};
				
			}
		}

	protected final int INV_START;
	protected static final int INV_SIZE = 36;
	protected static final int INV_QUICK = 27;
	protected Predicate<Player> validator;
	protected Lazy<Function<ItemStack,Boolean>> moveFunction = Lazy.of(()->defineQuickMoveStack().build(this));
	protected List<SyncableDataSlot<?>> specialDataSlots=new ArrayList<>();
	private Player player;
	public IIBaseContainer(MenuType<?> pMenuType, int pContainerId,Player player, int inv_start) {
		super(pMenuType, pContainerId);
		this.INV_START = inv_start;
		this.player=player;
	}

	protected void addPlayerInventory(Inventory inv, int dx, int dy, int quickBarY) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(inv, j + i * 9 + 9, dx + j * 18, dy + i * 18));
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(inv, i, dx + i * 18, quickBarY));
	}

	public QuickMoveStackBuilder defineQuickMoveStack() {
		return QuickMoveStackBuilder.first(0,INV_START);
	}

	public boolean quickMoveIn(ItemStack slotStack) {
		return moveFunction.get().apply(slotStack);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
	
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index < INV_START) {
				if (!this.moveItemStackTo(slotStack, INV_START, INV_SIZE + INV_START, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotStack, itemStack);
			} else if (index >= INV_START) {
				if (!quickMoveIn(slotStack)) {
					if (index < INV_QUICK + INV_START) {
						if (!this.moveItemStackTo(slotStack, INV_QUICK + INV_START, INV_SIZE + INV_START, false))
							return ItemStack.EMPTY;
					} else if (index < INV_SIZE + INV_START && !this.moveItemStackTo(slotStack, INV_START, INV_QUICK + INV_START, false))
						return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotStack, INV_START, INV_SIZE + INV_START, false)) {
				return ItemStack.EMPTY;
			}
			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (slotStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, slotStack);
		}
		return itemStack;
	}

	@Override
	public boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
		return super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
	}
	public void receiveMessage(short btnId,int state) {
		
	}
	public void sendMessage(int btnId,int state) {
		IINetwork.sendToServer(new IIContainerOperation(this.containerId,(short) btnId,state));
	}
	public void sendMessage(int btnId,boolean state) {
		IINetwork.sendToServer(new IIContainerOperation(this.containerId,(short) btnId,state?1:0));
	}
	public void sendMessage(int btnId,float state) {
		IINetwork.sendToServer(new IIContainerOperation(this.containerId,(short) btnId,Float.floatToRawIntBits(state)));
	}

	@Override
	public DataSlot addDataSlot(DataSlot pIntValue) {
		return super.addDataSlot(pIntValue);
	}

	@Override
	public void addDataSlots(ContainerData pArray) {
		super.addDataSlots(pArray);
	}
	public void addDataSlot(SyncableDataSlot<?> slot) {
		specialDataSlots.add(slot);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processPacket(IIContainerDataSync packet) {
		packet.forEach((i,o)->{
			((SyncableDataSlot)specialDataSlots.get(i)).setValue(o);
		});
	}
	
	
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		IIContainerDataSync packet=new IIContainerDataSync();
		for(int i=0;i<specialDataSlots.size();i++) {
			SyncableDataSlot<?> slot= specialDataSlots.get(i);
			if(slot.checkForUpdate()) {
				packet.add(i, slot.getConverter(), slot.getValue());
			}
		}
		if(packet.hasData()&&player!=null)
			IINetwork.sendPlayer((ServerPlayer)player, packet);
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		if(validator==null)
			return true;
		return validator.test(pPlayer);
	}

	
}