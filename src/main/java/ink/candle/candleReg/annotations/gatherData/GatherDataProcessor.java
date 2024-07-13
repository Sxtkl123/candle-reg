package ink.candle.candleReg.annotations.gatherData;

import ink.candle.candleReg.CandleReg;
import ink.candle.candleReg.annotations.register.Register;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import ink.candle.candleReg.interfaces.ICandleLanguage;
import ink.candle.candleReg.providers.CandleLanguageProvider;
import ink.candle.candleReg.utils.StringUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.ObjectUtils;
import org.objectweb.asm.Type;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GatherDataProcessor {

    private static final Map<String, List<LanguageProvider>> LANGUAGE_PROVIDERS = new HashMap<>();

    private static final Type GATHER_DATA = Type.getType(GatherData.class);
    private static final Type REGISTER = Type.getType(Register.class);

    @SubscribeEvent
    public static void registerEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();

        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> GATHER_DATA.equals(a.annotationType()))
                .toList();
        List<ModFileScanData.AnnotationData> registerAnnotations = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> REGISTER.equals(a.annotationType()))
                .toList();
    }
}
