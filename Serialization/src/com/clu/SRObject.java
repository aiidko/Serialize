package com.clu;

import static com.clu.SerializationUtils.*;

import java.util.ArrayList;
import java.util.List;

public class SRObject extends SRBase{

	public static final byte CONTAINER_TYPE = ContainerType.OBJECT; 
	private short fieldCount;	
	public List<SRField> fields = new ArrayList<SRField>();
	private short stringCount;	
	public List<SRString> strings = new ArrayList<SRString>();
	private short arrayCount;
	public List<SRArray> arrays= new ArrayList<SRArray>();

	private SRObject(){	
	}
	
	public SRObject(String name){
		size += 1 + 2 + 2 + 2;
		setName(name);
	}
	
	public void addField(SRField field){
		fields.add(field);
		size += field.getSize();
		
		fieldCount = (short)fields.size();
	}
	
	public void addString(SRString string){
		strings.add(string);
		size += string.getSize();
		
		stringCount = (short)strings.size();
	}
	
	public void addArray(SRArray array){
		arrays.add(array);
		size += array.getSize();
		
		arrayCount = (short)arrays.size();
	}
	
	public int getSize(){
		return size;
	}
	
	public SRField findField(String name){
		for (SRField field : fields){
			if(field.getName().equals(name))
				return field;
		}
		return null;
	}
	
	public SRArray findArray(String name){
		for (SRArray array : arrays){
			if(array.getName().equals(name))
				return array;
		}
		return null;
	}
	
	public SRString findString(String name){
		for (SRString string : strings){
			if(string.getName().equals(name))
				return string;
		}
		return null;
	}

	public int getBytes(byte[] dest, int pointer) {
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		
		pointer = writeBytes(dest, pointer, fieldCount);		
		for (SRField field : fields)
			pointer = field.getBytes(dest, pointer);
		
		pointer = writeBytes(dest, pointer, stringCount);
		for (SRString string : strings)
			pointer = string.getBytes(dest, pointer);
				
		pointer = writeBytes(dest, pointer, arrayCount);
		for (SRArray array : arrays)
			pointer = array.getBytes(dest, pointer);
		return pointer;
	}
	
	public static SRObject Deserialize(byte[] data, int pointer){
		byte containerType = data[pointer++]; 
		assert(containerType == CONTAINER_TYPE);
		
		SRObject result = new SRObject();
		result.nameLength = readShort(data, pointer);
		pointer += 2;
		result.name = readString(data, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;
		
		result.size = readInt(data, pointer);
		pointer +=4;
		
		//pointer += result.size - sizeOffset - result.nameLength;


		result.fieldCount = readShort(data, pointer);
		pointer +=2;
		
		for (int i = 0 ; i < result.fieldCount; i++){
			SRField field = SRField.Deserialize(data, pointer);
			result.fields.add(field);
			pointer += field.getSize();
		}

		result.stringCount = readShort(data, pointer);
		pointer +=2;
		
		for (int i = 0 ; i < result.stringCount; i++){
			SRString string = SRString.Deserialize(data, pointer);
			result.strings.add(string);
			pointer += string.getSize();
		}
		

		result.arrayCount = readShort(data, pointer);
		pointer +=2;
		
		for (int i = 0 ; i < result.arrayCount; i++){
			SRArray array = SRArray.Deserialize(data, pointer);
			result.arrays.add(array);
			pointer += array.getSize();
		}
		
		return result;
	}
	
}
