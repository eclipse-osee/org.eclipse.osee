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
package org.eclipse.osee.framework.core.applicability;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Donald G. Dunne
 */
public class FeatureDefinition extends NamedIdBase {

   private List<String> values;
   private String defaultValue;
   private String description;
   private boolean multiValued;
   private String valueType;
   private Object data;

   public FeatureDefinition() {
      super(ArtifactId.SENTINEL.getId(), "");
      // Not doing anything
   }

   public FeatureDefinition(Long id, String name, String valueType, List<String> values, String defaultValue, boolean multiValued, String description) {
      super(id, name);
      this.valueType = valueType;
      this.values = values;
      this.defaultValue = defaultValue;
      this.multiValued = multiValued;
      this.description = description;
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

   public void setValues(List<String> values) {
      this.values = values;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isMultiValued() {
      return multiValued;
   }

   public void setMultiValued(boolean multiValued) {
      this.multiValued = multiValued;
   }

   public String getValueType() {
      return valueType;
   }

   public void setValueType(String valueType) {
      this.valueType = valueType;
   }

   @JsonIgnore
   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }
}
