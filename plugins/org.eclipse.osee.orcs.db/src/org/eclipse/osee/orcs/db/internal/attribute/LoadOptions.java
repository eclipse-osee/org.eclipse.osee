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
package org.eclipse.osee.orcs.db.internal.attribute;

import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;

/**
 * @author Roberto E. Escobar
 */
public class LoadOptions {
   private static final int MAX_FETCH_SIZE = 10000;

   private boolean historical;
   private DeletionFlag allowDeletedArtifacts;
   private LoadLevel loadLevel;
   private int fetchSize;

   public LoadOptions() {
      this(MAX_FETCH_SIZE, false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.SHALLOW);
   }

   public LoadOptions(int fetchSize, boolean historical, DeletionFlag allowDeletedArtifacts, LoadLevel loadLevel) {
      super();
      this.fetchSize = fetchSize;
      this.historical = historical;
      this.allowDeletedArtifacts = allowDeletedArtifacts;
      this.loadLevel = loadLevel;
   }

   public int getFetchSize() {
      return fetchSize;
   }

   public boolean isHistorical() {
      return historical;
   }

   public boolean areDeletedAllowed() {
      return allowDeletedArtifacts.areDeletedAllowed();
   }

   public LoadLevel getLoadLevel() {
      return loadLevel;
   }

   public void setFetchSize(int fetchSize) {
      this.fetchSize = fetchSize;
   }

   public void setHistorical(boolean historical) {
      this.historical = historical;
   }

   public void setAllowDeletedArtifacts(DeletionFlag allowDeletedArtifacts) {
      this.allowDeletedArtifacts = allowDeletedArtifacts;
   }

   public void setLoadLevel(LoadLevel loadLevel) {
      this.loadLevel = loadLevel;
   }

   @Override
   public String toString() {
      return "LoadOptions [historical=" + historical + ", allowDeletedArtifacts=" + allowDeletedArtifacts + ", loadLevel=" + loadLevel + ", fetchSize=" + fetchSize + "]";
   }

}