package com.teammoeg.immersiveindustry.content.crucible;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnState;
import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.IIUtil;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class CrucibleLogic implements IClientTickableComponent<CrucibleState>, IMultiblockLogic<CrucibleState>, IServerTickableComponent<CrucibleState> {

	public CrucibleLogic() {

	}
	static final CapabilityFacing fluidout = new CapabilityFacing(2, 1, 2,RelativeBlockFace.UP);
	@Override
	public void tickServer(IMultiblockContext<CrucibleState> context) {
        //CrucibleRecipe recipe = getRecipe();
        tryOutput(context);
        /*
        if (burnTime > 0){
            if (getFromPreheater(BlastFurnacePreheaterTileEntity::doSpeedup, 0) > 0) {
            	if(temperature < 1680)
            		temperature++;//with preheater, boost
            }else if (temperature < 1100)
                temperature++;//without preheater, up to 1100
            else if(temperature >1100)
            	temperature--;//without preheater, reduce
        } else if (temperature > 0) {
            temperature--;
        }*/
        CrucibleState state=context.getState();
        ChangeDetectedItemHandler inventory=state.inventory;
        boolean lastIsActive=state.active;
        state.active=false;
        if (state.burnTime <= 0)  {
            if (!inventory.getStackInSlot(4).isEmpty() ) {
            	int totalTime=CrucibleRecipe.getFuelTime(context.getLevel().getRawLevel(),inventory.getStackInSlot(4));
            	if(totalTime>0) {
	                //burnTime = IIConfig.COMMON.coke.get();
	                state.burnTime = totalTime;
	                ItemStack origin=inventory.getStackInSlot(4);
	                if(origin.getCount()==1)
	                	inventory.setStackInSlot(4, ItemStack.EMPTY);
	                else
	                	inventory.setStackInSlot(4, origin.copyWithCount(origin.getCount()-1));
	                context.markMasterDirty();
            	}
            }
        }
        //new heating mechanism
        if (state.burnTime > 0){
            double coefficient = getFanSpeed() < 64 ? 0 : Math.sqrt(getFanSpeed()) / 8 ;
            if (coefficient == 0){//Speed < 64, no boost
                if(state.temperature > 1000){
                	state.temperature--;
                }else{
                	state.active=true;
                	state.burnTime--;
                	state.temperature++;
                }
            }else{//Speed >= 64, higher temperature and faster speed up
                if(state.temperature < 1700){
                	state.active=true;
                	state.burnTime--;
                	state.temperature++;
                    if(Math.random() + Math.random() < coefficient){
                    	state.temperature++;
                    }
                    if(Math.random() + Math.random() + Math.random() < coefficient){
                    	state.temperature++;
                    }
                }
            }
            context.markMasterDirty();
        }
        if (state.temperature > 0){
        	if()
        	state.temperature--;
        	context.markMasterDirty();
        }

        if(recipe == null){
            process = 0;
            processMax = 0;
        } else if (temperature >= recipe.temperature) {
            if (process > 0) {
                if (burnTime == 0) {
                    process--;
                }
                // during process
                else {
                    if (recipe.time != processMax) {
                        process = 0;
                        processMax = 0;
                    } else {
                        process--;
                        getFromPreheater(BlastFurnacePreheaterBlockEntity::doSpeedup, 0);
                    }
                }
                this.markContainingBlockForUpdate(null);
            } else {
                if (processMax == 0) {
                    this.process = recipe.time;
                    this.processMax = process;
                } else {
                    for (IngredientWithSize is : recipe.inputs) {
                        for (int i = 0; i < 4; i++) {
                            if (is.test(inventory.get(i))) {
                                Utils.modifyInvStackSize(inventory, i, -is.getCount());
                                break;
                            }
                        }
                    }
                    if (recipe.output_fluid != null) {
                        FluidStack matching = recipe.output_fluid;
                        if (!matching.isEmpty())
                            resultFluid = matching.copy();
                    }
                    if (!resultFluid.isEmpty()) {
                        int filled = tank[0].fill(resultFluid, IFluidHandler.FluidAction.EXECUTE);
                        if (filled < resultFluid.getAmount()) {
                            resultFluid.shrink(filled);
                            //return;
                        }
                        resultFluid = FluidStack.EMPTY;
                    }
                    if (!inventory.get(5).isEmpty())
                        inventory.get(5).grow(recipe.output.copy().getCount());
                    else if (inventory.get(5).isEmpty())
                        inventory.set(5, recipe.output.copy());
                    processMax = 0;
                }
            }
        }else{
            process = 0;
            processMax = 0;
        }
		
	}

	@Override
	public CrucibleState createInitialState(IInitialMultiblockContext<CrucibleState> capabilitySource) {
		return new CrucibleState(capabilitySource);
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
		return t->Shapes.block();
	}

	@Override
	public void tickClient(IMultiblockContext<CrucibleState> context) {
		RandomSource random =context.getLevel().getRawLevel().random;
		Level world=context.getLevel().getRawLevel();
		BlockPos pos=context.getLevel().toAbsolute(IIMultiblocks.CRUCIBLE.masterPosInMB());
        if (random.nextFloat() < 0.4F) {
            for (int i = 0; i < random.nextInt(2) + 2; ++i) {
                world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, pos.getX() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1), pos.getY() + random.nextDouble() + random.nextDouble(), pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1), 0.0D, 0.05D, 0.0D);
                world.addAlwaysVisibleParticle(ParticleTypes.SMOKE, pos.getX() + 0.25D + random.nextDouble() / 2.0D * (random.nextBoolean() ? 1 : -1), pos.getY() + 0.4D, pos.getZ() + 0.25D + random.nextDouble() / 2.0D * (random.nextBoolean() ? 1 : -1), 0.002D, 0.01D, 0.0D);
            }
        }
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<CrucibleState> ctx, CapabilityPosition position, Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {

                if (position.posInMultiblock().getY() <= 1)
            		return ctx.getState().fuelHandler.cast(ctx);
            	else if(position.side()==RelativeBlockFace.UP)
            		return ctx.getState().inputHandler.cast(ctx);
            	else
            		return ctx.getState().outputHandler.cast(ctx);
        }
        if(cap==ForgeCapabilities.FLUID_HANDLER) {
        	if(fluidout.isCapabilityPosition(position))
        		return ctx.getState().outputFluidHandler.cast(ctx);
        }
        return LazyOptional.empty();
	}

	@Override
	public void onEntityCollision(IMultiblockContext<CrucibleState> ctx, BlockPos posInMultiblock, Entity collided) {
		if(posInMultiblock.getY()>=2) {
			if(!(collided instanceof ItemEntity)) {
				if(collided instanceof LivingEntity col&&ctx.getState().active)
					col.hurt(ctx.getLevel().getRawLevel().damageSources().hotFloor(),4);
				return;
			}
			ItemEntity itemEntity=(ItemEntity) collided;
    		ItemStack insertItem = ItemHandlerHelper.insertItem(ctx.getState().inventory, itemEntity.getItem().copy(), false);
			if (insertItem.isEmpty()) {
				itemEntity.remove(RemovalReason.DISCARDED);
				return;
			}
			itemEntity.setItem(insertItem);
	    	
		}
	}
	public boolean tryOutput(IMultiblockContext<CrucibleState> context) {
		CrucibleState state=context.getState();
		boolean update = FluidUtils.multiblockFluidOutput(state.outputFluidCap, state.tank, 0, 0, null);
		return update;
	}
	   public <V> V getFromPreheater(IMultiblockContext<CrucibleState> context,Function<BlastFurnacePreheaterBlockEntity, V> getter, V orElse) {
	        return getBlast(context).map(getter).orElse(orElse);
	    }

	    public Optional<BlastFurnacePreheaterBlockEntity> getBlast(IMultiblockContext<CrucibleState> context) {
	    	BlockEntity te=context.getLevel().getBlockEntity(new BlockPos(1,0,2));
	        if (te instanceof BlastFurnacePreheaterBlockEntity&&((BlastFurnacePreheaterBlockEntity) te).getFacing().equals(context.getLevel().getOrientation().front().getOpposite()))
	            return Optional.of((BlastFurnacePreheaterBlockEntity) te);
	        return Optional.empty();
	    }

	    public int getFanSpeed(){
	        return 0;
	    }
}
