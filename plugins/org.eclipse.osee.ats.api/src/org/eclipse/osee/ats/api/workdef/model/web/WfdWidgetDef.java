/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.framework.core.data.Multiplicity.MultiplicityToken;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * @author Donald G. Dunne
 */
public class WfdWidgetDef extends WfdWidgetItem {

   public String widgetName;
   private List<WidgetHint> widgetHints = new ArrayList<>();
   private WfeAttributeTypeToken attrType;
   private String displayName;
   private String displayValue;
   private MultiplicityToken multiplicity;
   public String dataType;
   private Collection<WidgetOption> widgetOptions = new ArrayList<>();
   private boolean required = false;
   private boolean multiLine = false;
   private boolean editable = false;
   private List<String> enumOptions = new ArrayList<>();
   private List<WfdAttribute> attrs = new ArrayList<>();

   public WfdWidgetDef() {
      // for jax-rs
   }

   public String getWidgetName() {
      return widgetName;
   }

   public void setWidgetName(String widgetName) {
      this.widgetName = widgetName;
   }

   public List<WidgetHint> getWidgetHints() {
      return widgetHints;
   }

   public void setWidgetHints(List<WidgetHint> widgetHints) {
      this.widgetHints = widgetHints;
   }

   public String getDataType() {
      return dataType;
   }

   public void setDataType(String dataType) {
      this.dataType = dataType;
   }

   public Collection<WidgetOption> getWidgetOptions() {
      return widgetOptions;
   }

   public void setWidgetOptions(Collection<WidgetOption> widgetOptions) {
      this.widgetOptions = widgetOptions;
   }

   public boolean isRequired() {
      return required;
   }

   public void setRequired(boolean required) {
      this.required = required;
   }

   public boolean isMultiLine() {
      return multiLine;
   }

   public void setMultiLine(boolean multiLine) {
      this.multiLine = multiLine;
   }

   public List<String> getEnumOptions() {
      return enumOptions;
   }

   public void setEnumOptions(List<String> enumOptions) {
      this.enumOptions = enumOptions;
   }

   public WfeAttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(WfeAttributeTypeToken attrType) {
      this.attrType = attrType;
   }

   public List<WfdAttribute> getAttrs() {
      return attrs;
   }

   public void setAttrs(List<WfdAttribute> attrs) {
      this.attrs = attrs;
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public String getDisplayName() {
      return displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public String getDisplayValue() {
      return displayValue;
   }

   public void setDisplayValue(String displayValue) {
      this.displayValue = displayValue;
   }

   public MultiplicityToken getMultiplicity() {
      return multiplicity;
   }

   public void setMultiplicity(MultiplicityToken multiplicity) {
      this.multiplicity = multiplicity;
   }

}
