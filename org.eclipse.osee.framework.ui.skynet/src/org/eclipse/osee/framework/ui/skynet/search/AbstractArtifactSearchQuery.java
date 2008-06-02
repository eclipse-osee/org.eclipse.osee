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

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

public abstract class AbstractArtifactSearchQuery implements ISearchQuery {
   protected ArtifactSearchResult aResult;
   protected int numberOfMatches;
   private boolean doneRunning;

   public boolean canRunInBackground() {
      return true;
   }

   public IStatus run(final IProgressMonitor pm) {
      doneRunning = false;
      aResult.removeAll();
      numberOfMatches = 0;

      try {
         for (Artifact artifact : getArtifacts()) {
            Match match = new Match(artifact, 1, 2);
            aResult.addMatch(match);

            numberOfMatches++;
         }
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
      }

      doneRunning = true;
      return new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "OK", null);
   }

   public abstract Collection<Artifact> getArtifacts() throws Exception;

   public String getLabel() {
      return "Artifact Search";
   }

   public String getResultLabel() {
      return getCriteriaLabel() + " - " + (doneRunning ? (numberOfMatches + " matches") : "busy");
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
