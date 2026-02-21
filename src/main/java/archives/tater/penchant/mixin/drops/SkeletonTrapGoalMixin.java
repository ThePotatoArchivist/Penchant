package archives.tater.penchant.mixin.drops;

import archives.tater.penchant.registry.PenchantFlag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.SkeletonTrapGoal;
import net.minecraft.world.entity.monster.Skeleton;

@Mixin(SkeletonTrapGoal.class)
public class SkeletonTrapGoalMixin {
    @Inject(
            method = "enchant",
            at = @At("TAIL")
    )
    private void guranteedDrop(Skeleton skeleton, EquipmentSlot slot, DifficultyInstance difficulty, CallbackInfo ci) {
        if (PenchantFlag.GUARANTEED_ENCHANTED_DROP.isEnabled())
            skeleton.setDropChance(EquipmentSlot.MAINHAND, 1f);
    }
}
