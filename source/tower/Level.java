package tower;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Level {
	
	private Game gameInstance;
	private ArrayList<Object> Assets=new ArrayList<Object>();
	
	private Color player=null;
	
	public Level(String file) {
		 try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	            String line;
	            //first line: player
	            player=parseColor(br.readLine());
	            while ((line = br.readLine()) != null) {
	                // Split the line into class name and constructor parameters
	                String[] parts = line.split("\\s+");
	                String className = parts[0];
	                String[] parameters = new String[parts.length - 1];
	                System.arraycopy(parts, 1, parameters, 0, parts.length - 1);

	                // Dynamically create an instance of the specified class
	                Object instance = createInstance("tower."+className, parameters);
	                if (instance != null) {
	                    Assets.add(instance);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

	
	public void loadLevel() {
		gameInstance.clearAssets();
		gameInstance.addObjects(Assets);
		gameInstance.player=player;
	}
	
	private static Object createInstance(String className, String[] parameters) {
        try {
            // Get the Class object for the specified class name
            Class<?> clazz = Class.forName(className);

            // Find the appropriate constructor
            Constructor<?>[] constructors = clazz.getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == parameters.length) {
                    // Convert the string parameters to the appropriate types
                    Object[] convertedParameters = new Object[parameters.length];
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    for (int i = 0; i < parameters.length; i++) {
                        convertedParameters[i] = convertParameter(parameters[i], parameterTypes[i]);
                    }
                    return constructor.newInstance(convertedParameters);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	 private static Object convertParameter(String parameter, Class<?> type) {
	        if (type == int.class || type == Integer.class)
	            return Integer.parseInt(parameter);
	        else if (type == double.class || type == Double.class)
	            return Double.parseDouble(parameter);
	        else if (type == String.class)
	            return parameter;
	        else if (type == Color.class)
	            return parseColor(parameter);
	        // Add more type conversions as needed
	        throw new IllegalArgumentException("Unsupported parameter type: " + type.getName());
	    }

	    private static Color parseColor(String colorString) {
	    	 try {
	             // Check if the colorString matches any public static fields in the Color class
	             Field[] fields = Color.class.getFields();
	             for (Field field : fields)
	                 if (field.getType() == Color.class && field.getName().equalsIgnoreCase(colorString))
	                     return (Color) field.get(null);
	             // If not a predefined color, assume it is an RGB hex code
	             if (colorString.startsWith("#"))
	                 return Color.decode(colorString);
	             else
	            	 return null;
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
	         throw new IllegalArgumentException("Unsupported color format: " + colorString);
	    }


	public Game getGameInstance() {
		return gameInstance;
	}


	public void setGameInstance(Game gameInstance) {
		this.gameInstance = gameInstance;
	}
}
