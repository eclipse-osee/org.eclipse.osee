/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.List;

public class FeatureDefinition {

   private String name;
   private String type;
   private List<String> values;
   private String defaultValue;
   private String description;

   public FeatureDefinition() {
      // Not doing anything
   }

   public FeatureDefinition(String name, String type, List<String> values, String defaultValue, String description) {
      this.name = name;
      this.type = type;
      this.values = values;
      this.defaultValue = defaultValue;
      this.description = description;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public List<String> getValues() {
      return values;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public String getDescription() {
      return description;
   }
}
