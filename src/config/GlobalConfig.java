package config;
import static map.MapData.OPAQUE;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GlobalConfig extends CommonConfig {
	public String planetDataPath;
	public String planetDataOutputPath;
	
	public boolean countExistingTiles = true;
	public String planetMaterialsFilePath = null;
	public OreConfig[] oreTemplates;
	public PlanetConfig[] planets;
	public static GlobalConfig loadConfig(String path) {
		Gson gson = new Gson();
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
			return gson.fromJson(new String(encoded, Charset.defaultCharset()), GlobalConfig.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<GlobalConfig> loadConfigs(String path) {
		Gson gson = new Gson();
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
			Type listType = new TypeToken<ArrayList<GlobalConfig>>(){}.getType();
			return gson.fromJson(new String(encoded, Charset.defaultCharset()), listType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not find the specified config file:\n" + path, "File Read Error", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public GlobalConfig() {
		
	}
	
	public String toJSON() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
	
	public void cascadeSettings() {
		//add opacity
		Map<Integer, OreConfig> oreLookup = new HashMap<Integer, OreConfig>();
		for(OreConfig oreTemplate : oreTemplates) {
			oreLookup.put(oreTemplate.id, oreTemplate);
		}
		surfaceHintColour = 0xFF000000 | (surfaceHintColour << 16);
		for(PlanetConfig planetConfig : planets) {
			for(OreConfig ore : planetConfig.ores) {
				if(oreLookup.containsKey(ore.id)) {
					ore.cascadeSettings(oreLookup.get(ore.id));
				}
			}
			planetConfig.cascadeSettings(this);
		}
	}

}