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
}

