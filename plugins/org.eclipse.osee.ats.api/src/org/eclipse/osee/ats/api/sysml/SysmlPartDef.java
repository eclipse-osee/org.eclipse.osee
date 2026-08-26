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
 * Represents a SysML V2 part def (type definition with attributes).
 *
 * @author Donald G. Dunne
 */
public class SysmlPartDef {

   private final String name;
   private String superType;
   private final List<SysmlAttributeDef> attributes = new ArrayList<>();

   public SysmlPartDef(String name) {
      this.name = name;
   }

   public SysmlPartDef(String name, String superType) {
      this.name = name;
      this.superType = superType;
   }

   public String getName() {
      return name;
   }

   public String getSuperType() {
      return superType;
   }

   public void setSuperType(String superType) {
      this.superType = superType;
   }

   public boolean hasSuperType() {
      return superType != null && !superType.isEmpty();
   }

   public List<SysmlAttributeDef> getAttributes() {
      return attributes;
   }

   public void addAttribute(SysmlAttributeDef attr) {
      attributes.add(attr);
   }

   public void addAttribute(String attrName, String type) {
      attributes.add(new SysmlAttributeDef(attrName, type));
   }

   public void addAttribute(String attrName, String type, String multiplicity) {
      attributes.add(new SysmlAttributeDef(attrName, type, multiplicity));
   }
}
