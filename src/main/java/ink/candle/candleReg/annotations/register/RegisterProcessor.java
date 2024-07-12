package ink.candle.candleReg.annotations.register;

import ink.candle.candleReg.CandleReg;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterProcessor {

    public static final Map<ResourceLocation, Block> BLOCKS = new HashMap<>();

    public static final Map<ResourceLocation, Item> ITEMS = new HashMap<>();

    private static final Type REGISTER = Type.getType(Register.class);

    @SubscribeEvent
    public static void registerEvent(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS, helper -> register(TypeEnum.BLOCK, helper, BLOCKS));
        event.register(ForgeRegistries.Keys.ITEMS, helper -> register(TypeEnum.ITEM, helper, ITEMS));
    }

    @SuppressWarnings("unchecked")
    private static <T> void register(TypeEnum typeEnum, RegisterEvent.RegisterHelper<T> helper, Map<ResourceLocation, T> map) {
        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> REGISTER.equals(a.annotationType()))
                .toList();
        for (ModFileScanData.AnnotationData annotation : annotations) {
            Class<T> clazz;
            Map<String, Object> params = annotation.annotationData();
            String modId = (String) params.get("value");
            String name = (String) params.get("name");
            ModAnnotation.EnumHolder type = (ModAnnotation.EnumHolder) params.get("type");
            if (!typeEnum.toString().equals(type.getValue())) {
                continue;
            }
            try {
                clazz = (Class<T>) Class.forName(annotation.clazz().getClassName());
            } catch (ClassNotFoundException e) {
                CandleReg.LOGGER.error("Failed to load register annotation: {}", annotation.clazz(), e);
                continue;
            }

            try {
                T object = clazz.getDeclaredConstructor().newInstance();
                ResourceLocation location = new ResourceLocation(modId, name);
                helper.register(location, object);
                map.put(location, object);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                CandleReg.LOGGER.error("Failed to instance class: {}", clazz.getName(), e);
            }
        }
    }

}
