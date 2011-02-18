/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;

public final class AtsArtifactTypes {

   // @formatter:off
   public static final IArtifactType Action = TokenFactory.createArtifactType("AAMFDhY_rns71KvX14QA", "Action");
   public static final IArtifactType ActionableItem = TokenFactory.createArtifactType("AAMFDhW2LmhtRFsVyzwA", "Actionable Item");
   public static final IArtifactType DecisionReview = TokenFactory.createArtifactType("AAMFDhfrdR7BGTL7H_wA", "Decision Review");
   public static final IArtifactType PeerToPeerReview = TokenFactory.createArtifactType("AAMFDhh_300dpgmNtRAA", "PeerToPeer Review");
   public static final IArtifactType Task = TokenFactory.createArtifactType("AAMFDhbTAAB6h+06fuAA", "Task");
   public static final IArtifactType StateMachineArtifact = TokenFactory.createArtifactType("ABMfXC+LFBn31ZZbvjAA", "Abstract State Machine Artifact");
   public static final IArtifactType ReviewArtifact = TokenFactory.createArtifactType("ABMa6P4TwzXA1b8K3RAA", "Abstract Review Artifact");
   public static final IArtifactType TeamDefinition = TokenFactory.createArtifactType("AAMFDhUrlytusKbaQGAA", "Team Definition");
   public static final IArtifactType TeamWorkflow = TokenFactory.createArtifactType("AAMFDhSiF2OD+wiUqugA", "Team Workflow");
   public static final IArtifactType Version = TokenFactory.createArtifactType("AAMFDhder0oETnv14xQA", "Version");
   public static final IArtifactType Goal = TokenFactory.createArtifactType("ABMgU119UjI_Q23Yu+gA", "Goal");
   public static final IArtifactType AtsArtifact = TokenFactory.createArtifactType("ABMaLS0jvw92SE+4ZJQA", "ats.Ats Artifact");
   public static final IArtifactType WorkDefinition = TokenFactory.createArtifactType("AGrU8fWa3AJ6uoWYP7wA", "Work Definition");
   // @formatter:on

   private AtsArtifactTypes() {
      // Constants
   }
}