package core.sig;

/**
 * Represents a name/signature pair of a constructor represented by
 * a CompSig. 
 */
public class ArgSpec { 
    String name;
    ISig type;
    
    public ArgSpec(String name, ISig type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ISig getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ArgSpec))
            return false;
        ArgSpec other = (ArgSpec) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}

