package ink.candle.candleReg.examples;

import ink.candle.candleReg.CandleReg;
import ink.candle.candleReg.annotations.register.Register;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

@Register(value = CandleReg.MODID, name = "example_item", type = TypeEnum.ITEM)
public class ExampleItem extends Item {

    public ExampleItem() {
        super(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build()));
    }
}
