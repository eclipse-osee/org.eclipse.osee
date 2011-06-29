/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AbstractWorkDefItem {

   private String name;
   protected String description;
   protected Map<String, String> workDataKeyValueMap = new HashMap<String, String>();
   private final Pattern keyValuePattern = Pattern.compile("^(.*?)=(.*)$", Pattern.MULTILINE | Pattern.DOTALL);

   public AbstractWorkDefItem(String name) {
      this.name = name;
   }

   public String getWorkDataValue(String key) {
      return workDataKeyValueMap.get(key);
   }

   public void addWorkDataKeyValue(String key, String value) {
      workDataKeyValueMap.put(key, value);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Pattern getKeyValuePattern() {
      return keyValuePattern;
   }

   public Map<String, String> getWorkDataKeyValueMap() {
      return workDataKeyValueMap;
   }

   @Override
   public String toString() {
      return getName();
   }
}
