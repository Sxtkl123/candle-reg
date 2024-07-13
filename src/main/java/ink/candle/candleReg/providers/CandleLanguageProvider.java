package ink.candle.candleReg.providers;

import com.google.gson.JsonObject;
import ink.candle.candleReg.CandleReg;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CandleLanguageProvider implements DataProvider {

    private final PackOutput output;
    private final String modId;

    /**
     * This is a map that contains data like below:
     * <br/>
     * <code>
     *     {
     *         "en_us": {
     *             "item.example_item.name": "Example Item",
     *             "block.example_block.name": "Example Block"
     *         },
     *         "zh_cn": {
     *             "item.example_item.name": "示例物品",
     *             "block.example_block.name": "示例方块"
     *         }
     *     }
     * </code>
     */
    private final Map<String, Map<String, String>> data = new HashMap<>();

    public CandleLanguageProvider(PackOutput output, String modId) {
        this.output = output;
        this.modId = modId;
    }

    @NotNull
    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cache) {

        List<CompletableFuture<?>> futures = new ArrayList<>();

        if (!data.isEmpty()) {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                Path target = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modId).resolve("lang").resolve(entry.getKey() + ".json");
                entry.getValue().forEach(json::addProperty);
                futures.add(DataProvider.saveStable(cache, json, target));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));
    }

    @NotNull
    @Override
    public String getName() {
        return "Candle Language";
    }

    public void add(Object src, String locale, String name) {
        if (!data.containsKey(locale)) {
            data.put(locale, new TreeMap<>());
        }
        Map<String, String> trans = data.get(locale);
        if (src instanceof String) {
            trans.put((String) src, name);
        } else if (src instanceof Block) {
            trans.put(((Block) src).getDescriptionId(), name);
        } else if (src instanceof Item) {
            trans.put(((Item) src).getDescriptionId(), name);
        } else {
            if (trans.isEmpty()) {
                data.remove(locale);
                CandleReg.LOGGER.error("No translation found for {}", src);
            }
        }
    }
}
