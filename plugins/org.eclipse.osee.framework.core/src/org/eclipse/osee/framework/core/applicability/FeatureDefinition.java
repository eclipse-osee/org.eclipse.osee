/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */

public class FeatureDefinition extends NamedIdBase {
   public static final FeatureDefinition SENTINEL = new FeatureDefinition();

   private List<String> values;
   private String defaultValue;
   private String description;
   private boolean multiValued;
   private String valueType;
   private Object data;
   private String type; //legacy

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

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }
}
