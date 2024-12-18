package lyrellion.ars_elemancy.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.IElementalArmor;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.blocks.ElementalSpellTurretTile;
import alexthw.ars_elemental.common.entity.FirenandoEntity;
import alexthw.ars_elemental.common.glyphs.EffectBubbleShield;
import alexthw.ars_elemental.common.mob_effects.EnthrallEffect;
import alexthw.ars_elemental.recipe.HeadCutRecipe;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCut;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;

import static alexthw.ars_elemental.ConfigHandler.COMMON;
import static alexthw.ars_elemental.registry.ModPotions.*;
        import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_AIR;
import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_EARTH;

@EventBusSubscriber(modid = ArsElemental.MODID)
public class DamageEvents {


    @SubscribeEvent
    public static void bypassRes(LivingIncomingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (event.getSource().getEntity() instanceof Player player) {
            SpellSchool focus = ISchoolFocus.hasFocus(player);
            if (focus != null) {
                switch (focus.getId()) {
                    case "fire" -> {
                        //if the target is fire immune, cancel the event and deal damage
                        if (event.getSource().is(DamageTypeTags.IS_FIRE) && (living.fireImmune() || living.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                            event.setCanceled(true);
                            DamageSource newDamage = DamageUtil.source(player.level(), ModRegistry.MAGIC_FIRE, player);
                            living.hurt(newDamage, event.getAmount());
                        }
                    }
                    case "water" -> {
                        //if the target is immune to drowning, cancel the event and deal damage
                        if (event.getSource().is(DamageTypeTags.IS_DROWNING) && living.getType().is(EntityTypeTags.AQUATIC)) {
                            event.setCanceled(true);
                            DamageSource newDamage = DamageUtil.source(player.level(), DamageTypes.MAGIC, player);
                            living.hurt(newDamage, event.getAmount());
                        }
                    }
                }
            }

        } else if (event.getSource().getEntity() instanceof FirenandoEntity FE) {
            //if the firenando is attacking a non-monster, cancel the event
            if (!(living instanceof Monster mob)) {
                event.setCanceled(true);
                living.clearFire();
            } else {
                //if the firenando is attacking a monster, and the monster is fire immune, cancel the event and deal damage
                if ((mob.fireImmune() || living.hasEffect(MobEffects.FIRE_RESISTANCE)) && event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    event.setCanceled(true);
                    mob.hurt(DamageUtil.source(FE.level(), ModRegistry.MAGIC_FIRE, FE), event.getAmount());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleHealing(LivingHealEvent event) {
        //boost healing if you have earth focus
        if (COMMON.EnableGlyphEmpowering.get() || event.getEntity() instanceof Player player && ISchoolFocus.hasFocus(player) == ELEMENTAL_EARTH) {
            event.setAmount(event.getAmount() * 1.5F);
        }
        //cancel healing if under frozen effect
        if (event.getEntity().hasEffect(FROZEN)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void damageTweaking(LivingDamageEvent.Pre event) {

        var dealer = event.getSource().getEntity();
        var target = event.getEntity();

        // if frozen, boost next fire damage
        if (target.hasEffect(FROZEN) && event.getSource().is(ModRegistry.FIRE_DAMAGE)) {
            event.setNewDamage(event.getNewDamage() * 1.5F);
            target.removeEffect(FROZEN);
        }
        // if the target has magic fire, reduce earth damage
        if (target.hasEffect(MAGIC_FIRE) && event.getSource().is(ModRegistry.EARTH_DAMAGE)) {
            event.setNewDamage(event.getNewDamage() * 0.85F);
        }


        SpellSchool focus = ISchoolFocus.hasFocus(dealer);
        if (dealer instanceof Player && focus != null) {
            switch (focus.getId()) {
                case "water" -> {
                    //change the freezing buff from useless to the whole damage
                    if (target.getPercentFrozen() > 0.75F && event.getSource().is(DamageTypeTags.IS_FREEZING)) {
                        event.setNewDamage(event.getNewDamage() * 1.25F);
                    }
                }
                case "air" -> {
                    //let's try to compensate the loss of iframe skip with a buff to WS
                    if (target.hasEffect(MobEffects.LEVITATION) && event.getSource().is(DamageTypeTags.IS_FALL)) {
                        event.setNewDamage(event.getNewDamage() * 1.25F);
                    }
                }
            }
        }

        //fetch the damage reduction from the armor according to the damage source
        HashMap<SpellSchool, Integer> bonusMap = new HashMap<>();
        int bonusReduction = 0;

        for (ItemStack stack : event.getEntity().getArmorSlots()) {
            Item item = stack.getItem();
            if (item instanceof IElementalArmor armor && armor.fillAbsorptions(event.getSource(), bonusMap)) {
                bonusReduction++;
            }
        }

        boolean not_bypassEnchants = !event.getSource().is(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        if (target instanceof Player player) {
            if (event.getSource().getEntity() instanceof LivingEntity living && EnthrallEffect.isEnthralledBy(living, player))
                event.setNewDamage(event.getNewDamage() * .5F);
            if (not_bypassEnchants) {
                //reduce damage from elytra if you have air focus
                if (event.getSource().is(DamageTypes.FLY_INTO_WALL) && ISchoolFocus.hasFocus(player) == ELEMENTAL_AIR) {
                    event.setNewDamage(event.getNewDamage() * .1F);
                }

                //if you have 4 pieces of the fire school, fire is removed. Apply the fire focus buff if you have it, since it wouldn't detect the fire otherwise
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_FIRE, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    player.clearFire();
                    if (ISchoolFocus.hasFocus(player) == SpellSchools.ELEMENTAL_FIRE) {
                        player.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, 200, 2));
                    }
                }
                //if you have 4 pieces of the water school, you get extra air when drowning
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_WATER, 0) == 4 && event.getSource().is(DamageTypes.DROWN)) {
                    player.setAirSupply(player.getMaxAirSupply());
                    bonusReduction += 5;
                }
                //if you have 4 pieces of the earth school, you get extra food when you are low
                if (bonusMap.getOrDefault(ELEMENTAL_EARTH, 0) == 4 && player.getEyePosition().y() < 20 && player.getFoodData().getFoodLevel() < 2) {
                    player.getFoodData().setFoodLevel(20);
                }
                //if you have 4 pieces of the air school, you get extra fall damage reduction
                if (bonusMap.getOrDefault(ELEMENTAL_AIR, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FALL)) {
                    bonusReduction += 5;
                }

                if (bonusReduction > 0) {
                    //convert the damage reduction into mana and add the mana regen effect
                    var mana = CapabilityRegistry.getMana(player);
                    if (mana != null) {
                        if (bonusReduction > 3) mana.addMana(event.getOriginalDamage() * 5);
                        event.getEntity().addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 200, bonusReduction / 2));
                    }
                }

            }
        }

        if (bonusReduction > 0 && not_bypassEnchants)
            event.setNewDamage(event.getNewDamage() * (1 - bonusReduction / 10F));

        // if damage is magic and target has magic fire, add back the half the damage that was reduced from the armor points
        if (event.getSource().is(Tags.DamageTypes.IS_MAGIC) && target.hasEffect(MAGIC_FIRE)) {
            var armorReduction = event.getContainer().getReduction(DamageContainer.Reduction.ARMOR);
            event.setNewDamage(event.getNewDamage() + armorReduction * 0.5F);
        }

        int ManaBubbleCost = EffectBubbleShield.INSTANCE.GENERIC_INT.get();
        //check if the entity has the mana bubble effect and if so, reduce the damage
        if (not_bypassEnchants && event.getEntity().hasEffect(MANA_BUBBLE)) {
            LivingEntity living = event.getEntity();
            var mana = CapabilityRegistry.getMana(event.getEntity());
            if (mana != null) {
                double maxReduction = mana.getCurrentMana() / ManaBubbleCost;
                double amp = Math.min(1 + living.getEffect(MANA_BUBBLE).getAmplifier() / 2D, maxReduction);
                float newDamage = (float) Math.max(0.1, event.getNewDamage() - amp);
                float actualReduction = event.getNewDamage() - newDamage;
                // don't deplete mana if the entity is invulnerable due to a previous attack
                if (actualReduction > 0 && mana.getCurrentMana() >= ManaBubbleCost && event.getContainer().getPostAttackInvulnerabilityTicks() != event.getEntity().invulnerableTime) {
                    event.setNewDamage(newDamage);
                    mana.removeMana(actualReduction * ManaBubbleCost);
                }
                if (mana.getCurrentMana() < ManaBubbleCost) {
                    living.removeEffect(MANA_BUBBLE);
                }
            }
        }
    }


    @SubscribeEvent
    public static void vorpalCut(SpellDamageEvent.Post event) {
        if (!(event.target instanceof LivingEntity living) || living.getHealth() > 0) return;
        SpellSchool school = event.context.getCaster() instanceof TileCaster tc && tc.getTile() instanceof ElementalSpellTurretTile turret ? turret.getSchool() : ISchoolFocus.hasFocus(event.caster);
        Spell subspell = new Spell(event.context.getSpell().unsafeList().subList(event.context.getCurrentIndex() - 1, event.context.getSpell().size()));
        if (subspell.recipe().iterator().next() == EffectCut.INSTANCE && school == ELEMENTAL_AIR) {
            ItemStack skull = null;
            int chance = 0;
            ResourceLocation mob = RegistryHelper.getRegistryName(living.getType());
            if (living instanceof Player player) {
                GameProfile gameprofile = player.getGameProfile();
                skull = new ItemStack(Items.PLAYER_HEAD);
                chance = 20;
                skull.set(DataComponents.PROFILE, new ResolvableProfile(gameprofile));
            } else {
                for (RecipeHolder<HeadCutRecipe> recipeh : living.level().getRecipeManager().getAllRecipesFor(ModRegistry.HEAD_CUT.get())) {
                    HeadCutRecipe recipe = recipeh.value();
                    if (recipe.mob.equals(mob)) {
                        skull = recipe.result.copy();
                        chance = recipe.chance;
                        break;
                    }
                }
            }
            if (skull == null) return;

            int looting = Math.min(3, subspell.getBuffsAtIndex(0, event.caster, AugmentFortune.INSTANCE));
            for (int i = -1; i < looting; i++)
                if (living.getRandom().nextInt(100) <= chance) {
                    living.spawnAtLocation(skull);
                    break;
                }
        }
    }

}