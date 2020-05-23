/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Roberto E. Escobar
 */
public class UrlQuery {

   private static final String URL_ENCODING = "UTF-8";
   private final Map<String, List<String>> parameters;

   public UrlQuery() {
      this.parameters = new TreeMap<>();
   }

   public UrlQuery clear() {
      parameters.clear();
      return this;
   }

   public boolean containsKey(String key) {
      return parameters.containsKey(key);
   }

   public UrlQuery put(String name, String... values) {
      put(name, false, values);
      return this;
   }

   public UrlQuery put(String name, Object... values) {
      put(name, false, values);
      return this;
   }

   public UrlQuery putInPlace(String name, String... values) {
      put(name, true, values);
      return this;
   }

   public UrlQuery putInPlace(String name, Object... values) {
      put(name, true, values);
      return this;
   }

   private UrlQuery put(String name, boolean overrite, String... values) {
      for (String value : values) {
         if (Strings.isValid(value)) {
            innerPut(name, value, overrite);
         }
      }
      return this;
   }

   private UrlQuery put(String name, boolean overrite, Object... values) {
      for (Object value : values) {
         if (value != null) {
            innerPut(name, String.valueOf(value), overrite);
         }
      }
      return this;
   }

   private void innerPut(String name, String value, boolean overrite) {
      List<String> list = parameters.get(name);
      if (list == null) {
         list = new ArrayList<>();
         parameters.put(name, list);
      }
      if (overrite) {
         list.clear();
      }
      list.add(value);
   }

   private void innerPut(String name, String value) {
      innerPut(name, value, false);
   }

   public UrlQuery parse(URL url) throws UnsupportedEncodingException {
      if (url != null) {
         parse(url.getQuery());
      }
      return this;
   }

   public UrlQuery parse(String qs) throws UnsupportedEncodingException {
      clear();
      if (Strings.isValid(qs)) {
         String toParse = qs;

         int pos = toParse.lastIndexOf('/');
         if (pos > -1) {
            toParse = toParse.substring(pos + 1);
         }

         pos = toParse.lastIndexOf('?');
         if (pos > -1) {
            toParse = toParse.substring(pos + 1);
         }

         String pairs[] = toParse.split("&");
         for (String pair : pairs) {
            String name;
            String value;
            pos = pair.indexOf('=');
            if (pos == -1) {
               name = pair;
               value = null;
            } else {
               name = decode(pair.substring(0, pos));
               value = decode(pair.substring(pos + 1, pair.length()));
            }
            innerPut(name, value);
         }
      }
      return this;
   }

   public String getParameter(String name) {
      List<String> values = parameters.get(name);
      String toReturn;
      if (values == null) {
         toReturn = null;
      } else if (values.isEmpty()) {
         toReturn = "";
      } else {
         toReturn = values.iterator().next();
      }
      return toReturn;
   }

   public String[] getParameterValues(String name) {
      List<String> values = parameters.get(name);
      return values != null ? values.toArray(new String[values.size()]) : null;
   }

   public Enumeration<String> getParameterNames() {
      return java.util.Collections.enumeration(parameters.keySet());
   }

   public Map<String, String[]> getParameterMap() {
      Map<String, String[]> map = new TreeMap<>();
      for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
         List<String> list = entry.getValue();
         String[] values = list != null ? list.toArray(new String[list.size()]) : null;
         map.put(entry.getKey(), values);
      }
      return map;
   }

   public String toUrl() throws UnsupportedEncodingException {
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (Entry<String, List<String>> entry : parameters.entrySet()) {
         String key = entry.getKey();
         Collection<String> values = entry.getValue();
         if (values != null && !values.isEmpty()) {

            for (String value : values) {
               if (!isFirst) {
                  builder.append('&');
               }
               builder.append(encode(key));
               builder.append('=');
               builder.append(encode(value));
               isFirst = false;
            }
         }
      }
      return builder.toString();
   }

   @Override
   public String toString() {
      try {
         return toUrl();
      } catch (UnsupportedEncodingException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   private static String decode(String value) throws UnsupportedEncodingException {
      return URLDecoder.decode(value, URL_ENCODING);
   }

   private static String encode(String value) throws UnsupportedEncodingException {
      return URLEncoder.encode(value, URL_ENCODING);
   }
}