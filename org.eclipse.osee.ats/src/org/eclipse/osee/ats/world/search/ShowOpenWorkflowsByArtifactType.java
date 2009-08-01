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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class ShowOpenWorkflowsByArtifactType extends WorldUISearchItem {

   private final String artifactTypeName;
   private final boolean showFinished;
   private final boolean showWorkflow;

   public ShowOpenWorkflowsByArtifactType(String displayName, String artifactTypeName, boolean showFinished, boolean showWorkflow, OseeImage oseeImage) {
      super(displayName, oseeImage);
      this.artifactTypeName = artifactTypeName;
      this.showFinished = showFinished;
      this.showWorkflow = showWorkflow;
   }

   public ShowOpenWorkflowsByArtifactType(ShowOpenWorkflowsByArtifactType showOpenWorkflowsByArtifactType) {
      super(showOpenWorkflowsByArtifactType);
      this.artifactTypeName = showOpenWorkflowsByArtifactType.artifactTypeName;
      this.showFinished = showOpenWorkflowsByArtifactType.showFinished;
      this.showWorkflow = showOpenWorkflowsByArtifactType.showWorkflow;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {

      List<Artifact> artifacts = null;
      if (!showFinished) {
         List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>();
         List<String> cancelOrComplete = new ArrayList<String>(2);
         cancelOrComplete.add(DefaultTeamState.Cancelled.name() + ";;;");
         cancelOrComplete.add(DefaultTeamState.Completed.name() + ";;;");
         criteria.add(new AttributeCriteria(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(), cancelOrComplete,
               Operator.NOT_EQUAL));
         artifacts = ArtifactQuery.getArtifactListFromTypeAnd(artifactTypeName, AtsUtil.getAtsBranch(), 500, criteria);
      } else {
         artifacts = ArtifactQuery.getArtifactListFromType(artifactTypeName, AtsUtil.getAtsBranch());
      }

      if (showWorkflow) {
         return RelationManager.getRelatedArtifacts(artifacts, 1, AtsRelation.TeamWorkflowToReview_Team);
      } else {
         return artifacts;
      }
   }

   @Override
   public WorldUISearchItem copy() {
      return new ShowOpenWorkflowsByArtifactType(this);
   }

}
