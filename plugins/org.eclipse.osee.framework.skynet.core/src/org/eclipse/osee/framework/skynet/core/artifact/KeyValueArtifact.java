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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * Allows easy storage/loading of key/value pairs for any artifact with string attribute type. Any attributes that match
 * key=value will be processed. All others will be ignored. Multiple instances of same key are allowed.
 *
 * @author Donald G. Dunne
 */
public class KeyValueArtifact {

   private final Artifact artifact;
   private final AttributeTypeId keyValueAttributeType;

   public KeyValueArtifact(Artifact artifact, AttributeTypeId keyValueAttributeType)  {
      this.artifact = artifact;
      this.keyValueAttributeType = keyValueAttributeType;
      load();
   }
   protected HashCollection<String, String> keyValueMap = new HashCollection<>(20);
   private final Pattern keyValuePattern = Pattern.compile("^(.*?)=(.*)$", Pattern.MULTILINE | Pattern.DOTALL);

   public HashCollection<String, String> getHashCollection() {
      return keyValueMap;
   }

   public void setWorkDataKeyValueMap(HashCollection<String, String> hashCollection) {
      this.keyValueMap = hashCollection;
   }

   public void save()  {
      if (keyValueMap.size() > 0) {
         Set<String> keyValues = new HashSet<>();
         for (String key : keyValueMap.keySet()) {
            for (String value : keyValueMap.getValues(key)) {
               keyValues.add(key + "=" + value);
            }
         }
         artifact.setAttributeValues(keyValueAttributeType, keyValues);
      } else {
         artifact.deleteAttributes(keyValueAttributeType);
      }
   }

   public void load()  {
      for (String value : artifact.getAttributesToStringList(keyValueAttributeType)) {
         Matcher m = keyValuePattern.matcher(value);
         if (m.find()) {
            addValue(m.group(1), m.group(2));
         }
      }
   }

   public Collection<String> getValues(String key) {
      if (keyValueMap.containsKey(key)) {
         return keyValueMap.getValues(key);
      }
      return Collections.emptyList();
   }

   /**
    * Gets first value even if there are more. Returns null if none.
    */
   public String getValue(String key) {
      if (keyValueMap.getValues(key) == null) {
         return null;
      }
      if (keyValueMap.getValues(key).size() > 0) {
         return keyValueMap.getValues(key).iterator().next();
      }
      return null;
   }

   public void setValues(String key, Collection<String> values) {
      keyValueMap.removeValues(key);
      keyValueMap.put(key, values);
   }

   public void setValue(String key, String value) {
      keyValueMap.removeValues(key);
      keyValueMap.put(key, value);
   }

   public void addValue(String key, String value) {
      keyValueMap.put(key, value);
   }

   public void removeValues(String key) {
      keyValueMap.removeValues(key);
   }

}
