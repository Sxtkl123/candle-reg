package ink.candle.candleReg.annotations.lang;

import ink.candle.candleReg.CandleReg;
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
    @SuppressWarnings("unchecked")
    public static void gatherLang(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        List<ModFileScanData.AnnotationData> annotations = ProcessorUtil.readAnnotation(LANG);
        List<ModFileScanData.AnnotationData> registerAnnotations = ProcessorUtil.readAnnotation(REGISTER);

        for (ModFileScanData.AnnotationData annotation : annotations) {
            Map<String, Object> params = annotation.annotationData();
            List<String> name = (List<String>) params.get("name");
            List<String> locale = (List<String>) params.get("locale");

            if (name.size() != locale.size()) {
                CandleReg.LOGGER.error("Length of name and location is not same in class: {}", annotation.clazz().getClassName());
                continue;
            }

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
                providerAdd(provider, modId, id, name, locale, TypeEnum.ITEM);
            } else if (TypeEnum.BLOCK.toString().equals(type.getValue())) {
                providerAdd(provider, modId, id, name, locale, TypeEnum.BLOCK);
            }
        }

        for (CandleLanguageProvider provider : providers.values()) {
            gen.addProvider(event.includeClient(), provider);
        }

    }

    private static void providerAdd(CandleLanguageProvider provider, String modId, String id, List<String> name, List<String> locale, TypeEnum typeEnum) {
        for (int i = 0; i < name.size(); i++) {
            switch (typeEnum) {
                case ITEM:
                    provider.add(RegisterProcessor.ITEMS.get(new ResourceLocation(modId, id)), locale.get(i), name.get(i));
                    break;
                case BLOCK:
                    provider.add(RegisterProcessor.BLOCKS.get(new ResourceLocation(modId, id)), locale.get(i), name.get(i));
                    break;
            }
        }
    }

}
