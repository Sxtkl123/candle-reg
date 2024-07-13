package ink.candle.candleReg.examples;

import ink.candle.candleReg.CandleReg;
import ink.candle.candleReg.annotations.register.Register;
import ink.candle.candleReg.annotations.register.enums.TypeEnum;
import ink.candle.candleReg.annotations.registerItem.RegisterBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

@Register(value = CandleReg.MODID, type = TypeEnum.BLOCK)
@RegisterBlockItem
public class ExampleBlock extends Block {

    public ExampleBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    }
}
