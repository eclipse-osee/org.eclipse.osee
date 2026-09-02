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
 * Represents a SysML V2 attribute definition within a part def.
 *
 * @author Donald G. Dunne
 */
public class SysmlAttributeDef {

   private final String name;
   private final String type;
   private final String multiplicity;

   public SysmlAttributeDef(String name, String type) {
      this(name, type, SysmlConstants.MULTIPLICITY_ONE);
   }

   public SysmlAttributeDef(String name, String type, String multiplicity) {
      this.name = name;
      this.type = type;
      this.multiplicity = multiplicity;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public String getMultiplicity() {
      return multiplicity;
   }

   public boolean hasMultiplicity() {
      return multiplicity != null && !multiplicity.isEmpty();
   }
}
