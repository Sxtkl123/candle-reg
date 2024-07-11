package ink.candle.candleReg.annotations.register;

import ink.candle.candleReg.CandleReg;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import ink.candle.candleReg.examples.ExampleItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterProcessor {

    private static final Type REGISTER = Type.getType(Register.class);

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getAnnotations)
                    .flatMap(Collection::stream)
                    .filter(a -> REGISTER.equals(a.annotationType()))
                    .toList();
            CandleReg.LOGGER.info("Start Register Items...");
            for (ModFileScanData.AnnotationData annotation : annotations) {
                Class<?> clazz;
                Map<String, Object> params = annotation.annotationData();
                String modId = (String) params.get("value");
                String name = (String) params.get("name");
                ModAnnotation.EnumHolder type = (ModAnnotation.EnumHolder) params.get("type");
                if (!TypeEnum.ITEM.toString().equals(type.getValue())) {
                    continue;
                }
                try {
                    clazz = Class.forName(annotation.clazz().getClassName());
                } catch (ClassNotFoundException e) {
                    CandleReg.LOGGER.error("Failed to load register annotation: {}", annotation.clazz(), e);
                    continue;
                }

                try {
                    Object object = clazz.getDeclaredConstructor().newInstance();
                    helper.register(new ResourceLocation(modId, name), (Item) object);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    CandleReg.LOGGER.error("Failed to instance class: {}", clazz.getName(), e);
                }
            }
        });
    }
}
