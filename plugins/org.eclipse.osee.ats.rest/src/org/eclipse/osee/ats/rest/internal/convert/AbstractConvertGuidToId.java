/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.rest.internal.convert;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Megumi Telles
 */
public abstract class AbstractConvertGuidToId implements IAtsDatabaseConversion {

   private static final String SELECT_BRANCH_ID_BY_GUID = "select branch_id from osee_branch where branch_guid = ?";

   protected final Log logger;
   protected final JdbcClient jdbcClient;
   protected final OrcsApi orcsApi;
   protected final AtsApi atsApi;

   public AbstractConvertGuidToId(Log logger, JdbcClient jdbcClient, OrcsApi orcsApi, AtsApi atsApi) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
   }

   protected OrcsApi getOrcsApi() {
      return orcsApi;
   }

   protected Log getLogger() {
      return logger;
   }

   protected JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   protected BranchId getBranch(String guid) {
      return orcsApi.getQueryFactory().branchQuery().andId(getBranchIdLegacy(guid)).getResultsAsId().getExactlyOne();
   }

   protected TransactionBuilder createTransactionBuilder() {
      TransactionFactory txFactory = getOrcsApi().getTransactionFactory();
      Conditions.checkNotNull(txFactory, "transaction factory");
      return txFactory.createTransaction(COMMON, getName());
   }

   /**
    * Temporary method till all code uses branch id. Remove after 0.17.0
    */
   private BranchId getBranchIdLegacy(String branchGuid) {
      BranchId branch = getJdbcClient().fetch(BranchId.SENTINEL, SELECT_BRANCH_ID_BY_GUID, branchGuid);
      Conditions.checkExpressionFailOnTrue(branch.isInvalid(), "Error getting branch_id for branch: [%s]", branchGuid);
      return branch;
   }
}