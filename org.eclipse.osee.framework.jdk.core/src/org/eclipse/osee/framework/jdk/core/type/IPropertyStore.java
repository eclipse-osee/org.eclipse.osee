/*
 * Created on Feb 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.type;

import java.io.IOException;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public interface IPropertyStore {

   /**
    * Returns the value of the given key .
    * 
    * @param key the key
    * @return the value, or <code>null</code> if none
    */
   public String get(String key);

   /**
    * Returns the value, an array of strings, of the given key.
    * 
    * @param key the key
    * @return the array of string, or <code>null</code> if none
    */
   public String[] getArray(String key);

   /**
    * Convert the value of the given key to a boolean and return it.
    * 
    * @param key the key
    * @return the boolean value, or <code>false</code> if none
    */
   public boolean getBoolean(String key);

   /**
    * Convert the value of the given key to a double and return it.
    * 
    * @param key the key
    * @return the value converted to double, or throws <code>NumberFormatException</code> if none
    * @exception NumberFormatException if the string value does not contain a parsable number.
    * @see java.lang.Double#valueOf(java.lang.String)
    */
   public double getDouble(String key) throws NumberFormatException;

   /**
    * Convert the value of the given key to a float and return it.
    * 
    * @param key the key
    * @return the value converted to float, or throws <code>NumberFormatException</code> if none
    * @exception NumberFormatException if the string value does not contain a parsable number.
    * @see java.lang.Float#valueOf(java.lang.String)
    */
   public float getFloat(String key) throws NumberFormatException;

   /**
    * Convert the value of the given key to a int and return it.
    * 
    * @param key the key
    * @return the value converted to int, or throws <code>NumberFormatException</code> if none
    * @exception NumberFormatException if the string value does not contain a parsable number.
    * @see java.lang.Integer#valueOf(java.lang.String)
    */
   public int getInt(String key) throws NumberFormatException;

   /**
    * Convert the value of the given key to a long and return it.
    * 
    * @param key the key
    * @return the value converted to long, or throws <code>NumberFormatException</code> if none
    * @exception NumberFormatException if the string value does not contain a parsable number.
    * @see java.lang.Long#valueOf(java.lang.String)
    */
   public long getLong(String key) throws NumberFormatException;

   /**
    * Adds the pair <code>key/value</code>.
    * 
    * @param key the key.
    * @param value the value to be associated with the <code>key</code>
    */
   public void put(String key, String[] value);

   /**
    * Converts the double <code>value</code> to a string and adds the pair <code>key/value</code>.
    * 
    * @param key the key.
    * @param value the value to be associated with the <code>key</code>
    */
   public void put(String key, double value);

   /**
    * Converts the float <code>value</code> to a string and adds the pair <code>key/value</code>.
    * 
    * @param key the key.
    * @param value the value to be associated with the <code>key</code>
    */
   public void put(String key, float value);

   /**
    * Converts the integer <code>value</code> to a string and adds the pair <code>key/value</code>.
    * 
    * @param key the key.
    * @param value the value to be associated with the <code>key</code>
    */
   public void put(String key, int value);

   /**
    * Converts the long <code>value</code> to a string and adds the pair <code>key/value</code>.
    * 
    * @param key the key.
    * @param value the value to be associated with the <code>key</code>
    */
   public void put(String key, long value);

   /**
    * Adds the pair <code>key/value</code>.
    * 
    * @param key the key.
    * @param value the value to be associated with the <code>key</code>
    */
   public void put(String key, String value);

   /**
    * Converts the boolean <code>value</code> to a string and adds the pair <code>key/value</code>.
    * 
    * @param key the key.
    * @param value the value to be associated with the <code>key</code>
    */
   public void put(String key, boolean value);

   /**
    * Returns the property store's id
    * 
    * @return The Property Store's id
    */
   public String getId();

   /**
    * Save a property store to a file.
    * 
    * @param fileName the name of the file the property store values are written to.
    * @throws IOException
    */
   public void save(String fileName) throws Exception;

   /**
    * Load a property store from a file and fill the receiver with its content.
    * 
    * @param fileName the name of the file the property store values are read from.
    * @throws IOException
    */
   public void load(String fileName) throws Exception;

   /**
    * Get keys referencing primitive type items
    * 
    * @return primitive type item keys
    */
   public Set<String> keySet();

   /**
    * Get keys referencing arrayItems
    * 
    * @return array item keys
    */
   public Set<String> arrayKeySet();
}
