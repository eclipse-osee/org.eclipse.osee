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
package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Donald G. Dunne
 */
public class ShowOpenWorkflowsByArtifactType extends WorldSearchItem {

   private final Collection<String> artifactTypes;
   private final boolean showFinished;
   private final boolean showWorkflow;

   public ShowOpenWorkflowsByArtifactType(String displayName, Collection<String> artifactTypes, boolean showFinished, boolean showWorkflow) {
      super(displayName);
      this.artifactTypes = artifactTypes;
      this.showFinished = showFinished;
      this.showWorkflow = showWorkflow;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {

      List<ISearchPrimitive> artTypeNameCriteria = new LinkedList<ISearchPrimitive>();
      for (String artType : artifactTypes)
         artTypeNameCriteria.add(new ArtifactTypeSearch(artType, Operator.EQUAL));
      FromArtifactsSearch artTypeNameSearch = new FromArtifactsSearch(artTypeNameCriteria, false);

      List<ISearchPrimitive> allReviewCriteria = new LinkedList<ISearchPrimitive>();
      allReviewCriteria.add(artTypeNameSearch);
      if (!showFinished) {
         allReviewCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Cancelled.name() + ";;;", Operator.NOT_EQUAL));
         allReviewCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Completed.name() + ";;;", Operator.NOT_EQUAL));
      }
      FromArtifactsSearch allReviews = new FromArtifactsSearch(allReviewCriteria, true);

      if (!showWorkflow) {
         if (isCancelled()) return EMPTY_SET;
         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(allReviewCriteria, true,
                     BranchPersistenceManager.getInstance().getAtsBranch());
         if (isCancelled()) return EMPTY_SET;
         return arts;
      }

      List<ISearchPrimitive> teamCriteria = new LinkedList<ISearchPrimitive>();
      teamCriteria.add(new InRelationSearch(allReviews, RelationSide.TeamWorkflowToReview_Team));

      if (isCancelled()) return EMPTY_SET;
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifacts(teamCriteria, true,
                  BranchPersistenceManager.getInstance().getAtsBranch());

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

}
