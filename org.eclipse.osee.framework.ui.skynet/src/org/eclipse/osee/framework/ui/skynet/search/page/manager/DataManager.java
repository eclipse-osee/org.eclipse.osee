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
package org.eclipse.osee.framework.ui.skynet.search.page.manager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DataManager implements IDataListener {
   private Set<IDataListener> listeners = new HashSet<IDataListener>();

   private String branchName;
   private int revision;

   public DataManager() {
      super();
      branchName = "";
      revision = 0;
   }

   public String getBranchName() {
      return branchName;
   }

   public int getRevision() {
      return revision;
   }

   public void setBranchName(String branchName) {
      if (!this.branchName.equals(branchName)) {
         this.branchName = branchName;
         dataChanged();
      }
   }

   public void setRevision(int revision) {
      if (this.revision != revision) {
         this.revision = revision;
         dataChanged();
      }
   }

   public void setBranchAndRevision(String branchName, int revision) {
      boolean branchChanged = false;
      boolean revisionChanged = false;

      if (!this.branchName.equals(branchName)) {
         this.branchName = branchName;
         branchChanged = true;
      }

      if (this.revision != revision) {
         this.revision = revision;
         revisionChanged = true;
      }

      if (branchChanged || revisionChanged) {
         dataChanged();
      }
   }

   public void addDataListener(IDataListener listener) {
      listeners.add(listener);
   }

   public void removeDataListener(IDataListener listener) {
      listeners.remove(listener);
   }

   public void dataChanged() {
      Iterator<IDataListener> iterator = listeners.iterator();
      while (iterator.hasNext()) {
         (iterator.next()).dataChanged();
      }
   }

}
