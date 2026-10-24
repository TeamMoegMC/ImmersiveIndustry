package com.teammoeg.immersiveindustry.util;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.BlockHitResult;

public record IIMenuComponent<S extends IMultiblockState,C extends AbstractContainerMenu>(MultiblockContainer<S,C> cont) implements IMultiblockComponent<S> {
	

	@Override
	public ItemInteractionResult click(IMultiblockContext<S> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
		if(!isClient)
			player.openMenu(cont.provide(ctx, posInMultiblock));
		return ItemInteractionResult.SUCCESS;
	}

}
