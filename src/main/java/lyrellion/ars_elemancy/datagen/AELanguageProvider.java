package lyrellion.ars_elemancy.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class AELanguageProvider extends LanguageProvider
{
    public AELanguageProvider (PackOutput output, String modid, String locale)
    {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations ()
    {
        add("itemGroup.ars_elemancy", "Ars Elemancy");

        add("ars_elemancy.page.getting_started.title", "Ars Elemancy");
        add("ars_elemancy.page.getting_started.one",
            "words and words" +
            "and more words" +
            "words."
        );
        add("ars_elemancy.page.getting_started.two",
            "words and words" +
            "and more words" +
            "words."
        );

        add("ars_elemancy.page.tempest_focus", "describe tempest_focus");
        add("ars_elemancy.page.silt_focus", "describe silt_focus");
        add("ars_elemancy.page.mire_focus", "describe mire_focus");
        add("ars_elemancy.page.lava_focus", "describe lava_focus");
        add("ars_elemancy.page.cinder_focus", "describe cinder_focus");
        add("ars_elemancy.page.vapor_focus", "describe vapor_focus");
        add("ars_elemancy.page.elemancer_focus", "describe elemancer_focus");
        add("ars_elemancy.page.tempest_armor", "describe tempest_armor");
        add("ars_elemancy.page.mire_armor", "describe mire_armor");
        add("ars_elemancy.page.silt_armor", "describe silt_armor");
        add("ars_elemancy.page.lava_armor", "describe lava_armor");
        add("ars_elemancy.page.vapor_armor", "describe vapor_armor");
        add("ars_elemancy.page.cinder_armor", "describe cinder_armor");
        add("ars_elemancy.page.elemancer_armor", "describe elemancer_armor");
    }
}
