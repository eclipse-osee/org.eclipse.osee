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

package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

public abstract class AbstractArtifactSearchQuery implements ISearchQuery {
   protected ArtifactSearchResult aResult;
   private boolean doneRunning;

   public boolean canRunInBackground() {
      return true;
   }

   public String getLabel() {
      return "Artifact Search";
   }

   protected void setIsDoneRunning(boolean isDoneRunning) {
      this.doneRunning = isDoneRunning;
   }

   public abstract IStatus run(final IProgressMonitor pm);

   public String getResultLabel() {
      return getCriteriaLabel() + " - " + (doneRunning ? (aResult.getMatchCount() + " matches") : "busy");
   }

   public abstract String getCriteriaLabel();

   public boolean canRerun() {
      return true;
   }

   public ISearchResult getSearchResult() {
      if (aResult == null) {
         aResult = new ArtifactSearchResult(this);
      }
      return aResult;
   }
}
