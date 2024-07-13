package ink.candle.candleReg.utils;

import ink.candle.candleReg.CandleReg;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ProcessorUtil {

    public static Class<?> readClass(Type type) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(type.getClassName());
        } catch (ClassNotFoundException e) {
            CandleReg.LOGGER.error("Failed to load register annotation: {}", type, e);
        }
        return clazz;
    }

    public static Object instance(Class<?> clazz) {
        Object instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            CandleReg.LOGGER.error("Failed to instance class: {}", clazz.getName(), e);
        }
        return instance;
    }

    public static Map<String, Object> readRegisterParam(Type type, List<ModFileScanData.AnnotationData> data) {
        for (ModFileScanData.AnnotationData annotationData : data) {
            if (annotationData.clazz().equals(type)) {
                return annotationData.annotationData();
            }
        }

        return null;
    }

    public static List<ModFileScanData.AnnotationData> readAnnotation(Type annotation) {
        return ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> annotation.equals(a.annotationType()))
                .toList();
    }

}
