package ink.candle.candleReg.examples;

import ink.candle.candleReg.CandleReg;
import ink.candle.candleReg.annotations.gatherData.GatherData;
import ink.candle.candleReg.annotations.register.Register;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import ink.candle.candleReg.interfaces.ICandleLanguage;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

@Register(value = CandleReg.MODID, type = TypeEnum.ITEM)
@GatherData
public class ExampleItem extends Item implements ICandleLanguage {

    public ExampleItem() {
        super(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build()));
    }

    @Override
    public Map<String, String> getTranslations() {
        Map<String, String> ret = new HashMap<>();
        ret.put("en_us", "example item");
        return ret;
    }
}
