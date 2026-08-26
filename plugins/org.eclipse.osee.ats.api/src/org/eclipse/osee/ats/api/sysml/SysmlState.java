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
 * Represents a state within a SysML V2 state def.
 *
 * @author Donald G. Dunne
 */
public class SysmlState {

   private final String name;
   private final boolean isEntry;

   public SysmlState(String name) {
      this(name, false);
   }

   public SysmlState(String name, boolean isEntry) {
      this.name = name;
      this.isEntry = isEntry;
   }

   public String getName() {
      return name;
   }

   public boolean isEntry() {
      return isEntry;
   }
}
