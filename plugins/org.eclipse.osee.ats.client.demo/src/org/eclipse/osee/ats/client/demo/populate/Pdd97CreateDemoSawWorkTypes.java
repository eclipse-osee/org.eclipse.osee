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
package org.eclipse.osee.ats.client.demo.populate;

import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.client.demo.config.DemoCsci;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class Pdd97CreateDemoSawWorkTypes {

   public void run() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Add SAW Work Types");
      Artifact sawProgram = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Program);
      for (DemoCsci csci : DemoCsci.values()) {
         sawProgram.addAttribute(AtsAttributeTypes.CSCI, csci.name());
      }
      sawProgram.persist(transaction);
      Artifact sawTeamDef = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_SW);
      for (Artifact child : sawTeamDef.getChildren()) {
         child.setSoleAttributeValue(AtsAttributeTypes.ProgramUuid, sawProgram);
         if (child.getName().contains("Code")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Code.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.DP.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         } else if (child.getName().contains("Test")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Test.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.DP.name());
         } else if (child.getName().contains("Requirements")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Requirements.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         } else if (child.getName().contains("Design")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.SW_Design.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         } else if (child.getName().contains("HW")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Hardware.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         }
         child.persist(transaction);
      }

      Artifact sawTestAi = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Test_AI);
      sawTestAi.setSoleAttributeValue(AtsAttributeTypes.ProgramUuid, sawProgram);
      sawTestAi.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Test.name());
      sawTestAi.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.DP.name());
      sawTestAi.persist(transaction);

      Artifact sawCodeAi = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Code_AI);
      sawCodeAi.setSoleAttributeValue(AtsAttributeTypes.ProgramUuid, sawProgram);
      sawCodeAi.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Code.name());
      sawCodeAi.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
      sawCodeAi.persist(transaction);

      transaction.execute();
   }

}
