package com.teammoeg.immersiveindustry.content.crucible;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.IIUtil;
import com.teammoeg.immersiveindustry.util.RecipeHandler;
import com.teammoeg.immersiveindustry.util.RecipeProcessResult;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.ItemHandlerHelper;

public class CrucibleLogic implements IClientTickableComponent<CrucibleState>, IMultiblockLogic<CrucibleState>, IServerTickableComponent<CrucibleState> {

	public static final int MAX_TEMP = 1700;
	public static final int TEMP_ABOVE_WHICH_REQUIRES_FANS = 1000;
	public static final BlockPos FRONT_PREHEATER_REL_POS = new BlockPos(1, 0, -1);
	public static final BlockPos BACK_PREHEATER_REL_POS = new BlockPos(1, 0, 3);
	public static final int DEFAULT_ACTIVE_FAN_SPEED = 64;

	public CrucibleLogic() {

	}

	static final CapabilityFacing fluidout = new CapabilityFacing(2, 1, 2, RelativeBlockFace.UP);
	public void ensureBurntime(IMultiblockContext<CrucibleState> context) {
		CrucibleState state = context.getState();
		ChangeDetectedItemHandler inventory = state.inventory;
		if (state.burnTime <= 0) {
			if (!inventory.getStackInSlot(4).isEmpty()) {
				int totalTime = CrucibleRecipe.getFuelTime(context.getLevel().getRawLevel(), inventory.getStackInSlot(4));
				if (totalTime > 0) {
					// burnTime = IIConfig.COMMON.coke.get();
					state.burnTime = totalTime;
					state.burnTimeMax = totalTime;
					ItemStack origin = inventory.getStackInSlot(4);
					if (origin.getCount() == 1)
						inventory.setStackInSlotNoChange(4, ItemStack.EMPTY);
					else
						inventory.setStackInSlotNoChange(4, origin.copyWithCount(origin.getCount() - 1));
					context.markMasterDirty();
				}
			}
		}
	}
	@Override
	public void tickServer(IMultiblockContext<CrucibleState> context) {
		// CrucibleRecipe recipe = getRecipe();
		tryOutput(context);
		/*
		 * if (burnTime > 0){ if
		 * (getFromPreheater(BlastFurnacePreheaterTileEntity::doSpeedup, 0) > 0) {
		 * if(temperature < 1680) temperature++;//with preheater, boost }else if
		 * (temperature < 1100) temperature++;//without preheater, up to 1100 else
		 * if(temperature >1100) temperature--;//without preheater, reduce } else if
		 * (temperature > 0) { temperature--; }
		 */
		CrucibleState state = context.getState();
		ChangeDetectedItemHandler inventory = state.inventory;
		RandomSource rs = context.getLevel().getRawLevel().random;
		boolean lastIsActive = state.active;
		state.active = false;

		// new heating mechanism
		ensureBurntime(context);
		if (state.burnTime > 0) {
			int fanspeed = getFanSpeed(context);
			double coefficient = fanspeed < DEFAULT_ACTIVE_FAN_SPEED ? 0 : Math.sqrt(fanspeed) / 8;
			if (coefficient == 0) {// Speed < 64, no boost
				if (state.temperature > TEMP_ABOVE_WHICH_REQUIRES_FANS) {
					state.temperature--;
				} else {
					state.active = true;
					state.burnTime--;
					state.temperature++;
				}
			} else {// Speed >= 64, higher temperature and faster speed up
				if (state.temperature < MAX_TEMP) {
					state.active = true;
					state.burnTime--;
					state.temperature++;
					if (Math.random() + Math.random() < coefficient) {
						state.temperature++;
					}
					if (Math.random() + Math.random() + Math.random() < coefficient) {
						state.temperature++;
					}
				}
			}
			context.markMasterDirty();
		}
		if (state.temperature > 0) {
			state.temperature -= IIUtil.randomValue(rs, IIConfig.COMMON.crucibleCoolDown.get());
			context.markMasterDirty();
		}
		RecipeHandler<CrucibleRecipe> handler = state.recipe;
		if (handler.shouldTestRecipe()) {
			RecipeProcessResult<CrucibleRecipe> recipeResult = CrucibleRecipe.findRecipe(context.getLevel().getRawLevel(), inventory);
			handler.setRecipe(recipeResult);
			context.markMasterDirty();
		}
		if (handler.shouldTickProcess()) {
			ensureBurntime(context);
			CrucibleRecipe rcp = CrucibleRecipe.recipeList.getById(context.getLevel().getRawLevel(), handler.getLastRecipe());
			if (state.temperature>=rcp.temperature&&state.burnTime > 0)
				if (handler.tickProcess(1)) {
					state.burnTime--;
					state.active = true;
				}

			if (handler.isRecipeFinished()) {
				RecipeProcessResult<CrucibleRecipe> recipeResult = handler.getRecipeResultCache();
				if (recipeResult != null) {
					CrucibleRecipe recipe = recipeResult.recipe();
					if (inventory.insertItem(5, recipe.output.get(), true).isEmpty() && state.tank.fill(recipe.output_fluid, FluidAction.SIMULATE) >= recipe.output_fluid.getAmount()) {
						inventory.insertItem(5, recipe.output.get(), false);
						state.tank.fill(recipe.output_fluid, FluidAction.EXECUTE);
						handler.endProcess();
						recipeResult.runOperations(inventory);
					}

				} else {
					handler.endProcess();
				}
			}
			context.markMasterDirty();
		}
		if (state.active != lastIsActive){
			NonMirrorableWithActiveBlock.setActive(context.getLevel(), IIMultiblocks.Multiblock.CRUCIBLE.get(), state.active);
		}

	}

