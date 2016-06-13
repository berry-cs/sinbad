/**
 * 
 */
package core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nadeem Abdul Hamid
 *
 */
public class Param {
	private String key;
	private String description;
	private ParamType type;
	private boolean required; 

	public Param(String key, ParamType type) {
		this(key, null, type);
	}
	
	public Param(String key, String description, ParamType type) {
		this(key, description, type, false);
	}
	
	public Param(String key, String description, ParamType type,
			boolean required) {
		this.key = key;
		this.description = description;
		this.type = type;
		this.required = required;
	}

	/**
	 * Import information to construct a param from the given map
	 */
	public Param(Map<String, String> pm) {
        this(pm.get("key"), ParamType.fromString(pm.get("type")));
        this.description = pm.getOrDefault("description", null);
        this.required = pm.containsKey("required") && pm.get("required").equals("true");
    }

    /**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the type
	 */
	public ParamType getType() {
		return type;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Param) {
            Param that = (Param) other;
            result = (this.key.equals(that.key) && this.type.equals(that.type));
        }
        return result;
    }

    public int hashCode() {
        return (41 * (this.key.hashCode() + this.type.hashCode()));
    }

    /**
     * Exports a representation of this object's data
     * @value a value for this parameter, or null if not set
     * @return
     */
    public Map<String, String> export(String value) {
        Map<String, String> m = new HashMap<String, String>();
        
        m.put("key", this.key);
        if (this.type == ParamType.PATH) m.put("type", "path"); 
        else if (this.type == ParamType.QUERY) m.put("type", "query");
        
        if (this.required) m.put("required", "true");
        if (this.description != null) m.put("description", this.description);
        if (value != null) m.put("value",  value);
        
        return m;
    }
	
}
