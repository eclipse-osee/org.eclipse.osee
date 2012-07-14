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
public class LoadOptions extends Options {

   private LoadLevel loadLevel;

   public LoadOptions() {
      this(DeletionFlag.EXCLUDE_DELETED, LoadLevel.SHALLOW);
   }

   public LoadOptions(boolean includeDeleted, LoadLevel loadLevel) {
      this(DeletionFlag.allowDeleted(includeDeleted), loadLevel);
   }

   public LoadOptions(DeletionFlag includeDeleted, LoadLevel loadLevel) {
      super(includeDeleted);
      this.loadLevel = loadLevel;
   }

   public LoadLevel getLoadLevel() {
      return loadLevel;
   }

   public void setLoadLevel(LoadLevel loadLevel) {
      this.loadLevel = loadLevel;
   }

   @Override
   public void reset() {
      super.reset();
      loadLevel = LoadLevel.SHALLOW;
   }

   @Override
   public LoadOptions clone() {
      LoadOptions clone = new LoadOptions();
      clone.setIncludeDeleted(this.areDeletedIncluded());
      clone.setFromTransaction(getFromTransaction());
      clone.loadLevel = this.loadLevel;
      return clone;
   }

   @Override
   public String toString() {
      return "LoadOptions [loadLevel=" + loadLevel + " " + super.toString() + "]";
   }

}