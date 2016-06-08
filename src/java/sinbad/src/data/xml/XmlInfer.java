package data.xml;

import java.util.HashMap;
import java.util.Map;

import core.infer.IDataFormatInfer;

public class XmlInfer implements IDataFormatInfer {

    @Override
    public boolean matchedBy(String path) {
        return path.toLowerCase().contains(".xml")
                || path.toLowerCase().contains("=xml")
                || path.toLowerCase().endsWith(".xml.gz")
                || path.toLowerCase().endsWith(".xml.zip");
    }

    @Override
    public Map<String, String> getOptions() {
        return new HashMap<String,String>();
    }

}
