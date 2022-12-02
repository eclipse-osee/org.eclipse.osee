/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Audrey Denk
 */
public class ConfigurationGroupDefinition {
   private String id = Strings.EMPTY_STRING;
   private String name = Strings.EMPTY_STRING;
   private String description = Strings.EMPTY_STRING;
   private List<String> configurations = new ArrayList<>();

   public ConfigurationGroupDefinition() {
      //
   }

   public ConfigurationGroupDefinition(String id, String name, String desc, List<String> configurations) {
      this.setId(id);
      this.setName(name);
      this.setDescription(desc);
      this.setConfigurations(configurations);
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
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

   public void setDescription(String desc) {
      this.description = desc;
   }

   public List<String> getConfigurations() {
      return configurations;
   }

   public void setConfigurations(List<String> configurations) {
      this.configurations = configurations;
   }

}