	@Override
	public CrucibleState createInitialState(IInitialMultiblockContext<CrucibleState> capabilitySource) {
		return new CrucibleState(capabilitySource);
	}

	static VoxelShape getShape(BlockPos pos) {
		if(pos.getX()==1&&pos.getZ()==1) {
			return Shapes.block();
		}
		double minX=0;
		double minZ=0;
		double maxX=1;
		double maxZ=1;
		double minSideX=0;
		double minSideZ=0;
		double maxSideX=1;
		double maxSideZ=1;
		double minorNum=.375;
		double bottomY=0;
		double sideGrow=.1875;
		switch(pos.getY()){
		case 2:minorNum=.125;break;
		case 0:bottomY=.125;sideGrow=.125;
		case 1:minorNum=.25;break;
		}
			
		if(pos.getX()==2) {
			maxX=1-minorNum;
			maxSideX=maxX+sideGrow;
		}else
		if(pos.getX()==0) {
			minX=minorNum;
			minSideX=minX-sideGrow;
		}
		if(pos.getZ()==0) {
			minZ=minorNum;
			minSideZ=minZ-sideGrow;
		}else
		if(pos.getZ()==2) {
			maxZ=1-minorNum;
			maxSideZ=maxZ+sideGrow;
		}
		
		VoxelShape pillar=Shapes.empty();
		double pillarY=1;
		if(pos.getY()==3) {
			pillarY=.0625;
		}

		
		
		if(pos.getX()==0) {
			if(pos.getZ()==0) {
				pillar=Shapes.box(0, 0, 0, 1-.375, pillarY, 1-.375);
			}else
			if(pos.getZ()==2) {
				pillar=Shapes.box(0, 0, .375, 1-.375, pillarY, 1);
			}
		}
		if(pos.getX()==2) {
			if(pos.getZ()==0) {
				pillar=Shapes.box(.375, 0, 0, 1, pillarY, 1-.375);
			}else
			if(pos.getZ()==2) {
				pillar=Shapes.box(.375, 0, .375, 1, pillarY, 1);
			}
		}

		VoxelShape base= Shapes.or(pillar,Shapes.box(minX, bottomY, minZ, maxX, 1, maxZ));
		if(pos.getY()==1) {
			base=Shapes.or(Shapes.box(minSideX, .5, minSideZ, maxSideX, 1, maxSideZ),base);
		}
		if(pos.getY()==0){
			base=Shapes.or(Shapes.box(minSideX, .125, minSideZ, maxSideX, .5, maxSideZ), base);
		}
		if(pos.getX()==1&&pos.getY()==0&&pos.getZ()==0)
			base=Shapes.or(Shapes.box(.0625, .125, .0625, .9375, .875, .9375), base);
		return base;
	}
	static final Function<BlockPos, VoxelShape> shapeCache=Util.memoize(CrucibleLogic::getShape);
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
	
