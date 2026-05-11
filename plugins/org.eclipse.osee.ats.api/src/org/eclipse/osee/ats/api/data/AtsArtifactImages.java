/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.data;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Action;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileBacklog;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileFeatureGroup;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileProgram;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileProgramBacklog;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileProgramBacklogItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileProgramFeature;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileSprint;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileStory;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileTeam;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Goal;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.PeerToPeerReview;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ProblemReportTeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Task;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactImages {

   private final static List<ArtifactImage> images = new LinkedList<>();

   // @formatter:off

   // ATS
   public static ArtifactImage ACTION = construct(Action, "action.gif", "/ats/images");
   public static ArtifactImage TEAM_WORKFLOW = construct(TeamWorkflow, "workflow.gif", "/ats/images");
   public static ArtifactImage TASK = construct(Task, "task.gif", "/ats/images");
   public static ArtifactImage PEER_REVIEW = construct(PeerToPeerReview, "peer_review.gif", "/ats/images");
   public static ArtifactImage GOAL = construct(Goal, "peer_review.gif", "/ats/images");
   public static ArtifactImage PROBLEM_REPORT = construct(ProblemReportTeamWorkflow, "probRept.gif", "/ats/images");

   // Agile
   public static ArtifactImage AGILE_SPRINT = construct(AgileSprint, "agileSprint.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_TEAM = construct(AgileTeam, "agileTeam.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_FEATURE_GROUP = construct(AgileFeatureGroup, "agileFeatureGroup.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_BACKLOG = construct(AgileBacklog, "agileBacklog.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_BACKLOG_ITEM = construct(AgileProgramBacklogItem, "agileProgramBacklogItem.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_PROGRAM = construct(AgileProgram, "agileProgram.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_PROGRAM_BACKLOG = construct(AgileProgramBacklog, "agileProgramBacklog.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_PROGRAM_FEATURE = construct(AgileProgramFeature, "agileProgramFeature.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_STORY = construct(AgileStory, "agileStory.gif", "/ats/agileui/images");
   // @formatter:on

   protected static ArtifactImage construct(ArtifactTypeToken artifactType, String imageName, String baseUrl) {
      ArtifactImage image = ArtifactImage.construct(artifactType, imageName, baseUrl);
      images.add(image);
      return image;
   }

   public static List<ArtifactImage> getImages() {
      return images;
   }

}
