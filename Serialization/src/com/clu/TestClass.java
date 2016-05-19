package com.clu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

	static class Clazz{
		
		private String name;
		private String path;
		private int width, height;
		private List<Entity> entities = new ArrayList<Entity>();
		
		private Clazz(){			
		}
		
		public Clazz(String path){
			this.name = "some test value";
			this.path = path;
			width = 64;
			height = 128;
		}
		
		public void add(Entity e){
			e.init(this);
			entities.add(e);
			
		}
		
		public void update(){
			
		}
		
		public void render(){
			
		}
		
		public void save(String path){
			
			SRDatabase database = new SRDatabase(name);
			SRObject object = new SRObject("TestSerialize");
			object.addString(SRString.Create("name", name));
			object.addString(SRString.Create("path", this.path));
			object.addField(SRField.Integer("width", width));
			object.addField(SRField.Integer("height", height));
			object.addField(SRField.Integer("entityCount", entities.size()));
			database.addObject(object);
			for (int i = 0; i < entities.size(); i++){
				Entity e = entities.get(i);
				
				SRObject entity = new SRObject("E" + i);
				byte type = 0;
				if (e instanceof ChildEntity)
					type = 1;
				entity.addField(SRField.Byte("type", type));
				entity.addField(SRField.Integer("arrayIndex", i));
				e.serialize(entity);			
				database.addObject(entity);
			}		
			
			database.serializeToFile(path);
		}
		
		public static Clazz load(String path)throws IOException{
			SRDatabase database = SRDatabase.DeserializeFromFile(path);
			SRObject levelData = database.findObject("TestSerialize");
			
			Clazz result = new Clazz();
			result.name = levelData.findString("name").getString();
			result.path = levelData.findString("path").getString();
			result.width = levelData.findField("width").getInt();
			result.height = levelData.findField("height").getInt();
			int entityCount = levelData.findField("entityCount").getInt();
			
			for(int i =0; i < entityCount; i++){
				SRObject entity = database.findObject("E" + i);
				Entity e;
				if (entity.findField("type").getByte() == 1)
					e = ChildEntity.deserialize(entity);
				else
					e = Entity.deserialize(entity);
				result.add(e);
			}
			return result;
		}				
	}
	
	static class Entity{

		protected int x, y;
		protected boolean removed = false;
		protected Clazz clazz;
		
		public Entity(){
			x = 0;
			y = 0;
		}
		
		public void init(Clazz clazz){
			this.clazz = clazz;
		}
		
		public void serialize(SRObject object){
			object.addField(SRField.Integer("x", x));
			object.addField(SRField.Integer("y", y));
			object.addField(SRField.Boolean("removed", removed));
		}
		
		public static Entity deserialize(SRObject object){
			Entity result = new Entity();
			result.x = object.findField("x").getInt();
			result.y = object.findField("y").getInt();
			result.removed = object.findField("removed").getBoolean();
			return result;
		}
		
	}
	
	static class ChildEntity extends Entity{
		
		private String name;
		private char value;
		
		private ChildEntity(){
			
		}
		
		public ChildEntity(String name, int x, int y, char value){
			this.x = x;
			this.y = y;
			
			this.name = name;
			this.value = value;
		}
		
		public void serialize(SRObject object){
			super.serialize(object);
			object.addString(SRString.Create("name", name));
			object.addField(SRField.Char("value", value));
		}
		
		public static ChildEntity deserialize(SRObject object){
			Entity e = Entity.deserialize(object);
			ChildEntity result = new ChildEntity();
			result.x = e.x;
			result.y = e.y;
			result.removed = e.removed;
			
			result.name = object.findString("name").getString();
			result.value = object.findField("value").getChar();
			
			return result;
		}
		
	}
	
	
	
	public void serializeTest(){
			Entity emptyE = new Entity();
			System.out.println("Field of emptyE: x - " + emptyE.x + ", y - " + emptyE.y +
					", removed - " + emptyE.removed);
			ChildEntity cE = new ChildEntity("Test name of childEntity", 40 ,28 , 'J');
			
			System.out.println("Field of childEntityt: x - " + cE.x + ", y - " + cE.y +
					", removed - " + cE.removed + ", name - " + cE.name + ", value - " + cE.value);
			
			Clazz clazz = new Clazz("res/some/path/to/png");
			System.out.println("Field of clazz: name - " + clazz.name + ", path - " + clazz.path +
					", width - " + clazz.width + ", height - " + clazz.height + ", entities - " + clazz.entities.size());

			System.out.println("Insert emptyE and cE into clazz object");
			clazz.add(emptyE);
			clazz.add(cE);	
			System.out.println("entities - " + clazz.entities.size());
			System.out.println("Save file clazz.srlz . This file can be open with some redactors (e.x HxD).");
			clazz.save("clazz.srlz");
	}
	
	public void deserializeTest(){
		Clazz clazz;
		try{
			clazz = Clazz.load("clazz.srlz");
		}
		catch (Exception ex){
			System.out.println("File not found. Try to serialize first.");
			return;
		}
			System.out.println("Result of deserialization:");
			System.out.println("Field of clazz: name - " + clazz.name + ", path - " + clazz.path +
					", width - " + clazz.width + ", height - " + clazz.height + ", entities - " + clazz.entities.size());
			//in case that we know the order of objects, we can use just index to get object
			Entity emptyE = clazz.entities.get(0);
			System.out.println("Field of emptyE: x - " + emptyE.x + ", y - " + emptyE.y +
					", removed - " + emptyE.removed);
			ChildEntity cE = (ChildEntity) clazz.entities.get(1);
			System.out.println("Field of childEntityt: x - " + cE.x + ", y - " + cE.y +
					", removed - " + cE.removed + ", name - " + cE.name + ", value - " + cE.value);
			
	}
}