		return shapeCache;
	}

	@Override
	public void tickClient(IMultiblockContext<CrucibleState> context) {
		RandomSource random = context.getLevel().getRawLevel().random;
		Level world = context.getLevel().getRawLevel();
		BlockPos pos = context.getLevel().toAbsolute(IIMultiblocks.CRUCIBLE.masterPosInMB());
		if(context.getState().active) {
			if (random.nextFloat() < 0.1F) {
				for (int i = 0; i < random.nextInt(2) + 2; ++i) {
					world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, pos.getX() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1),
						pos.getY() + random.nextDouble() + random.nextDouble(), pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1), 0.0D, 0.05D, 0.0D);
					world.addAlwaysVisibleParticle(ParticleTypes.SMOKE, pos.getX() + 0.25D + random.nextDouble() / 2.0D * (random.nextBoolean() ? 1 : -1), pos.getY() + 0.4D,
						pos.getZ() + 0.25D + random.nextDouble() / 2.0D * (random.nextBoolean() ? 1 : -1), 0.002D, 0.01D, 0.0D);
				}
			}
		}
		CrucibleState state = context.getState();
		if(!state.active)
			return;
		final IMultiblockLevel level = context.getLevel();
		if(!state.isSoundPlaying.getAsBoolean())
		{
			final Vec3 soundPos = level.toAbsolute(new Vec3(1.5, 1.5, 1.5));
			state.isSoundPlaying = MultiblockSound.startSound(
					() -> state.active, context.isValid(), soundPos, IESounds.arcFurnace, 0.075f
			);
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<CrucibleState> ctx, CapabilityPosition position, Capability<T> cap) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {

			if (position.posInMultiblock().getY() <= 1)
				return ctx.getState().fuelHandler.cast(ctx);
			else if (position.side() == RelativeBlockFace.UP)
				return ctx.getState().inputHandler.cast(ctx);
			else
				return ctx.getState().outputHandler.cast(ctx);
		}
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			if (fluidout.isCapabilityPosition(position))
				return ctx.getState().outputFluidHandler.cast(ctx);
		}
		return LazyOptional.empty();
	}

	@Override
	public void onEntityCollision(IMultiblockContext<CrucibleState> ctx, BlockPos posInMultiblock, Entity collided) {
		if (posInMultiblock.getY() >= 2) {
			if (!(collided instanceof ItemEntity)) {
				if (collided instanceof LivingEntity col && ctx.getState().active)
					col.hurt(ctx.getLevel().getRawLevel().damageSources().hotFloor(), 4);
				return;
			}
			ItemEntity itemEntity = (ItemEntity) collided;
			ItemStack insertItem = ItemHandlerHelper.insertItem(ctx.getState().inventory, itemEntity.getItem().copy(), false);
			if (insertItem.isEmpty()) {
				itemEntity.remove(RemovalReason.DISCARDED);
				return;
			}
			itemEntity.setItem(insertItem);

		}
	}

	public boolean tryOutput(IMultiblockContext<CrucibleState> context) {
		CrucibleState state = context.getState();
		boolean update = FluidUtils.multiblockFluidOutput(state.outputFluidCap, state.tank, 0, 0, null);
		return update;
	}

	public int getFromPreheater(IMultiblockContext<CrucibleState> context, Function<BlastFurnacePreheaterBlockEntity, Integer> getter, int orElse) {
		Optional<BlastFurnacePreheaterBlockEntity> preheater1 = Optional.empty();
		Optional<BlastFurnacePreheaterBlockEntity> preheater2 = Optional.empty();
		BlockEntity te1 = context.getLevel().getBlockEntity(FRONT_PREHEATER_REL_POS);
		BlockEntity te2 = context.getLevel().getBlockEntity(BACK_PREHEATER_REL_POS);
		if (te1 instanceof BlastFurnacePreheaterBlockEntity && ((BlastFurnacePreheaterBlockEntity) te1).getFacing().equals(context.getLevel().getOrientation().front().getOpposite()))
			preheater1 = Optional.of((BlastFurnacePreheaterBlockEntity) te1);
		if (te2 instanceof BlastFurnacePreheaterBlockEntity && ((BlastFurnacePreheaterBlockEntity) te2).getFacing().equals(context.getLevel().getOrientation().front()))
			preheater2 = Optional.of((BlastFurnacePreheaterBlockEntity) te2);
		int result1 = preheater1.map(getter).orElse(orElse);
		int result2 = preheater2.map(getter).orElse(orElse);
		return result1 + result2;
	}

	public int getFanSpeed(IMultiblockContext<CrucibleState> context) {
		boolean hasPreheater = getFromPreheater(context, BlastFurnacePreheaterBlockEntity::doSpeedup, 0) > 0;
		int fanSpeed = 0;
		if (hasPreheater) {
			fanSpeed = DEFAULT_ACTIVE_FAN_SPEED;
		}
		if (hasPreheater != context.getState().hasPreheater) {
			context.getState().hasPreheater = hasPreheater;
			context.markDirtyAndSync();
		}
		return fanSpeed;
	}
	@Override
	public void dropExtraItems(CrucibleState state, Consumer<ItemStack> drop) {
		MBInventoryUtils.dropItems(state.inventory, drop);
	}
}
