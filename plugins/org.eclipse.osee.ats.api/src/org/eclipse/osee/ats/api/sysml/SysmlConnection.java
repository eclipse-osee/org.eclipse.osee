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
 * Represents a SysML V2 connection usage (instance linking two part usages via a connection def).
 *
 * @author Donald G. Dunne
 */
public class SysmlConnection {

   private final String connectionDefName;
   private final String sourcePartName;
   private final String targetPartName;

   public SysmlConnection(String connectionDefName, String sourcePartName, String targetPartName) {
      this.connectionDefName = connectionDefName;
      this.sourcePartName = sourcePartName;
      this.targetPartName = targetPartName;
   }

   public String getConnectionDefName() {
      return connectionDefName;
   }

   public String getSourcePartName() {
      return sourcePartName;
   }

   public String getTargetPartName() {
      return targetPartName;
   }
}
