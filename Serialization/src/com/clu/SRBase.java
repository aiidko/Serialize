package com.clu;

public abstract class SRBase {
	
	protected short nameLength;
	protected byte[] name;
	
	protected int size = 2 + 4;
	
	public void setName(String name){
		assert(name.length() < Short.MAX_VALUE);
		
		if(this.name != null)
			size -= this.name.length;
		
		nameLength = (short)name.length();
		this.name = name.getBytes();
		size += nameLength;
	}
	
	public String getName(){
		return new String(name, 0, nameLength);
	}
	public abstract int getSize();
}
