package lyrellion.ars_elemancy.datagen;

import com.hollingsworth.arsnouveau.api.documentation.ReloadDocumentationEvent;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.TextEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;

import static com.hollingsworth.arsnouveau.setup.registry.Documentation.addPage;
import lyrellion.ars_elemancy.common.items.foci.ElemancyFocus
import lyrellion.ars_elemancy.registry.ModItems;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;


public class AMDocumentation
{
    public static void addPages (ReloadDocumentationEvent.AddEntries ignored)
    {
        ElemancyFocus elemancerFocus = ModItems.ELEMANCER_FOCUS.get();
        DocEntry gettingStartedPage = addPage(new DocEntryBuilder(ArsElemancy.MODID, DocumentationRegistry.GETTING_STARTED, "ars_elemancy.page.getting_started.title", ArsElemancy.prefix("getting_started"))
            .withIcon(elemancerFocus)
            .withPage(TextEntry.create(Component.translatable("ars_elemancy.page.getting_started.one"), Component.translatable("itemGroup.ars_elemancy"), elemancerFocus))
            .withTextPage("ars_elemancy.page.getting_started.two") // remove if not needed
        ).withRelation(elemancerFocusPage);

        addElemancyItem(ModItems.TEMPEST_FOCUS.get(), "tempest_focus", gettingStartedPage);
        addElemancyItem(ModItems.SILT_FOCUS.get(), "silt_focus", gettingStartedPage);
        addElemancyItem(ModItems.MIRE_FOCUS.get(), "mire_focus", gettingStartedPage);
        addElemancyItem(ModItems.LAVA_FOCUS.get(), "lava_focus", gettingStartedPage);
        addElemancyItem(ModItems.CINDER_FOCUS.get(), "cinder_focus", gettingStartedPage);
        addElemancyItem(ModItems.VAPOR_FOCUS.get(), "vapor_focus", gettingStartedPage);
        addElemancyItem(elemancerFocus, "elemancer_focus", gettingStartedPage);
        addElemancyItem(ModItems.TEMPEST_ARMOR.get(), "tempest_armor", gettingStartedPage);
        addElemancyItem(ModItems.MIRE_ARMOR.get(), "mire_armor", gettingStartedPage);
        addElemancyItem(ModItems.SILT_ARMOR.get(), "silt_armor", gettingStartedPage);
        addElemancyItem(ModItems.LAVA_ARMOR.get(), "lava_armor", gettingStartedPage);
        addElemancyItem(ModItems.VAPOR_ARMOR.get(), "vapor_armor", gettingStartedPage);
        addElemancyItem(ModItems.CINDER_ARMOR.get(), "cinder_armor", gettingStartedPage);
        addElemancyItem(ModItems.ELEMANCER_ARMOR.get(), "elemancer_armor", gettingStartedPage);
    }

    public static DocEntry addElemancyItem (ItemLike item, String localizationSlug, DocEntry relatedPage)
    {
        return addPage(new DocEntryBuilder(DocumentationRegistry.ITEMS, item)
            .withIcon(item)
            .withIntroPageNoIncrement("ars_elemancy.page.".concat(localizationSlug))
            .withCraftingPages(item))
            .withRelation(relatedPage)
    }

    public static void editPages (ReloadDocumentationEvent.Post ignored)
    {
        DocEntry baseEntry = DocumentationRegistry.getEntry(ModItems.ELEMANCER_FOCUS.getResourceLocation());
        if (baseEntry != null)
        {
            // here is where we put entries which depend on registries to have been resolved
        }
    }
}
