package com.gregtechceu.gtceu.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class RecipeLogicProvider extends CapabilityBlockProvider<RecipeLogic> {

    public RecipeLogicProvider() {
        super(GTCEu.id("recipe_logic_provider"));
    }

    @Nullable
    @Override
    protected RecipeLogic getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return GTCapabilityHelper.getRecipeLogic(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, RecipeLogic capability) {
        data.putBoolean("Working", capability.isWorking());
        var recipeInfo = new CompoundTag();
        var recipe = capability.getLastRecipe();
        if (recipe != null) {
            var EUt = RecipeHelper.getInputEUt(recipe);
            var isInput = true;
            if (EUt == 0) {
                isInput = false;
                EUt = RecipeHelper.getOutputEUt(recipe);
            }

            recipeInfo.putLong("EUt", EUt);
            recipeInfo.putBoolean("isInput", isInput);
        }

        if (!recipeInfo.isEmpty()) {
            data.put("Recipe", recipeInfo);
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (capData.getBoolean("Working")) {
            var recipeInfo = capData.getCompound("Recipe");
            if (!recipeInfo.isEmpty()) {
                var EUt = recipeInfo.getLong("EUt");
                var isInput = recipeInfo.getBoolean("isInput");
                boolean isSteam = false;

                if (blockEntity instanceof MetaMachineBlockEntity mbe) {
                    var machine = mbe.getMetaMachine();
                    if (machine instanceof SimpleSteamMachine) {
                        isSteam = true;
                    } else if (machine instanceof SteamParallelMultiblockMachine) {
                        EUt = (long) (EUt * SteamParallelMultiblockMachine.CONVERSION_RATE);
                        isSteam = true;
                    }
                }

                if (EUt > 0) {
                    Component text;

                    if (isSteam) {
                        text = Component.literal(FormattingUtil.formatNumbers(EUt)).withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(" mB/t").withStyle(ChatFormatting.RESET));
                    } else {
                        var tier = GTUtil.getTierByVoltage(EUt);
                        text = Component.literal(FormattingUtil.formatNumbers(EUt)).withStyle(ChatFormatting.RED)
                                .append(Component.literal(" EU/t").withStyle(ChatFormatting.RESET)
                                        .append(Component.literal(" (").withStyle(ChatFormatting.GREEN)
                                                .append(Component.literal(GTValues.VNF[tier])
                                                        .withStyle(style -> style.withColor(GTValues.VC[tier])))
                                                .append(Component.literal(")").withStyle(ChatFormatting.GREEN))));
                    }

                    if (isInput) {
                        tooltip.add(Component.translatable("gtceu.top.energy_consumption").append(" ").append(text));
                    } else {
                        tooltip.add(Component.translatable("gtceu.top.energy_production").append(" ").append(text));
                    }
                }
            }
        }
    }
}
