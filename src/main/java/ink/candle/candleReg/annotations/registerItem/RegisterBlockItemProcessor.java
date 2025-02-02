package ink.candle.candleReg.annotations.registerItem;

import ink.candle.candleReg.annotations.register.Register;
import ink.candle.candleReg.annotations.register.RegisterProcessor;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import ink.candle.candleReg.utils.ProcessorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterBlockItemProcessor {

    private static final Type REGISTER_BLOCK_ITEM = Type.getType(RegisterBlockItem.class);
    private static final Type REGISTER = Type.getType(Register.class);

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerEvent(RegisterEvent event) {
        List<ModFileScanData.AnnotationData> annotations = ProcessorUtil.readAnnotation(REGISTER_BLOCK_ITEM);
        List<ModFileScanData.AnnotationData> registerAnnotations = ProcessorUtil.readAnnotation(REGISTER);

        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            for (ModFileScanData.AnnotationData annotation : annotations) {
                Map<String, Object> params = ProcessorUtil.readRegisterParam(annotation.clazz(), registerAnnotations);
                if (params == null) continue;

                String name = (String) params.get("name");
                String modId = (String) params.get("value");
                ModAnnotation.EnumHolder type = (ModAnnotation.EnumHolder) params.get("type");
                if (!TypeEnum.BLOCK.toString().equals(type.getValue())) continue;

                Class<?> clazz = ProcessorUtil.readClass(annotation.clazz());
                if (clazz == null) continue;

                BlockItem item = new BlockItem(RegisterProcessor.BLOCKS.get(new ResourceLocation(modId, name)), new Item.Properties());
                RegisterProcessor.ITEMS.put(new ResourceLocation(modId, name), item);
                helper.register(new ResourceLocation(modId, name), item);
            }
        });
    }

}
