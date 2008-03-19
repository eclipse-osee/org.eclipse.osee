/*
 * Created on Mar 19, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.type;

import java.io.InputStream;
import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
public interface IVariantData {

   public String get(String key);

   public String[] getArray(String key);

   public boolean getBoolean(String key);

   public double getDouble(String key) throws NumberFormatException;

   public float getFloat(String key) throws NumberFormatException;

   public int getInt(String key) throws NumberFormatException;

   public long getLong(String key) throws NumberFormatException;

   public Date getDate(String key) throws Exception;

   public String getStreamAsString(String key) throws Exception;

   public InputStream getStream(String key) throws IllegalArgumentException;

   public void put(String key, String[] value);

   public void put(String key, double value);

   public void put(String key, float value);

   public void put(String key, int value);

   public void put(String key, long value);

   public void put(String key, String value);

   public void put(String key, boolean value);

   public void put(String key, Date date);

   public void put(String key, byte[] bytes);
}
