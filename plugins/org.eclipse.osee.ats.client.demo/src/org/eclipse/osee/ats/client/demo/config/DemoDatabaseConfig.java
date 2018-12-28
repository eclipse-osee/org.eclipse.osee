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
package org.eclipse.osee.ats.client.demo.config;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.config.AtsDatabaseConfig;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.util.AtsGroup;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Initialization class that will load configuration information for a sample DB.
 *
 * @author Donald G. Dunne
 */
public class DemoDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() {

      AtsWorkDefinitionSheetProviders.initializeDatabase(new XResultData(false), "demo");
      TestUtil.setDemoDb(true);

      configureForParallelCommit();

      AtsGroup.AtsTempAdmin.addMember(UserManager.getUser(DemoUsers.Joe_Smith));
      AtsGroup.AtsTempAdmin.getArtifact().persist("Set Joe as Temp Admin");

      AtsDatabaseConfig.organizePrograms(AtsArtifactTypes.Program, DemoArtifactToken.DemoPrograms);

      AtsClientService.get().setConfigValue(AtsUtil.SINGLE_SERVER_DEPLOYMENT, "true");
      // Reload caches cause Demo sheet import added new users
      AtsClientService.getConfigEndpoint().getWithPend();
      AtsClientService.get().getConfigService().getConfigurations();
   }

   /**
    * Configure SAW_Bld_1 and SAW_Bld_2 for parallel commit, including recursive setup where SAW_Bld_1 needs to be
    * committed to SAW_Bld_1 and SAW_Bld_2 and SAW_Bld_2 needs to be committed to SAW_Bld_2 and SAW_Bld_1
    */
   private void configureForParallelCommit() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("configureForParallelCommit");

      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_3);

      changes.relate(sawBld1Ver, AtsRelationTypes.ParallelVersion_Child, sawBld2Ver);

      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld1Ver);
      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld3Ver);

      changes.execute();
   }
}
