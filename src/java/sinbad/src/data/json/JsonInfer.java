package data.json;

import java.util.HashMap;
import java.util.Map;

import core.infer.IDataFormatInfer;

public class JsonInfer implements IDataFormatInfer {

    @Override
    public boolean matchedBy(String path) {
        return path.toLowerCase().contains(".json")
                || path.toLowerCase().contains("=json")
                || path.toLowerCase().endsWith("json")
                || path.toLowerCase().endsWith(".json.gz")
                || path.toLowerCase().endsWith(".json.zip");
    }

    @Override
    public Map<String, String> getOptions() {
        return new HashMap<String,String>();
    }

}
