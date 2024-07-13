package ink.candle.candleReg.annotations.lang;

import ink.candle.candleReg.annotations.register.Register;
import ink.candle.candleReg.annotations.register.RegisterProcessor;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import ink.candle.candleReg.providers.CandleLanguageProvider;
import ink.candle.candleReg.utils.ProcessorUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LangProcessor {

    private static final Type REGISTER = Type.getType(Register.class);
    private static final Type LANG = Type.getType(Lang.class);

    private static final Map<String, CandleLanguageProvider> providers = new HashMap<>();

    @SubscribeEvent
    public static void gatherLang(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        List<ModFileScanData.AnnotationData> annotations = ProcessorUtil.readAnnotation(LANG);
        List<ModFileScanData.AnnotationData> registerAnnotations = ProcessorUtil.readAnnotation(REGISTER);

        for (ModFileScanData.AnnotationData annotation : annotations) {
            Map<String, Object> params = annotation.annotationData();
            String name = (String) params.get("name");
            String locale = (String) params.get("locale");

            Map<String, Object> registerParams = ProcessorUtil.readRegisterParam(annotation.clazz(), registerAnnotations);
            if (registerParams == null) continue;

            String modId = (String) registerParams.get("value");
            String id = (String) registerParams.get("name");
            ModAnnotation.EnumHolder type = (ModAnnotation.EnumHolder) registerParams.get("type");
            if (!providers.containsKey(modId)) {
                providers.put(modId, new CandleLanguageProvider(output, modId));
            }

            CandleLanguageProvider provider = providers.get(modId);
            if (TypeEnum.ITEM.toString().equals(type.getValue())) {
                provider.add(RegisterProcessor.ITEMS.get(new ResourceLocation(modId, id)), locale, name);
            }
        }

        for (CandleLanguageProvider provider : providers.values()) {
            gen.addProvider(event.includeClient(), provider);
        }

    }

}
