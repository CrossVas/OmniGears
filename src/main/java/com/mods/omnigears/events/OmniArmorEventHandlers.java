package com.mods.omnigears.events;

import com.mods.omnigears.items.armors.ItemAdvancedNanoChest;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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

    public static class OnHurtEvent {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onHurtEvent(LivingHurtEvent e) {
            DamageSource damageSource = e.getSource();
            LivingEntity target = e.getEntity();
            float damage = e.getAmount();
            if (e.isCanceled()) return;
            if (damageSource.isBypassArmor() && damageSource.isBypassMagic()) return;
            if (damage <= 0) return;
            if (!damageSource.isBypassArmor()) {
                float realDamage = Math.max(0.5F, damage * 0.25F);
                target.getArmorSlots().forEach(stack -> {
                    if (stack.getItem() instanceof ItemAdvancedNanoChest armor) {
                        int energy = Math.min((int) (realDamage * armor.energyPerDamage), armor.getEnergyStored(stack));
                        armor.extractEnergy(stack, energy, false);
                    }
                });
            }
        }
    }
}
