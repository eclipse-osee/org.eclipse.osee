/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.plugin.core.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * @author Roberto E. Escobar
 */
public class NonEclipseManifestHeader extends Dictionary<Object, Object> {

   Object[] headers;
   Object[] values;
   int size = 0;

   private NonEclipseManifestHeader(int initialCapacity) {
      headers = new Object[initialCapacity];
      values = new Object[initialCapacity];
   }

   private int getIndex(Object key) {
      boolean stringKey = key instanceof String;
      for (int i = 0; i < size; i++) {
         if (headers[i].equals(key)) {
            return i;
         }
         if (stringKey && headers[i] instanceof String && ((String) headers[i]).equalsIgnoreCase((String) key)) {
            return i;
         }
      }
      return -1;
   }

   @Override
   public Object remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object put(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public synchronized Object get(Object key) {
      int i = -1;
      if ((i = getIndex(key)) != -1) {
         return values[i];
      }
      return null;
   }

   private Object remove(int remove) {
      Object removed = values[remove];
      for (int i = remove; i < size; i++) {
         if (i == headers.length - 1) {
            headers[i] = null;
            values[i] = null;
         } else {
            headers[i] = headers[i + 1];
            values[i] = values[i + 1];
         }
      }
      if (remove < size) {
         size--;
      }
      return removed;
   }

   public synchronized Object set(Object key, Object value) {
      if (key instanceof String) {
         key = ((String) key).intern();
      }
      int i = getIndex(key);
      if (value == null) /* remove */
      {
         if (i != -1) {
            return remove(i);
         }
      } else /* put */
      {
         if (i != -1) {
            throw new IllegalArgumentException("Duplicate key found" + key);
         }
         add(key, value);
      }
      return null;
   }

   private void add(Object header, Object value) {
      if (size == headers.length) {
         // grow the arrays
         Object[] newHeaders = new Object[headers.length + 10];
         Object[] newValues = new Object[values.length + 10];
         System.arraycopy(headers, 0, newHeaders, 0, headers.length);
         System.arraycopy(values, 0, newValues, 0, values.length);
         headers = newHeaders;
         values = newValues;
      }
      headers[size] = header;
      values[size] = value;
      size++;
   }

   public static Dictionary<Object, Object> parseManifest(InputStream in) throws Exception {
      try {
         NonEclipseManifestHeader headers = new NonEclipseManifestHeader(10);
         BufferedReader br;
         try {
            br = new BufferedReader(new InputStreamReader(in, "UTF8")); //$NON-NLS-1$
         } catch (UnsupportedEncodingException e) {
            br = new BufferedReader(new InputStreamReader(in));
         }

         String header = null;
         StringBuffer value = new StringBuffer(256);
         boolean firstLine = true;

         while (true) {
            String line = br.readLine();
            /*
             * The java.util.jar classes in JDK 1.3 use the value of the last encountered manifest
             * header. So we do the same to emulate this behavior. We no longer throw a
             * BundleException for duplicate manifest headers.
             */

            if (line == null || line.length() == 0) /* EOF or empty line */
            {
               if (!firstLine) /* flush last line */
               {
                  headers.set(header, null); /* remove old attribute,if present */
                  headers.set(header, value.toString().trim());
               }
               break; /* done processing main attributes */
            }

            if (line.charAt(0) == ' ') /* continuation */
            {
               if (firstLine) /* if no previous line */
               {
                  throw new Exception("Invalid Space at line: " + line);
               }
               value.append(line.substring(1));
               continue;
            }

            if (!firstLine) {
               headers.set(header, null); /* remove old attribute,if present */
               headers.set(header, value.toString().trim());
               value.setLength(0); /* clear StringBuffer */
            }

            int colon = line.indexOf(':');
            if (colon == -1) /* no colon */
            {
               throw new Exception("Invalid Colon at line: " + line);
            }
            header = line.substring(0, colon).trim();
            value.append(line.substring(colon + 1));
            firstLine = false;
         }
         return headers;
      } catch (IOException e) {
         throw new Exception("IO Exception: ", e);
      } finally {
         try {
            in.close();
         } catch (IOException ee) {
         }
      }
   }

   @Override
   public synchronized Enumeration<Object> keys() {
      return new ArrayEnumeration(headers, size);
   }

   @Override
   public synchronized Enumeration<Object> elements() {
      return new ArrayEnumeration(values, size);
   }

   @Override
   public synchronized int size() {
      return size;
   }

   @Override
   public synchronized boolean isEmpty() {
      return size == 0;
   }

   @Override
   public String toString() {
      String toReturn = "[";
      for (int index = 0; index < size; index++) {
         toReturn += headers[index] + "=" + values[index] + ",";
      }
      toReturn += "]";
      return toReturn;
   }

   class ArrayEnumeration implements Enumeration<Object> {
      private final Object[] array;
      int cur = 0;

      public ArrayEnumeration(Object[] array, int size) {
         this.array = new Object[size];
         System.arraycopy(array, 0, this.array, 0, this.array.length);
      }

      public boolean hasMoreElements() {
         return cur < array.length;
      }

      public Object nextElement() {
         return array[cur++];
      }

   }
}
