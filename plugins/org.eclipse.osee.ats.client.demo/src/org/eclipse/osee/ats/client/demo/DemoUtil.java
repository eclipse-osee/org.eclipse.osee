/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;

public class DemoUtil {

   private static String atsIds;

   private DemoUtil() {
      // Utility class
   }

   public static void checkDbInitSuccess() throws OseeCoreException {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
   }

   public static void checkDbInitAndPopulateSuccess() throws OseeCoreException {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
      if (!isPopulateDbSuccessful()) {
         throw new OseeStateException("PopulateDb must be successful to continue");
      }
   }

   public static boolean isDbInitSuccessful() throws OseeCoreException {
      return OseeInfo.getValue("DbInitSuccess").equals("true");
   }

   public static void setDbInitSuccessful(boolean success) throws OseeCoreException {
      OseeInfo.setValue("DbInitSuccess", String.valueOf(success));
   }

   public static boolean isPopulateDbSuccessful() throws OseeCoreException {
      return OseeInfo.getValue("PopulateSuccessful").equals("true");
   }

   public static void setPopulateDbSuccessful(boolean success) throws OseeCoreException {
      OseeInfo.setValue("PopulateSuccessful", String.valueOf(success));
   }

   public static TeamWorkFlowArtifact getSawCodeCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Commited_Code_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawTestCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Commited_Test_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawReqCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Commited_Req_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawSWDesignCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Commited_SWDesign_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawCodeUnCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_UnCommited_Code_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawTestUnCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_UnCommited_Test_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawReqUnCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_UnCommited_Req_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawSWDesignUnCommittedWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(
         DemoArtifactToken.SAW_UnCommited_SWDesign_TeamWf);
   }

   public static TeamWorkFlowArtifact getSwDesignNoBranchWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawCodeNoBranchWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_NoBranch_Code_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawTestNoBranchWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_NoBranch_Test_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawReqNoBranchWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_NoBranch_Req_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawSWDesignNoBranchWf() throws OseeCoreException {
      return (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf);
   }

   public static Collection<TeamWorkFlowArtifact> getSawCommittedTeamWfs() {
      return Arrays.asList(DemoUtil.getSawTestCommittedWf(), DemoUtil.getSawReqCommittedWf(),
         DemoUtil.getSawCodeCommittedWf());
   }

   public static Collection<TeamWorkFlowArtifact> getSawUnCommittedTeamWfs() {
      return Arrays.asList(DemoUtil.getSawTestUnCommittedWf(), DemoUtil.getSawReqUnCommittedWf(),
         DemoUtil.getSawCodeUnCommittedWf(), DemoUtil.getSawSWDesignUnCommittedWf());
   }

   public static String getSawAtsIds() {
      if (atsIds == null) {
         atsIds = Collections.toString(",", AtsObjects.toAtsIds(getSawCommittedTeamWfs()));
      }
      return atsIds;
   }

}
