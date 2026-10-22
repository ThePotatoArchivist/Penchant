package archives.tater.penchant.mixin.drops;

import archives.tater.penchant.registry.PenchantEnchantmentTags;
import archives.tater.penchant.registry.PenchantEnchantments;
import archives.tater.penchant.registry.PenchantFlag;
import archives.tater.penchant.registry.PenchantLootData;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.mutable.MutableFloat;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "enchantSpawnedEquipment",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V")
    )
    private void guaranteeDrop(Mob instance, EquipmentSlot equipmentSlot, ItemStack stack, Operation<Void> original) {
        original.call(instance, equipmentSlot, stack);
        if (PenchantFlag.GUARANTEED_ENCHANTED_DROP.isEnabled() &&
                stack.getEnchantments().keySet().stream().anyMatch(enchantment -> !enchantment.is(PenchantEnchantmentTags.IGNORE_GUARANTEED_DROP)))
            instance.setDropChance(equipmentSlot, 1f);
    }

    @WrapOperation(
            method = "dropCustomDeathLoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processEquipmentDropChance(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)F")
    )
    private float modifyDropChance(ServerLevel serverLevel, LivingEntity entity, DamageSource killingBlow, float chance, Operation<Float> original, @Local(name = "slot") EquipmentSlot slot) {
        var modifiedChance = new MutableFloat(original.call(serverLevel, entity, killingBlow, (Float) chance));
        var stack = getItemBySlot(slot);

        EnchantmentHelper.runIterationOnEquipment(entity, (enchantment, level, item) -> {
            var context = PenchantLootData.equipmentDropContext(serverLevel, level, entity, killingBlow, slot, stack);
            enchantment.value().getEffects(PenchantEnchantments.EQUIPMENT_DROPS).forEach(filteredEffect -> {
                if (filteredEffect.enchanted() == EnchantmentTarget.VICTIM && filteredEffect.affected() == EnchantmentTarget.VICTIM && filteredEffect.matches(context))
                    modifiedChance.setValue(filteredEffect.effect().process(level, random, modifiedChance.floatValue()));
            });
        });

        if (killingBlow.getEntity() instanceof LivingEntity livingAttacker)
            EnchantmentHelper.runIterationOnEquipment(livingAttacker, (enchantment, level, item) -> {
                var context = PenchantLootData.equipmentDropContext(serverLevel, level, entity, killingBlow, slot, stack);
                enchantment.value().getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach((filteredEffect) -> {
                    if (filteredEffect.enchanted() == EnchantmentTarget.ATTACKER && filteredEffect.affected() == EnchantmentTarget.VICTIM && filteredEffect.matches(context))
                        modifiedChance.setValue(filteredEffect.effect().process(level, random, modifiedChance.floatValue()));
                });
            });

        return modifiedChance.floatValue();
    }
}
