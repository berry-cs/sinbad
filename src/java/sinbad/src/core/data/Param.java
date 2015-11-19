/**
 * 
 */
package core.data;

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
	
}
