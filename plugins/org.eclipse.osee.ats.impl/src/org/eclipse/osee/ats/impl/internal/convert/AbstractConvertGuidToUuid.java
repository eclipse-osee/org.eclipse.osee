/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.convert;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.impl.internal.util.AtsUtilServer;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Megumi Telles
 */
public abstract class AbstractConvertGuidToUuid implements IAtsDatabaseConversion {

   private final IOseeDatabaseService dbService;
   private final OrcsApi orcsApi;

   public AbstractConvertGuidToUuid(IOseeDatabaseService dbService, OrcsApi orcsApi) {
      this.dbService = dbService;
      this.orcsApi = orcsApi;
   }

   protected BranchReadable getBranch(String guid) throws OseeCoreException {
      return orcsApi.getQueryFactory(null).branchQuery().andUuids(getBranchIdLegacy(guid)).getResults().getExactlyOne();
   }

   protected TransactionBuilder createTransactionBuilder() throws OseeCoreException {
      TransactionFactory txFactory = getOrcsApi().getTransactionFactory(null);
      Conditions.checkNotNull(txFactory, "transaction factory");
      return txFactory.createTransaction(COMMON,
         AtsUtilServer.getArtifactByGuid(getOrcsApi(), SystemUser.OseeSystem.getGuid()), getName());
   }

   private final String SELECT_BRANCH_ID_BY_GUID = "select branch_id from osee_branch where branch_guid = ?";

   /**
    * Temporary method till all code uses branch uuid. Remove after 0.17.0
    */
   private long getBranchIdLegacy(String branchGuid) {
      Long longId = dbService.runPreparedQueryFetchObject(0L, SELECT_BRANCH_ID_BY_GUID, branchGuid);
      Conditions.checkExpressionFailOnTrue(longId <= 0, "Error getting branch_id for branch: [%s]", branchGuid);
      return longId;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

}
