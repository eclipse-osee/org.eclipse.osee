/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.applicability;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

public class FeatureValue extends NamedIdBase {

   private List<ConfigurationValue> configurationValues = new LinkedList<ConfigurationValue>();
   private List<FeatureAttribute> attributes = new LinkedList<FeatureAttribute>();

   public FeatureValue(Long id, String name, List<ConfigurationValue> configurationValues, List<FeatureAttribute> attributes) {
      super(id, name);
      this.setConfigurationValues(configurationValues);
      this.setAttributes(attributes);
   }

   public FeatureValue(Long id, String name) {
      super(id, name);
   }

   public FeatureValue(int id, String name) {
      super(id, name);
   }

   public FeatureValue() {
   }

   /**
    * @return the configurationValues
    */
   public List<ConfigurationValue> getConfigurationValues() {
      return configurationValues;
   }

   /**
    * @param configurationValues the configurationValues to set
    */
   public void setConfigurationValues(List<ConfigurationValue> configurationValues) {
      this.configurationValues = configurationValues;
   }

   @Override
   @JsonIgnore
   public String getIdString() {
      return super.getIdString();
   }

   @Override
   @JsonIgnore
   public int getIdIntValue() {
      return super.getIdIntValue();
   }

   /**
    * @return the attributes
    */
   public List<FeatureAttribute> getAttributes() {
      return attributes;
   }

   /**
    * @param attributes the attributes to set
    */
   public void setAttributes(List<FeatureAttribute> attributes) {
      this.attributes = attributes;
   }

}
