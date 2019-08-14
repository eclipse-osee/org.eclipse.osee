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
package org.eclipse.osee.framework.jdk.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class AXml {

   public static String getRootTag(String xmlStr) {
      Matcher m;
      m = Pattern.compile("^[\r\n \t]*<.*?>[\r\n \t]*<(.*?)>", Pattern.MULTILINE | Pattern.DOTALL).matcher(xmlStr);
      if (m.find()) {
         return m.group(1);
      }
      return "";
   }

   /**
    * Returns <elementName>data</elementName> NOTE: data is not sent through AXml.textToXml
    *
    * @return String <elementName>data</elementName>
    */
   public static String addTagData(String elementName, String data) {
      return addTagData(elementName, data, false);
   }

   public static String addTagData(String elementName, Collection<Integer> data, String separator) {
      return addTagData(elementName, Collections.toString(separator, data), false);
   }

   public static String addTagData(String elementName, String data, boolean newLine) {
      String str = "<" + elementName + ">" + data + "</" + elementName + ">";
      if (newLine) {
         return str + "\n";
      } else {
         return str;
      }
   }

   /**
    * Parse <xmlRoot name="this.is.name" value="this.is.value"> and return name, value
    *
    * @param xmlRoot xml tag name
    * @param xmlStr string containing xml
    * @return String[]{name, value}
    */
   public static String[] getNameValue(String xmlRoot, String xmlStr) {
      String[] strs = new String[] {"", ""};
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + " name=\"(.*?)\" value=\"(.*?)\" />",
         Pattern.MULTILINE | Pattern.DOTALL).matcher(xmlStr);
      if (m.find()) {
         strs[0] = m.group(1);
         strs[1] = m.group(2);
      }
      return strs;
   }

   public static String getNameValueXml(String xmlRoot, String name, String value) {
      return String.format("<%s name=\"%s\" value=\"%s\"/>", xmlRoot, name, value);
   }

   public static String[] getTagDataArray(String xmlStr, String xmlRoot) {
      Vector<String> v = new Vector<>();
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
         xmlStr);
      while (m.find()) {
         v.add(xmlToText(m.group(1)));
      }
      return v.toArray(new String[v.size()]);
   }

   public static List<Integer> getTagIntegerDataArray(String xmlStr, String xmlRoot, String separator) {
      List<Integer> results = new ArrayList<>();
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
         xmlStr);
      while (m.find()) {
         for (String str : m.group(1).split(separator)) {
            results.add(new Integer(xmlToText(str)));
         }
      }
      return results;
   }

   /**
    * Returns data between <xmlRoot> and </xmlRoot> from xmlStr
    *
    * @return Return tag string
    */
   public static String getTagData(String xmlStr, String xmlRoot) {
      String tags[] = getTagDataArray(xmlStr, xmlRoot);
      if (tags.length > 0) {
         return tags[0];
      }
      return "";
   }

   public static int getTagIntData(String xmlStr, String xmlRoot) {
      String tags[] = getTagDataArray(xmlStr, xmlRoot);
      if (tags.length > 0) {
         String intStr = tags[0];
         return new Integer(intStr).intValue();
      }
      return 0;
   }

   public static Boolean getTagBooleanData(String xmlStr, String xmlRoot) {
      String tags[] = getTagDataArray(xmlStr, xmlRoot);
      if (tags.length > 0) {
         String intStr = tags[0];
         return intStr.equals("true") ? true : false;
      }
      return false;
   }

   public static Integer getTagIntegerData(String xmlStr, String xmlRoot) {
      String tags[] = getTagDataArray(xmlStr, xmlRoot);
      if (tags.length > 0) {
         String intStr = tags[0];
         return new Integer(intStr);
      }
      return null;
   }

   /**
    * Given text strings containing xml reserved characters, replace with valid xml representation characters > => & gt;
    * < => & lt; & => & amp; ' => & apos; " => & quot;
    *
    * @param text text to be converted to valid XML representation characters
    * @return String valid xml string
    */
   public static String textToXml(String text) {
      if (!Strings.isValid(text)) {
         return "";
      }
      return ReservedCharacters.encodeXmlEntities(text);
   }

   /**
    * Given xml strings containing xml reserved characters, replace with displayable characters > <= & gt; < <= & lt; &
    * <= & amp; ' <= & apos; " <= & quot;
    *
    * @return displayable string
    */
   //TODO: remove this and use {@link ReservedCharacters}
   public static String xmlToText(String xml) {
      if (!Strings.isValid(xml)) {
         return "";
      }
      String str = xml;
      str = str.replaceAll("&gt;", ">");
      str = str.replaceAll("&lt;", "<");
      str = str.replaceAll("&apos;", "'");
      str = str.replaceAll("&quot;", "\"");
      str = str.replaceAll("&amp;", "&");
      return str;
   }

   public static String removeXmlTags(String str) {
      return str.replaceAll("<[^>]+>", "");
   }

}
