/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.api.sysml;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SysML V2 part usage (instance of a part def with attribute values).
 *
 * @author Donald G. Dunne
 */
public class SysmlPartUsage {

   private final String name;
   private final String defName;
   private final List<SysmlAttributeValue> attributeValues = new ArrayList<>();
   private String exhibitState;

   public SysmlPartUsage(String name, String defName) {
      this.name = name;
      this.defName = defName;
   }

   public String getName() {
      return name;
   }

   public String getDefName() {
      return defName;
   }

   public List<SysmlAttributeValue> getAttributeValues() {
      return attributeValues;
   }

   public void addAttributeValue(SysmlAttributeValue value) {
      attributeValues.add(value);
   }

   public void addAttributeValue(String attrName, Object value) {
      attributeValues.add(new SysmlAttributeValue(attrName, value));
   }

   public void addEnumValue(String attrName, String value, String enumDefName) {
      attributeValues.add(new SysmlAttributeValue(attrName, value, true, enumDefName));
   }

   public String getExhibitState() {
      return exhibitState;
   }

   public void setExhibitState(String exhibitState) {
      this.exhibitState = exhibitState;
   }

   public boolean hasExhibitState() {
      return exhibitState != null && !exhibitState.isEmpty();
   }
}
