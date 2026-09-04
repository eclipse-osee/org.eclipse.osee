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
 * Represents a transition between states in a SysML V2 state def.
 *
 * @author Donald G. Dunne
 */
public class SysmlTransition {

   private final String name;
   private final String source;
   private final String target;

   public SysmlTransition(String name, String source, String target) {
      this.name = name;
      this.source = source;
      this.target = target;
   }

   public SysmlTransition(String source, String target) {
      this.name = source + "_to_" + target;
      this.source = source;
      this.target = target;
   }

   public String getName() {
      return name;
   }

   public String getSource() {
      return source;
   }

   public String getTarget() {
      return target;
   }
}
