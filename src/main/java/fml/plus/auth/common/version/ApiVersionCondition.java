package fml.plus.auth.common.version;

import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    private final WebMvcConfiguration.ApiVersionProperties properties;
    private final String version;


    public ApiVersionCondition(ApiControl apiControl, WebMvcConfiguration.ApiVersionProperties properties) {
        this(apiControl == null ? properties.getDefaultVersion() : apiControl.version(), properties);
    }

    private ApiVersionCondition(String version, WebMvcConfiguration.ApiVersionProperties properties) {
        this.properties = properties;
        this.version = version;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition apiVersionCondition) {
        return new ApiVersionCondition(apiVersionCondition.version, properties);
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest httpServletRequest) {
        String version = httpServletRequest.getHeader(properties.getHeader());
        version = Strings.isNullOrEmpty(version) ? properties.getDefaultVersion() : version;

        if (!validate(version) || compare(version, this.version) < 0) {
            return null;
        } else {
            return this;
        }
    }

    @Override
    public int compareTo(ApiVersionCondition apiVersionCondition, HttpServletRequest httpServletRequest) {
        return compare(apiVersionCondition.version, this.version);
    }

    private boolean validate(String version) {
        String[] arr = version.split(properties.getSeparator());
        for (String s : arr) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private int compare(String version1, String version2) {
        String[] arr1 = version1.split(properties.getSeparator());
        String[] arr2 = version2.split(properties.getSeparator());

        int[] versionArr1 = convertAndFill(arr1, Integer.max(arr1.length, arr2.length));
        int[] versionArr2 = convertAndFill(arr2, Integer.max(arr1.length, arr2.length));

        for (int i = 0; i < versionArr1.length; i++) {
            int sub = versionArr1[i] - versionArr2[i];
            if (sub != 0) {
                return sub;
            }
        }
        return 0;
    }

    private int[] convertAndFill(String[] strings, int fillLength) {
        int[] arr = new int[fillLength];
        for (int i = 0; i < fillLength; i++) {
            if (i < strings.length) {
                arr[i] = Integer.parseInt(strings[i]);
            } else {
                arr[i] = 0;
            }
        }
        return arr;
    }
}
