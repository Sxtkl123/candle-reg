package ink.candle.candleReg.annotations.registerItem;

import ink.candle.candleReg.CandleReg;
import ink.candle.candleReg.annotations.register.Register;
import ink.candle.candleReg.annotations.register.RegisterProcessor;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import ink.candle.candleReg.utils.StringUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.ObjectUtils;
import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterBlockItemProcessor {

    private static final Type REGISTER_BLOCK_ITEM = Type.getType(RegisterBlockItem.class);
    private static final Type REGISTER = Type.getType(Register.class);

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerEvent(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getAnnotations)
                    .flatMap(Collection::stream)
                    .filter(a -> REGISTER_BLOCK_ITEM.equals(a.annotationType()))
                    .toList();
            List<ModFileScanData.AnnotationData> registerAnnotations = ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getAnnotations)
                    .flatMap(Collection::stream)
                    .filter(a -> REGISTER.equals(a.annotationType()))
                    .toList();
            for (ModFileScanData.AnnotationData annotation : annotations) {
                Class<?> clazz;
                try {
                    clazz = Class.forName(annotation.clazz().getClassName());
                } catch (ClassNotFoundException e) {
                    CandleReg.LOGGER.error("Failed to load register annotation: {}", annotation.clazz(), e);
                    continue;
                }
                String name = null;
                String modId = null;

                for (ModFileScanData.AnnotationData register : registerAnnotations) {
                    if (register.clazz().equals(annotation.clazz())) {
                        Map<String, Object> params = register.annotationData();
                        modId = (String) params.get("value");
                        name = (String) params.get("name");
                        if (ObjectUtils.isEmpty(name)) {
                            name = StringUtil.toSnakeCase(clazz.getSimpleName());
                        }
                        ModAnnotation.EnumHolder type = (ModAnnotation.EnumHolder) params.get("type");
                        if (!TypeEnum.BLOCK.toString().equals(type.getValue())) {
                            break;
                        }
                    }
                }

                if (name == null || modId == null) {
                    continue;
                }
                try {
                    helper.register(new ResourceLocation(modId, name), new BlockItem(RegisterProcessor.BLOCKS.get(new ResourceLocation(modId, name)), new Item.Properties()));
                } catch (Exception e) {
                    CandleReg.LOGGER.error("Failed to instance class: {}", clazz.getName(), e);
                }
            }
        });
    }

}
