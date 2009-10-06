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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;

/**
 * Allows easy storage/loading of key/value pairs for any artifact with string attribute type
 * 
 * @author Donald G. Dunne
 */
public class KeyValueArtifact {

   private final Artifact artifact;
   private final String keyValueAttributeName;

   public KeyValueArtifact(Artifact artifact, String keyValueAttributeName) throws OseeCoreException {
      this.artifact = artifact;
      this.keyValueAttributeName = keyValueAttributeName;
      load();
   }
   protected Map<String, String> workDataKeyValueMap = new HashMap<String, String>();
   private final Pattern keyValuePattern = Pattern.compile("^(.*?)=(.*)$", Pattern.MULTILINE | Pattern.DOTALL);

   public Map<String, String> getWorkDataKeyValueMap() {
      return workDataKeyValueMap;
   }

   public void setWorkDataKeyValueMap(Map<String, String> workDataKeyValueMap) {
      this.workDataKeyValueMap = workDataKeyValueMap;
   }

   public void save() throws OseeCoreException {
      if (workDataKeyValueMap.size() > 0) {
         Set<String> keyValues = new HashSet<String>();
         for (Entry<String, String> entry : workDataKeyValueMap.entrySet()) {
            keyValues.add(entry.getKey() + "=" + entry.getValue());
         }
         artifact.setAttributeValues(keyValueAttributeName, keyValues);
      }
   }

   public void load() throws OseeCoreException {
      for (String value : artifact.getAttributesToStringList(keyValueAttributeName)) {
         Matcher m = keyValuePattern.matcher(value);
         if (m.find()) {
            addWorkDataKeyValue(m.group(1), m.group(2));
         } else {
            throw new OseeStateException("Illegal value for WorkData; must be key=value");
         }
      }
   }

   public String getWorkDataValue(String key) {
      return workDataKeyValueMap.get(key);
   }

   public void addWorkDataKeyValue(String key, String value) {
      workDataKeyValueMap.put(key, value);
   }

}
