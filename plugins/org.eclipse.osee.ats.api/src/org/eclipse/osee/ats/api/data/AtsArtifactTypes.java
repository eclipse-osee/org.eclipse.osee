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
package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactTypes {

   // @formatter:off
   public static final ArtifactTypeToken AtsConfigObject = ArtifactTypeToken.valueOf(801, "ats.Ats Config Artifact");
   public static final ArtifactTypeToken Action = ArtifactTypeToken.valueOf(67, "Action");
   public static final ArtifactTypeToken ActionableItem = ArtifactTypeToken.valueOf(69, "Actionable Item");
   public static final ArtifactTypeToken Configuration = ArtifactTypeToken.valueOf(93802085744703L, "ATS Configuration");
   public static final ArtifactTypeToken DecisionReview = ArtifactTypeToken.valueOf(66, "Decision Review");
   public static final ArtifactTypeToken PeerToPeerReview = ArtifactTypeToken.valueOf(65, "PeerToPeer Review");
   public static final ArtifactTypeToken Task = ArtifactTypeToken.valueOf(74, "Task");
   public static final ArtifactTypeToken AbstractWorkflowArtifact = ArtifactTypeToken.valueOf(71, "Abstract State Machine Artifact");
   public static final ArtifactTypeToken ReviewArtifact = ArtifactTypeToken.valueOf(64, "Abstract Review Artifact");
   public static final ArtifactTypeToken TeamDefinition = ArtifactTypeToken.valueOf(68, "Team Definition");
   public static final ArtifactTypeToken ResponsibleTeam = ArtifactTypeToken.valueOf(8943243743202487405L, "Responsible Team");
   public static final ArtifactTypeToken TeamWorkflow = ArtifactTypeToken.valueOf(73, "Team Workflow");
   public static final ArtifactTypeToken Version = ArtifactTypeToken.valueOf(70, "Version");
   public static final ArtifactTypeToken Goal = ArtifactTypeToken.valueOf(72, "Goal");
   public static final ArtifactTypeToken AtsArtifact = ArtifactTypeToken.valueOf(63, "ats.Ats Artifact");
   public static final ArtifactTypeToken WorkDefinition = ArtifactTypeToken.valueOf(62, "Work Definition");
   public static final ArtifactTypeToken WorkPackage = ArtifactTypeToken.valueOf(802, "Work Package");
   public static final ArtifactTypeToken Program = ArtifactTypeToken.valueOf(52374361342017540L, "Program");
   public static final ArtifactTypeToken Country = ArtifactTypeToken.valueOf(4955822638391722788L, "Country");

   public static final ArtifactTypeToken AgileProgram = ArtifactTypeToken.valueOf(7844993694062372L, "Agile Program");
   public static final ArtifactTypeToken AgileProgramBacklog = ArtifactTypeToken.valueOf(7844994687943135L, "Agile Program Backlog");
   public static final ArtifactTypeToken AgileProgramBacklogItem = ArtifactTypeToken.valueOf(11221316461321645L, "Agile Program Backlog Item");
   public static final ArtifactTypeToken AgileProgramFeature = ArtifactTypeToken.valueOf(99876313545914L, "Agile Program Feature");
   public static final ArtifactTypeToken AgileStory = ArtifactTypeToken.valueOf(33216462134454L, "Agile Story");
   public static final ArtifactTypeToken AgileTeam = ArtifactTypeToken.valueOf(7553778770333667393L, "Agile Team");
   public static final ArtifactTypeToken AgileBacklog = ArtifactTypeToken.valueOf(7553335770333667393L, "Agile Backlog");
   public static final ArtifactTypeToken AgileSprint = ArtifactTypeToken.valueOf(9088615648290692675L, "Agile Sprint");
   public static final ArtifactTypeToken AgileFeatureGroup = ArtifactTypeToken.valueOf(560322181883393633L, "Agile Feature Group");

   public static final ArtifactTypeToken Insertion = ArtifactTypeToken.valueOf(1735587136604728792L, "Insertion");
   public static final ArtifactTypeToken InsertionActivity = ArtifactTypeToken.valueOf(3943415539127781884L, "Insertion Activity");

   public static final ArtifactTypeToken RuleDefinition = ArtifactTypeToken.valueOf(6370402109038303278L, "Rule Definition");

   // @formatter:on

   private AtsArtifactTypes() {
      // Constants
   }
}