package lyrellion.ars_elemancy.datagen;

import lyrellion.ars_elemancy.ArsElemancy;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ArsElemancy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Datagen {

    public static CompletableFuture<HolderLookup.Provider> provider;
    public static PackOutput output;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        provider = event.getLookupProvider();
        output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new AEBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(event.includeClient(), new AEItemModelProvider(gen, existingFileHelper));

        BlockTagsProvider BTP = new AETagsProvider.AEBlockTagsProvider(gen, provider, existingFileHelper);
        gen.addProvider(event.includeServer(), BTP);
        gen.addProvider(event.includeServer(), new AETagsProvider.AEItemTagsProvider(gen, provider, BTP, existingFileHelper));
        gen.addProvider(event.includeServer(), new AETagsProvider.AEEntityTagProvider(gen, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new AETagsProvider.AEDamageTypeProvider(gen, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(gen, provider));
        gen.addProvider(event.includeServer(), new AELootTables(gen, provider));

        gen.addProvider(event.includeServer(), new AEImbuementProvider(gen));
        gen.addProvider(event.includeServer(), new AEGlyphProvider(gen));
        gen.addProvider(event.includeServer(), new AEApparatusProvider(gen));

        gen.addProvider(event.includeServer(), new AEPatchouliProvider(gen, provider));
        gen.addProvider(event.includeServer(), new AECurioProvider(output, existingFileHelper, provider));

        gen.addProvider(event.includeServer(), new AEAdvancementsProvider(output, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new AECasterTomeProvider(gen));
        gen.addProvider(event.includeClient(), new AELanguageProvider(output, ArsElemancy.MODID, "en_us"));
    }

    public static <T> Collection<T> takeAll(Collection<T> src, Predicate<T> predicate) {
        List<T> ret = new ArrayList<>();

        Iterator<T> iter = src.iterator();
        while (iter.hasNext()) {
            T item = iter.next();
            if (predicate.test(item)) {
                iter.remove();
                ret.add(item);
            }
        }

        if (ret.isEmpty()) {
            return Collections.emptyList();
        }
        return ret;
    }

}
