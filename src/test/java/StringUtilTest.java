import ink.candle.candleReg.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtilTest {

    private static final Logger log = LoggerFactory.getLogger(StringUtilTest.class);

    public static void main(String[] args) {
        String str = "ExampleBlockTest";
        log.info(StringUtil.toSnakeCase(str));
    }

}
