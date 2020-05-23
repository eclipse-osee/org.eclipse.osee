/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.update;

import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;

/**
 * @author Roberto E. Escobar
 */
public abstract class ConflictResolverOperation extends AbstractOperation {

   private ConflictManagerExternal conflictManager;

   public ConflictResolverOperation(String operationName, String pluginId) {
      super(operationName, pluginId);
      this.conflictManager = null;
   }

   public void setConflictManager(ConflictManagerExternal conflictManager) {
      this.conflictManager = conflictManager;
   }

   public ConflictManagerExternal getConflictManager() {
      return this.conflictManager;
   }
}
