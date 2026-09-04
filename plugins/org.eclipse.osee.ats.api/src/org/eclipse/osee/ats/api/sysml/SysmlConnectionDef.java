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

/**
 * Represents a SysML V2 connection def (typed relationship with named ends and multiplicity). Generated from
 * RelationTypeToken.
 *
 * @author Donald G. Dunne
 */
public class SysmlConnectionDef {

   private final String name;
   private final String endAName;
   private final String endAType;
   private final String endAMultiplicity;
   private final String endBName;
   private final String endBType;
   private final String endBMultiplicity;

   public SysmlConnectionDef(String name, String endAName, String endAType, String endAMultiplicity, String endBName,
      String endBType, String endBMultiplicity) {
      this.name = name;
      this.endAName = endAName;
      this.endAType = endAType;
      this.endAMultiplicity = endAMultiplicity;
      this.endBName = endBName;
      this.endBType = endBType;
      this.endBMultiplicity = endBMultiplicity;
   }

   public String getName() {
      return name;
   }

   public String getEndAName() {
      return endAName;
   }

   public String getEndAType() {
      return endAType;
   }

   public String getEndAMultiplicity() {
      return endAMultiplicity;
   }

   public String getEndBName() {
      return endBName;
   }

   public String getEndBType() {
      return endBType;
   }

   public String getEndBMultiplicity() {
      return endBMultiplicity;
   }
}
