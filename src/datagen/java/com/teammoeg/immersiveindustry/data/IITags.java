package com.teammoeg.immersiveindustry.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IITags extends BlockTagsProvider {




	public IITags(PackOutput output, CompletableFuture<Provider> lookupProvider, String modId,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(Provider pProvider) {
		for(MultiblockRegistration<?> i:IIMultiblocks.MULTIBLOCKS) {
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(i.block().get());
			
		}
			
	}

}
