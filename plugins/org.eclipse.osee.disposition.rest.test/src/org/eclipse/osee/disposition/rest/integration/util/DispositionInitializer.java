/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.integration.util;

import static org.eclipse.osee.disposition.rest.integration.util.DispositionTestUtil.SAW_Bld_1_FOR_DISPO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Angel Avila
 */
public class DispositionInitializer {

   private final OrcsApi orcsApi;
   private final DispoApi dispoApi;

   public DispositionInitializer(OrcsApi orcsApi, DispoApi dispoApi) {
      this.orcsApi = orcsApi;
      this.dispoApi = dispoApi;
   }

   public void initialize() throws Exception {

      orcsApi.getBranchOps().createWorkingBranch(SAW_Bld_1_FOR_DISPO, SystemUser.OseeSystem, SAW_Bld_1,
         ArtifactId.SENTINEL);

      // create Dispo Config Art
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         DemoUsers.Joe_Smith, "Create Dispo Config");
      ArtifactId createArtifact = tx.createArtifact(CoreArtifactTypes.GeneralData, DispoStrings.Dispo_Config_Art);
      StringBuffer sb = new StringBuffer(SAW_Bld_1.getIdString());
      sb.append(":");
      sb.append(SAW_Bld_1_FOR_DISPO.getIdString());
      sb.append("\n");
      sb.append(SAW_Bld_1.getIdString());
      sb.append(":");
      sb.append(SAW_Bld_1_FOR_DISPO.getIdString());
      tx.createAttribute(createArtifact, CoreAttributeTypes.GeneralStringData, sb.toString());
      tx.commit();

      // Creat Set and Item Arts
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName("DEMO SET");
      descriptor.setImportPath("c:");
      BranchId branch = SAW_Bld_1_FOR_DISPO;
      dispoApi.createDispoSet(branch, descriptor, DemoUsers.Joe_Smith.getIdString());
   }
}