package com.prefab.items;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/**
 * This class is used to create a sword which has the same speed as pre-1.9
 * swords.
 *
 * @author WuestMan
 */
public class ItemSwiftBlade extends SwordItem {
    /*
     * Initializes a new instance of the ItemSwiftBlade class.
     */
    public ItemSwiftBlade(Tier tier, int attackDamageIn, float attackSpeedIn) {
        super(tier,
                new Item.Properties()
                        .attributes(SwordItem.createAttributes(tier, attackDamageIn, attackSpeedIn))
                        .stacksTo(1)
                        .durability(tier.getUses()));
    }

    /**
     * Returns the amount of damage this item will deal. One heart of damage is
     * equal to 2 damage points.
     */
    @Override
    public float getAttackDamageBonus(Entity entity, float f, DamageSource damageSource) {
        return this.getTier().getAttackDamageBonus();
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on
     * material.
     */
    @Override
    public int getEnchantmentValue() {
        return this.getTier().getEnchantmentValue();
    }

}