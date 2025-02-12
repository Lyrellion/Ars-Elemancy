package lyrellion.ars_elemancy.common.items.caster_tools;

import alexthw.ars_elemental.common.items.caster_tools.ElementalCasterTome;
import lyrellion.ars_elemancy.api.item.ISchoolFocus;
import lyrellion.ars_elemancy.common.components.SchoolCasterTomeData;
import lyrellion.ars_elemancy.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.items.CasterTome;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.NotEnoughManaPacket;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElemancyCasterTome extends CasterTome implements ISchoolFocus {
    private final SpellSchool school;

    public ElemancyCasterTome(Properties properties, SpellSchool school) {
        super(properties.component(ModRegistry.E_TOME_CASTER, new SchoolCasterTomeData()));
        this.school = school;
    }

    @Override
    public SpellSchool getSchool() {
        return school;
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        tooltip2.add(Component.translatable("tooltip.ars_elemancy.caster_tome"));
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    @Override
    public double getDiscount() {
        return 0;
    }

    public static class ETomeResolver extends SpellResolver {

        public SpellSchool getSchool() {
            return school;
        }

        private final SpellSchool school;

        public ETomeResolver(SpellContext context, SpellSchool school) {
            super(context);
            this.school = school;
        }

        @Override
        public boolean hasFocus(ItemStack stack) {
            return hasFocus(stack.getItem());
        }

        @Override
        public boolean hasFocus(Item item) {
            if (item instanceof ISchoolFocus focus) {
                return school == focus.getSchool();
            } else if (item == ItemsRegistry.SHAPERS_FOCUS.get()) {
                return school == SpellSchools.MANIPULATION;
            }
            return super.hasFocus(item);
        }

        @Override
        protected boolean enoughMana(LivingEntity entity) {
            int totalCost = getResolveCost();
            IManaCap manaCap = CapabilityRegistry.getMana(entity);
            if (manaCap == null)
                return false;
            boolean canCast = totalCost <= manaCap.getCurrentMana() || manaCap.getCurrentMana() == manaCap.getMaxMana() || (entity instanceof Player player && player.isCreative());
            if (!canCast && !entity.getCommandSenderWorld().isClientSide && !silent) {
                PortUtil.sendMessageNoSpam(entity, Component.translatable("ars_nouveau.spell.no_mana"));
                if (entity instanceof ServerPlayer serverPlayer)
                    Networking.sendToPlayerClient(new NotEnoughManaPacket(totalCost), serverPlayer);
            }
            return canCast;
        }

        @Override
        public SpellResolver getNewResolver(SpellContext context) {
            return new ETomeResolver(context, getSchool());
        }
    }
}
