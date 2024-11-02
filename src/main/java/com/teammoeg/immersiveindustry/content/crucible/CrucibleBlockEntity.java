/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.content.crucible;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.content.IActiveState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class CrucibleBlockEntity extends MultiblockPartBlock<CrucibleBlockEntity> implements IIEInventory,
        IActiveState, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessBE, IEBlockInterfaces.IBlockBounds, IMultiblockState {

    public CrucibleBlockEntity.CrucibleData guiData = new CrucibleBlockEntity.CrucibleData();
    private NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);
    public int temperature;
    public int burnTime;
    public int process = 0;
    public int processMax = 0;
    public int updatetick = 0;
    public FluidStack resultFluid = FluidStack.EMPTY;
    public FluidTank[] tank = new FluidTank[]{new FluidTank(14400)};
    public static ResourceLocation coal_coke = new ResourceLocation("forge:coal_coke");

    private static BlockPos fluidout = new BlockPos(2, 2, 2);

    public CrucibleBlockEntity() {
        super(IIContent.IIMultiblocks.CRUCIBLE, IIContent.IITileTypes.CRUCIBLE.get(), false);
    }

    @Nonnull
    @Override
    public IFluidTank[] getAccessibleFluidTanks(Direction side) {
        CrucibleBlockEntity master = master();
        if (master != null) {
            if (this.posInMultiblock.getY() == 2 && (this.posInMultiblock.getZ() == 2 ||
                    this.posInMultiblock.getX() == 2)) {
                return master.tank;
            }
        }
        return new FluidTank[0];
    }

    @Override
    public boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
        return false;
    }

    @Override
    public boolean canDrainTankFrom(int iTank, Direction side) {
        CrucibleBlockEntity master = master();
        if (master != null) {
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx) {
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean receiveClientEvent(int id, int arg) {
        if (id == 0)
            this.formed = arg == 1;
        markDirty();
        this.markContainingBlockForUpdate(null);
        return true;
    }

    @Nullable
    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return master();
    }

    @Override
    public IEMenuTypes.ArgContainer getContainerType() {
        return null;
    }

    @Override
    public boolean canUseGui(Player player) {
        return ;
    }

    @Override
    public boolean canUseGui(PlayerEntity player) {
        return formed;
    }

    @Override
    public int[] getCurrentProcessesStep() {
        CrucibleBlockEntity master = master();
        if (master != this && master != null)
            return master.getCurrentProcessesStep();
        return new int[]{processMax - process};
    }

    @Override
    public int[] getCurrentProcessesMax() {
        CrucibleBlockEntity master = master();
        if (master != this && master != null)
            return master.getCurrentProcessesMax();
        return new int[]{processMax};
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        CrucibleBlockEntity master = master();
        if (master != null && master.formed && formed)
            return master.inventory;
        return this.inventory;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (slot == 0 || slot == 1 || slot == 2 || slot == 3)
            return CrucibleRecipe.isValidInput(stack);
        if (slot == 4)
            return CrucibleRecipe.getFuelTime(stack) > 0;
        return false;
    }


    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    LazyOptional<IItemHandler> inputHandler = registerConstantCap(
            new IEInventoryHandler(4, this, 0, true, false)
    );
    LazyOptional<IItemHandler> fuelHandler = registerConstantCap(
            new IEInventoryHandler(1, this, 2, true, false)
    );
    LazyOptional<IItemHandler> outputHandler = registerConstantCap(
            new IEInventoryHandler(1, this, 3, false, true)
    );
    private CapabilityReference<IFluidHandler> fluidHandler = CapabilityReference.forBlockEntityAt(this, () -> {
        return new DirectionalBlockPos(this.getBlockPosForPos(fluidout),Direction.DOWN);
    }, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            CrucibleBlockEntity master = master();
            if (master != null) {
                if (this.posInMultiblock.getY() <= 1)
            		return master.fuelHandler.cast();
            	else if(facing==Direction.UP)
            		return master.inputHandler.cast();
            	else
            		return master.outputHandler.cast();
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        tank[0].readFromNBT(nbt.getCompound("tank"));
        temperature=nbt.getInt("temperature");
        burnTime=nbt.getInt("burntime");
        if (!descPacket) {
            process = nbt.getInt("process");
            processMax = nbt.getInt("processMax");
            resultFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("result_fluid"));
            ItemStackHelper.loadAllItems(nbt, inventory);
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("tank", tank[0].writeToNBT(new CompoundNBT()));
        nbt.putInt("temperature", temperature);
        nbt.putInt("burntime", burnTime);
        if (!descPacket) {
            nbt.putInt("process", process);
            nbt.putInt("processMax", processMax);
            nbt.put("result_fluid", resultFluid.writeToNBT(new CompoundNBT()));
            ItemStackHelper.saveAllItems(nbt, inventory);
        }
    }
    @Override
    public void tick() {
        checkForNeedlessTicking();
        if (!isDummy()) {
            if (!world.isRemote && formed) {
                CrucibleRecipe recipe = getRecipe();
                tryOutput();
                updatetick++;
                if (updatetick > 10) {
                    final boolean activeBeforeTick = getIsActive();
                    if (temperature > 0) {
                        updatetick = 0;
                        this.markContainingBlockForUpdate(null);
                        if (!activeBeforeTick)
                            setActive(true);
                    } else if (activeBeforeTick)
                        setActive(false);
                    final boolean activeAfterTick = getIsActive();
                    if (activeBeforeTick != activeAfterTick) {
                        master().markDirty();
                        // scan 3x4x3
                        for (int x = 0; x < 3; ++x)
                            for (int y = 0; y < 4; ++y)
                                for (int z = 0; z < 3; ++z) {
                                    BlockPos actualPos = getBlockPosForPos(new BlockPos(x, y, z));
                                    BlockEntity te = Utils.getExistingTileEntity(world, actualPos);
                                    if (te instanceof CrucibleBlockEntity)
                                        ((CrucibleBlockEntity) te).setActive(activeAfterTick);
                                }
                    }
                }
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
                //new heating mechanism
                if (burnTime > 0){
                    double coefficient = getFanSpeed() < 64 ? 0 : Math.sqrt(getFanSpeed()) / 8 ;
                    if (coefficient == 0){//Speed < 64, no boost
                        if(temperature > 1000){
                            temperature--;
                        }else{
                            temperature++;
                        }
                    }else{//Speed >= 64, higher temperature and faster speed up
                        if(temperature < 1700){
                            temperature++;
                            if(Math.random() + Math.random() < coefficient){
                                temperature++;
                            }
                            if(Math.random() + Math.random() + Math.random() < coefficient){
                                temperature++;
                            }
                        }
                    }
                }else if (temperature > 0){
                    temperature--;
                }
                if (burnTime > 0) {
                    burnTime--;
                } else {
                    if (!inventory.get(4).isEmpty() && CrucibleRecipe.getFuelTime(inventory.get(4)) > 0/*inventory.get(4).getItem().getTags().contains(coal_coke)*/) {
                        //burnTime = IIConfig.COMMON.coke.get();
                        burnTime = CrucibleRecipe.getFuelTime(inventory.get(4)) / 2;
                        inventory.get(4).shrink(1);
                        master().markDirty();
                    }
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
            if (world != null && formed && getIsActive()) {
                Random random = world.rand;
                if (random.nextFloat() < 0.4F) {
                    for (int i = 0; i < random.nextInt(2) + 2; ++i) {
                        world.addOptionalParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, pos.getX() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1), pos.getY() + random.nextDouble() + random.nextDouble(), pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1), 0.0D, 0.05D, 0.0D);
                        world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.25D + random.nextDouble() / 2.0D * (random.nextBoolean() ? 1 : -1), pos.getY() + 0.4D, pos.getZ() + 0.25D + random.nextDouble() / 2.0D * (random.nextBoolean() ? 1 : -1), 0.002D, 0.01D, 0.0D);
                    }
                }
            }
        }
    }


    @Nullable
    public CrucibleRecipe getRecipe() {
        /*if (inventory.get(0).isEmpty())
            return null;*/
        CrucibleRecipe recipe = CrucibleRecipe.findRecipe(inventory.get(0), inventory.get(1), inventory.get(2), inventory.get(3));
        if (recipe == null)
            return null;
        if (inventory.get(5).isEmpty() || (ItemStack.areItemsEqual(inventory.get(5), recipe.output) &&
                inventory.get(5).getCount() + recipe.output.getCount() <= getSlotLimit(5))) {
            return recipe;
        }
        return null;
    }

    public void tryOutput() {
        boolean update = false;
        if (this.tank[0].getFluidAmount() > 0) {
            FluidStack out = Utils.copyFluidStackWithAmount(this.tank[0].getFluid(),
                    Math.min(this.tank[0].getFluidAmount(), 80), false);
            if (fluidHandler.isPresent()) {
                IFluidHandler output = fluidHandler.getNullable();
                int accepted = output.fill(out, IFluidHandler.FluidAction.SIMULATE);

                if (accepted > 0) {
                    int drained = output.fill(
                            Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false),
                            IFluidHandler.FluidAction.EXECUTE);
                    this.tank[0].drain(drained, IFluidHandler.FluidAction.EXECUTE);
                    out.shrink(accepted);
                    update |= true;
                }
            }
        }
        if (update) {
            this.markDirty();
            this.markContainingBlockForUpdate(null);
        }
    }

    @Override
    public BlockState getState() {
        return null;
    }

    @Override
    public void setState(BlockState blockState) {

    }

    @NotNull
    @Override
    public VoxelShape getBlockBounds(@org.jetbrains.annotations.Nullable CollisionContext collisionContext) {
        return null;
    }

    @Override
    public void writeSaveNBT(CompoundTag compoundTag) {

    }

    @Override
    public void readSaveNBT(CompoundTag compoundTag) {

    }

    public class CrucibleData implements IIntArray {
        public static final int BURN_TIME = 0;
        public static final int PROCESS_MAX = 1;
        public static final int CURRENT_PROCESS = 2;

        @Override
        public int get(int index) {
            switch (index) {
                case BURN_TIME:
                    return burnTime;
                case PROCESS_MAX:
                    return processMax;
                case CURRENT_PROCESS:
                    return process;
                default:
                    throw new IllegalArgumentException("Unknown index " + index);
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case BURN_TIME:
                    burnTime = value;
                    break;
                case PROCESS_MAX:
                    processMax = value;
                    break;
                case CURRENT_PROCESS:
                    process = value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown index " + index);
            }
        }

        @Override
        public int size() {
            return 3;
        }
    }

    public <V> V getFromPreheater(Function<BlastFurnacePreheaterBlockEntity, V> getter, V orElse) {
        return getBlast().map(getter).orElse(orElse);
    }

    public Optional<BlastFurnacePreheaterBlockEntity> getBlast() {
        BlockPos pos = getPos().add(0, -1, 0).offset(getFacing(), 2);
        BlockEntity te = Utils.getExistingTileEntity(world, pos);
        if (te instanceof BlastFurnacePreheaterBlockEntity&&((BlastFurnacePreheaterBlockEntity) te).getFacing().equals(this.getFacing().getOpposite()))
            return Optional.of((BlastFurnacePreheaterBlockEntity) te);
        return Optional.empty();
    }

    public int getFanSpeed(){
        return 128;
    }
}
