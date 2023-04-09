package com.mods.omnigears;

import cofh.lib.api.item.IEnergyContainerItem;
import com.mods.omnigears.items.armors.ItemAdvancedNanoChest;
import com.mods.omnigears.items.armors.base.IProtectionProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OmniArmorEventHandlers {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttackEvent(LivingAttackEvent e) {
        if (e.isCanceled())
            return;

        LivingEntity target = e.getEntity();
        DamageSource damageSource = e.getSource();

//        if ()
    }

    public static class OnFallDamage {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onFallEvent(LivingFallEvent e) {
            LivingEntity entity = e.getEntity();
            Level level = entity.level;

            if (!level.isClientSide()) {
                entity.getArmorSlots().forEach(armorChest -> {
                    float fallDamageAbsorption = 1.0F; // 100%
                    if (armorChest.getItem() instanceof IEnergyContainerItem electricArmor) {
                        if (armorChest.getItem() == OmniGearsObjects.NANO_BOOTS)
                            fallDamageAbsorption = 0.25F; // 25%
                        int fallDamage = (int) Math.max(e.getDistance() * fallDamageAbsorption, 0);
                    }
                });
            }
        }
    }

    public static class OnHurtEvent {
        // handles armor energy usage when taking damage
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onHurtEvent(LivingHurtEvent e) {
            DamageSource damageSource = e.getSource();
            LivingEntity target = e.getEntity();
            float damage = e.getAmount();
            if (e.isCanceled()) return;
            if (damageSource.isBypassMagic()) return;
            if (damage <= 0) return;
            if (!damageSource.isBypassArmor()) {
                float realDamage = Math.max(0.5F, damage * 0.25F);
                target.getArmorSlots().forEach(stack -> {
                    if (stack.getItem() instanceof IProtectionProvider chest) {
                        int energy = Math.min((int) (realDamage * chest.getEnergyPerDamage()), chest.getStoredEnergy(stack));
                        chest.useEnergy(stack, energy, false);
                    }
                });
            }
        }
    }
}
