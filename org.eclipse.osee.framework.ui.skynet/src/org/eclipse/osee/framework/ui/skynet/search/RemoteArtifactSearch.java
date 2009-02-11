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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactMatch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactXmlQueryResultParser.MatchLocation;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeLineElement;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeMatch;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
final class RemoteArtifactSearch extends AbstractArtifactSearchQuery {
   private final String queryString;
   private final String[] attributeTypeNames;
   private final boolean includeDeleted;
   private final boolean matchWordOrder;
   private final Branch branch;
   private final boolean findAllMatchLocations;

   RemoteArtifactSearch(String queryString, Branch branch, boolean includeDeleted, boolean matchWordOrder, boolean findAllMatchLocations, String... attributeTypeNames) {
      this.branch = branch;
      this.includeDeleted = includeDeleted;
      this.attributeTypeNames = attributeTypeNames;
      this.queryString = queryString;
      this.matchWordOrder = matchWordOrder;
      this.findAllMatchLocations = findAllMatchLocations;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getCriteriaLabel()
    */
   @Override
   public String getCriteriaLabel() {
      List<String> optionsList = new ArrayList<String>();
      if (includeDeleted) {
         optionsList.add("Include Deleted");
      }

      if (matchWordOrder) {
         optionsList.add("Match Word Order");
         if (findAllMatchLocations) {
            optionsList.add("All Matches");
         } else {
            optionsList.add("1st Match Only");
         }
      }

      if (attributeTypeNames != null && attributeTypeNames.length > 0) {
         optionsList.add(String.format("Attribute Type Filter:%s", Arrays.deepToString(attributeTypeNames)));
      }

      String options = String.format(" - Options:[%s]", StringFormat.listToValueSeparatedString(optionsList, ", "));
      return String.format("%s%s", queryString, optionsList.size() > 0 ? options : "");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IStatus run(IProgressMonitor pm) {
      setIsDoneRunning(false);
      aResult.removeAll();

      try {
         List<ArtifactMatch> matches =
               ArtifactQuery.getArtifactMatchesFromAttributeWithKeywords(branch, queryString, matchWordOrder,
                     includeDeleted, findAllMatchLocations, attributeTypeNames);

         for (ArtifactMatch artifactMatch : matches) {
            if (artifactMatch.hasMatchData()) {
               Artifact artifact = artifactMatch.getArtifact();
               List<Match> matchList = new ArrayList<Match>();
               HashCollection<Attribute<?>, MatchLocation> matchData = artifactMatch.getMatchData();
               for (Attribute<?> attribute : matchData.keySet()) {
                  for (MatchLocation location : matchData.getValues(attribute)) {
                     AttributeLineElement lineElement = new AttributeLineElement(artifact, attribute, location);
                     int offset = location.getStartPosition() - 1;
                     int length = location.getEndPosition() - location.getStartPosition();
                     matchList.add(new AttributeMatch(artifact, offset, length, lineElement));
                  }
               }
               aResult.addMatches(matchList.toArray(new Match[matchList.size()]));
            } else {
               Match match = new Match(artifactMatch.getArtifact(), 1, 2);
               aResult.addMatch(match);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      setIsDoneRunning(true);
      return new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "OK", null);
   }
}
