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
package org.eclipse.osee.ats.rest.internal.convert;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Megumi Telles
 */
public class ConvertFavoriteBranchGuidToId extends AbstractConvertGuidToId {

   private int numChanges = 0;

   public ConvertFavoriteBranchGuidToId(Log logger, JdbcClient jdbcClient, OrcsApi orcsApi, IAtsServer atsServer) {
      super(logger, jdbcClient, orcsApi, atsServer);
   }

   @Override
   public String getName() {
      return "FavoriteBranchGuidToId";
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("ConvertFavoriteBranchGuidToId (required conversion)\n\n");
      data.append("Necessary for upgrading from OSEE 0.16.2 to 0.17.0\n");
      data.append("-- Converts a User's Favorite Branch Guid(s) to Id(s).\n\n");
      data.append("NOTE: This operation can be run multiple times\n");
      return data.toString();
   }

   @Override
   public void run(XResultData data, boolean reportOnly, AtsApi atsApi) {
      if (reportOnly) {
         data.log("REPORT ONLY - Changes not persisted\n");
      }
      try {
         QueryFactory queryFactory = getOrcsApi().getQueryFactory();
         TransactionBuilder tx = createTransactionBuilder();
         for (ArtifactReadable art : getUsersFavoriteBranch(queryFactory)) {
            convertAttributeToId(data, reportOnly, tx, art, art.getAttributes(CoreAttributeTypes.FavoriteBranch));
         }
         if (reportOnly) {
            data.log("\n" + numChanges + " Need to be Changed");
         } else {
            data.log("\n" + numChanges + " Changes Persisted");
            if (numChanges > 0) {
               tx.commit();
            }
         }
         numChanges = 0;
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Exception occurred while trying to convert branch guid to id");
      }
   }

   private void convertAttributeToId(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, ResultSet<? extends AttributeReadable<Object>> favBranchAttrValues) {
      for (AttributeReadable<Object> attr : favBranchAttrValues) {
         String value = attr.toString();
         if (GUID.isValid(value)) {
            convert(data, reportOnly, tx, art, attr, value);
         } else {
            data.logf(
               "Not a guid attribute value.  Actual value [%s] for artifact type [%s] name [%s] id [%s] NOT converted to id.\n \n",
               value, art.getArtifactType(), art.getName(), art.getGuid());
         }
      }
   }

   private void convert(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, AttributeReadable<Object> attr, String value) {
      BranchReadable branch = null;
      try {
         branch = getBranch(value);
      } catch (OseeCoreException ex) {
         getLogger().warn(ex, "No Branch found with value: [%s]", value);
      }
      if (branch != null) {
         addId(data, reportOnly, tx, art, attr, branch);
      } else {
         removeAttrForNonExistentBranch(data, reportOnly, tx, art, attr, value);
      }
   }

   private void addId(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, AttributeReadable<Object> attr, BranchReadable branch) {
      numChanges++;
      data.logf("Adding id attribute of value %s to artifact type [%s] name [%s] id [%s]\n", branch,
         art.getArtifactType(), art.getName(), art.getGuid());
      if (!reportOnly) {
         try {
            tx.setAttributeById(art, attr, branch.getIdString());
         } catch (OseeCoreException ex) {
            data.errorf(
               "Error building transaction for convert to id attribute of value %s for artifact type [%s] name [%s] id [%s]\n",
               branch, art.getArtifactType(), art.getName(), art.getGuid());
         }
      }
   }

   private void removeAttrForNonExistentBranch(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, AttributeReadable<Object> attr, String value) {
      try {
         data.logf("No Branch found with value [%s]. Recommend removing attribute.\n", value);
         if (!reportOnly) {
            tx.deleteByAttributeId(art, attr);
         }
      } catch (OseeCoreException ex) {
         data.errorf("Error building transaction to remove guid [%s] for branch that no longer exists\n", value);
      }
   }

   private ResultSet<ArtifactReadable> getUsersFavoriteBranch(QueryFactory queryFactory) {
      return queryFactory.fromBranch(atsServer.getAtsBranch()).andTypeEquals(CoreArtifactTypes.User).andExists(
         CoreAttributeTypes.FavoriteBranch).getResults();
   }

}
