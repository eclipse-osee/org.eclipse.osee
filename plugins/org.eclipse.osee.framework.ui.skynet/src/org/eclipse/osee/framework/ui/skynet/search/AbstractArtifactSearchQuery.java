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

package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

public abstract class AbstractArtifactSearchQuery implements ISearchQuery {
   protected ArtifactSearchResult aResult;
   private boolean doneRunning;

   @Override
   public boolean canRunInBackground() {
      return true;
   }

   @Override
   public String getLabel() {
      return "Artifact Search";
   }

   protected void setIsDoneRunning(boolean isDoneRunning) {
      this.doneRunning = isDoneRunning;
   }

   @Override
   public abstract IStatus run(final IProgressMonitor pm);

   public String getResultLabel() {
      StringBuilder builder = new StringBuilder();
      builder.append(Strings.truncate(getCriteriaLabel(), 256));
      builder.append(" - ");
      if (doneRunning) {
         builder.append(aResult.getMatchCount());
         if (aResult.getMatchCount() > 0) {
            builder.append(" matches");
         } else {
            builder.append(" match");
         }
         if (aResult.getArtifactResults() != null && !aResult.getArtifactResults().isEmpty()) {
            builder.append(" on Branch: \"");
            builder.append(aResult.getArtifactResults().get(0).getBranchToken().getShortName());
            builder.append("\"");
         }
      } else {
         builder.append("busy");
      }
      return builder.toString();
   }

   public abstract String getCriteriaLabel();

   @Override
   public boolean canRerun() {
      return true;
   }

   @Override
   public ISearchResult getSearchResult() {
      if (aResult == null) {
         aResult = new ArtifactSearchResult(this);
      }
      return aResult;
   }
}
