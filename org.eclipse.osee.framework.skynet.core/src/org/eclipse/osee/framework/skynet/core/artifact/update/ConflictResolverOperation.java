/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
