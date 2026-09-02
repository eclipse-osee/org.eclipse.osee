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
 * Represents a SysML V2 enum def.
 *
 * @author Donald G. Dunne
 */
public class SysmlEnumDef {

   private final String name;
   private final List<String> literals = new ArrayList<>();

   public SysmlEnumDef(String name) {
      this.name = name;
   }

   public SysmlEnumDef(String name, List<String> literals) {
      this.name = name;
      this.literals.addAll(literals);
   }

   public String getName() {
      return name;
   }

   public List<String> getLiterals() {
      return literals;
   }

   public void addLiteral(String literal) {
      literals.add(literal);
   }
}
