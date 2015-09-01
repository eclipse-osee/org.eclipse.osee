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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactTypes {

   // @formatter:off
   public static final IArtifactType Action = TokenFactory.createArtifactType(0x0000000000000043L, "Action");
   public static final IArtifactType ActionableItem = TokenFactory.createArtifactType(0x0000000000000045L, "Actionable Item");
   public static final IArtifactType Configuration = TokenFactory.createArtifactType(0x000055500000003FL, "ATS Configuration");
   public static final IArtifactType DecisionReview = TokenFactory.createArtifactType(0x0000000000000042L, "Decision Review");
   public static final IArtifactType PeerToPeerReview = TokenFactory.createArtifactType(0x0000000000000041L, "PeerToPeer Review");
   public static final IArtifactType Task = TokenFactory.createArtifactType(0x000000000000004AL, "Task");
   public static final IArtifactType AbstractWorkflowArtifact = TokenFactory.createArtifactType(0x0000000000000047L, "Abstract State Machine Artifact");
   public static final IArtifactType ReviewArtifact = TokenFactory.createArtifactType(0x0000000000000040L, "Abstract Review Artifact");
   public static final IArtifactType TeamDefinition = TokenFactory.createArtifactType(0x0000000000000044L, "Team Definition");
   public static final IArtifactType TeamWorkflow = TokenFactory.createArtifactType(0x0000000000000049L, "Team Workflow");
   public static final IArtifactType Version = TokenFactory.createArtifactType(0x0000000000000046L, "Version");
   public static final IArtifactType Goal = TokenFactory.createArtifactType(0x0000000000000048L, "Goal");
   public static final IArtifactType AtsArtifact = TokenFactory.createArtifactType(0x000000000000003FL, "ats.Ats Artifact");
   public static final IArtifactType WorkDefinition = TokenFactory.createArtifactType(0x000000000000003EL, "Work Definition");
   public static final IArtifactType WorkPackage = TokenFactory.createArtifactType(0x0000000000000322L, "Work Definition");
   public static final IArtifactType Program = TokenFactory.createArtifactType(0x0000BA123443210004L, "Program");
   public static final IArtifactType Country = TokenFactory.createArtifactType(0x44C69E6EBB2D8324L, "Country");

   public static final IArtifactType AgileTeam = TokenFactory.createArtifactType(0x68D469C51DA01041L, "Agile Team");
   public static final IArtifactType AgileFeatureGroup = TokenFactory.createArtifactType(0x07C6AA0E42EE7661L, "Agile Feature Group");
   public static final IArtifactType AgileSprint = TokenFactory.createArtifactType(0x7E213FC7506C5E43L, "Agile Sprint");

   public static final IArtifactType Insertion = TokenFactory.createArtifactType(0x18160B4E220FEDD8L, "Insertion");
   public static final IArtifactType InsertionActivity = TokenFactory.createArtifactType(0x36B9D38A2B7789FCL, "Insertion Activity");

   public static final IArtifactType RuleDefinition = TokenFactory.createArtifactType   (0x586836F761A0982EL, "Rule Definition");

   // @formatter:on

   private AtsArtifactTypes() {
      // Constants
   }
}