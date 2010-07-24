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
import org.eclipse.osee.framework.core.data.NamedIdentity;

public class AtsArtifactTypes extends NamedIdentity implements IArtifactType {
   public static final AtsArtifactTypes Action = new AtsArtifactTypes("AAMFDhY_rns71KvX14QA", "Action");
   public static final AtsArtifactTypes ActionableItem =
      new AtsArtifactTypes("AAMFDhW2LmhtRFsVyzwA", "Actionable Item");
   public static final AtsArtifactTypes DecisionReview =
      new AtsArtifactTypes("AAMFDhfrdR7BGTL7H_wA", "Decision Review");
   public static final AtsArtifactTypes PeerToPeerReview = new AtsArtifactTypes("AAMFDhh_300dpgmNtRAA",
      "PeerToPeer Review");
   public static final AtsArtifactTypes Task = new AtsArtifactTypes("AAMFDhbTAAB6h+06fuAA", "Task");
   public static final AtsArtifactTypes TeamDefinition =
      new AtsArtifactTypes("AAMFDhUrlytusKbaQGAA", "Team Definition");
   public static final AtsArtifactTypes TeamWorkflow = new AtsArtifactTypes("AAMFDhSiF2OD+wiUqugA", "Team Workflow");
   public static final AtsArtifactTypes Version = new AtsArtifactTypes("AAMFDhder0oETnv14xQA", "Version");
   public static final AtsArtifactTypes Goal = new AtsArtifactTypes("ABMgU119UjI_Q23Yu+gA", "Goal");

   private AtsArtifactTypes(String guid, String name) {
      super(guid, name);
   }
}