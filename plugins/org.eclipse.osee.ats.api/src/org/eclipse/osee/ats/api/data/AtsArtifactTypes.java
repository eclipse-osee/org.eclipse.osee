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
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactTypes {

   // @formatter:off
   public static final ArtifactTypeToken AtsConfigObject = TokenFactory.createArtifactType(801, "ats.Ats Config Artifact");
   public static final ArtifactTypeToken Action = TokenFactory.createArtifactType(67, "Action");
   public static final ArtifactTypeToken ActionableItem = TokenFactory.createArtifactType(69, "Actionable Item");
   public static final ArtifactTypeToken Configuration = TokenFactory.createArtifactType(93802085744703L, "ATS Configuration");
   public static final ArtifactTypeToken DecisionReview = TokenFactory.createArtifactType(66, "Decision Review");
   public static final ArtifactTypeToken PeerToPeerReview = TokenFactory.createArtifactType(65, "PeerToPeer Review");
   public static final ArtifactTypeToken Task = TokenFactory.createArtifactType(74, "Task");
   public static final ArtifactTypeToken AbstractWorkflowArtifact = TokenFactory.createArtifactType(71, "Abstract State Machine Artifact");
   public static final ArtifactTypeToken ReviewArtifact = TokenFactory.createArtifactType(64, "Abstract Review Artifact");
   public static final ArtifactTypeToken TeamDefinition = TokenFactory.createArtifactType(68, "Team Definition");
   public static final ArtifactTypeToken ResponsibleTeam = TokenFactory.createArtifactType(8943243743202487405L, "Responsible Team");
   public static final ArtifactTypeToken TeamWorkflow = TokenFactory.createArtifactType(73, "Team Workflow");
   public static final ArtifactTypeToken Version = TokenFactory.createArtifactType(70, "Version");
   public static final ArtifactTypeToken Goal = TokenFactory.createArtifactType(72, "Goal");
   public static final ArtifactTypeToken AtsArtifact = TokenFactory.createArtifactType(63, "ats.Ats Artifact");
   public static final ArtifactTypeToken WorkDefinition = TokenFactory.createArtifactType(62, "Work Definition");
   public static final ArtifactTypeToken WorkPackage = TokenFactory.createArtifactType(802, "Work Package");
   public static final ArtifactTypeToken Program = TokenFactory.createArtifactType(52374361342017540L, "Program");
   public static final ArtifactTypeToken Country = TokenFactory.createArtifactType(4955822638391722788L, "Country");

   public static final ArtifactTypeToken AgileProgram = TokenFactory.createArtifactType(7844993694062372L, "Agile Program");
   public static final ArtifactTypeToken AgileProgramBacklog = TokenFactory.createArtifactType(7844994687943135L, "Agile Program Backlog");
   public static final ArtifactTypeToken AgileProgramBacklogItem = TokenFactory.createArtifactType(11221316461321645L, "Agile Program Backlog Item");
   public static final ArtifactTypeToken AgileProgramFeature = TokenFactory.createArtifactType(99876313545914L, "Agile Program Feature");
   public static final ArtifactTypeToken AgileStory = TokenFactory.createArtifactType(33216462134454L, "Agile Story");
   public static final ArtifactTypeToken AgileTeam = TokenFactory.createArtifactType(7553778770333667393L, "Agile Team");
   public static final ArtifactTypeToken AgileBacklog = TokenFactory.createArtifactType(7553335770333667393L, "Agile Backlog");
   public static final ArtifactTypeToken AgileSprint = TokenFactory.createArtifactType(9088615648290692675L, "Agile Sprint");
   public static final ArtifactTypeToken AgileFeatureGroup = TokenFactory.createArtifactType(560322181883393633L, "Agile Feature Group");

   public static final ArtifactTypeToken Insertion = TokenFactory.createArtifactType(1735587136604728792L, "Insertion");
   public static final ArtifactTypeToken InsertionActivity = TokenFactory.createArtifactType(3943415539127781884L, "Insertion Activity");

   public static final ArtifactTypeToken RuleDefinition = TokenFactory.createArtifactType(6370402109038303278L, "Rule Definition");

   // @formatter:on

   private AtsArtifactTypes() {
      // Constants
   }
}