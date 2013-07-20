package samoht2401.universalwire.system;

public enum ItemType {
	cable, generator, consumer;
	
	public static ItemType get(int ordinal)
    {
        if(ordinal == 1)
        	return generator;
        if(ordinal == 2)
        	return consumer;
        return cable;
    }
}
