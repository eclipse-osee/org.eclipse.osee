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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;

/**
 * @author Roberto E. Escobar
 */
public class LoadOptions {

   private boolean historical;
   private DeletionFlag includeDeleted;
   private LoadLevel loadLevel;

   public LoadOptions() {
      this(false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.SHALLOW);
   }

   public LoadOptions(boolean historical, boolean includeDeleted, LoadLevel loadLevel) {
      this(historical, DeletionFlag.allowDeleted(includeDeleted), loadLevel);
   }

   public LoadOptions(boolean historical, DeletionFlag includeDeleted, LoadLevel loadLevel) {
      super();
      this.historical = historical;
      this.includeDeleted = includeDeleted;
      this.loadLevel = loadLevel;
   }

   public boolean isHistorical() {
      return historical;
   }

   public boolean areDeletedIncluded() {
      return includeDeleted.areDeletedAllowed();
   }

   public void setIncludeDeleted(boolean enabled) {
      includeDeleted = DeletionFlag.allowDeleted(enabled);
   }

   public LoadLevel getLoadLevel() {
      return loadLevel;
   }

   public void setHistorical(boolean historical) {
      this.historical = historical;
   }

   public void setLoadLevel(LoadLevel loadLevel) {
      this.loadLevel = loadLevel;
   }

   @Override
   public String toString() {
      return "LoadOptions [historical=" + historical + ", includeDeleted=" + includeDeleted + ", loadLevel=" + loadLevel + "]";
   }

}