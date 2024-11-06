package lyrellion.ars_elemancy;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {

    public static class Common {

        public final ModConfigSpec.ConfigValue<Double> FireMasteryBuff;
        public final ModConfigSpec.ConfigValue<Double> WaterMasteryBuff;
        public final ModConfigSpec.ConfigValue<Double> AirMasteryBuff;
        public final ModConfigSpec.ConfigValue<Double> EarthMasteryBuff;

        public final ModConfigSpec.ConfigValue<Double> MajorFocusDiscount;
        public final ModConfigSpec.ConfigValue<Double> LesserFocusDiscount;
        public final ModConfigSpec.ConfigValue<Boolean> EnableRegenBonus;

        public static ModConfigSpec.IntValue ARMOR_MAX_MANA;
        public static ModConfigSpec.IntValue ARMOR_MANA_REGEN;

        public Common(ModConfigSpec.Builder builder) {
            builder.comment("Enable or disable the passive bonus of the foci").push("Elemental Spell Foci - Abilities");
            LesserFocusDiscount = builder.comment("Adjust this value to define how much the matching spell cost gets discounted by the lesser focus")
                    .defineInRange("elemental_less_focus_discount", 0.15D, 0.0D, 0.99D);
            MajorFocusDiscount = builder.comment("Adjust this value to define how much the matching spell cost gets discounted by the greater focus")
                    .defineInRange("elemental_maj_focus_discount", 0.25D, 0.0D, 0.99D);
            EnableRegenBonus = builder.comment("Enable regen bonus under special conditions").define("regen_bonus", true);
            builder.pop();

            builder.comment("Adjust these values to balance how much a spell gets amplified by the matching spell focus, doubled for major foci.")
                    .push("Elemental Spell Foci - Amplify");

            FireMasteryBuff = builder.define("fire_focus_buff", 1.0D);
            WaterMasteryBuff = builder.define("water_focus_buff", 1.0D);
            AirMasteryBuff = builder.define("air_focus_buff", 1.0D);
            EarthMasteryBuff = builder.define("earth_focus_buff", 1.0D);

            builder.comment("Adjust Elemental Armor Mana Buffs").push("Elemental Armors");

            ARMOR_MAX_MANA = builder.comment("Max mana bonus for each elemental armor piece").defineInRange("armorMaxMana", 100, 0, 10000);
            ARMOR_MANA_REGEN = builder.comment("Mana regen bonus for each elemental armor piece").defineInRange("armorManaRegen", 4, 0, 100);

            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    public static class Client {
        public static ModConfigSpec.ConfigValue<Boolean> EnableSFRendering;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("Visual Configs");

            EnableSFRendering = builder.comment("Enables the rendering of the spell focus while equipped").define("Enable SpellFocusRender", true);

            builder.pop();
        }
    }

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {

        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        final Pair<Client, ModConfigSpec> specClientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specClientPair.getRight();
        CLIENT = specClientPair.getLeft();

    }

}