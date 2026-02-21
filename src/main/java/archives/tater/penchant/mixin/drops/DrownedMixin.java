package archives.tater.penchant.mixin.drops;

import archives.tater.penchant.registry.PenchantFlag;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Drowned.class)
public class DrownedMixin extends Zombie {
    public DrownedMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE:FIRST", target = "Lnet/minecraft/world/entity/monster/Drowned;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V")
    )
    private void guranteedTrident(Drowned instance, EquipmentSlot equipmentSlot, ItemStack itemStack, Operation<Void> original) {
        original.call(instance, equipmentSlot, itemStack);
        if (PenchantFlag.GUARANTEED_TRIDENT_DROP.isEnabled())
            setDropChance(equipmentSlot, 1f);
    }
}
