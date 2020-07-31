package ar.gob.coronavirus.utils.many;

import java.util.Random;

public class TextUtils {

    public static final String APPLICATION_TOKEN = "new_relic_token";
    public static final String BASE_URL = "http://base.url";
    public static final String ADVICE_URL = "http://static.url";
    public static final String CERTIFICATE_MATCHER = "**.";

    public static SemaforoInfo getInfo() {
        return new SemaforoInfo(new Random().nextInt(9999));
    }

    public static String getSecretAutorizacionv2() {
        return "1234567890123456789012345678901";
    }
}
