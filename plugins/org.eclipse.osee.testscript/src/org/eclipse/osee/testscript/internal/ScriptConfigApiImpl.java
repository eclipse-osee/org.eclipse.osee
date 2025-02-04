/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.accessor.internal.ArtifactAccessorImpl;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.testscript.ScriptConfigApi;
import org.eclipse.osee.testscript.ScriptConfigToken;

public class ScriptConfigApiImpl implements ScriptConfigApi {
   private final OrcsApi orcsApi;
   private ArtifactAccessor<ScriptConfigToken> accessor;

   public ScriptConfigApiImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.setAccessor(new ScriptConfigAccessor(orcsApi));
   }

   private void setAccessor(ArtifactAccessor<ScriptConfigToken> scriptConfigAccessor) {
      this.accessor = scriptConfigAccessor;
   }

   @Override
   public ScriptConfigToken get(BranchId branchId) {
      try {
         List<ScriptConfigToken> allConfigs = (List<ScriptConfigToken>) this.accessor.getAll(branchId);
         if (allConfigs.isEmpty()) {
            return get(CoreBranches.COMMON);
         }
         if (allConfigs.size() > 1) {
            throw new OseeStateException("There is more than one Zenith configuration artifact on this branch.");
         }
         return allConfigs.get(0);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return ScriptConfigToken.SENTINEL;
      }
   }

   @Override
   public TransactionResult createScriptConfiguration(BranchId branchId) {
      TransactionResult txResult = new TransactionResult();
      try {
         List<ScriptConfigToken> allConfigs = (List<ScriptConfigToken>) this.accessor.getAll(branchId);
         if (!allConfigs.isEmpty()) {
            txResult.getResults().error("A configuration artifact already exists on this branch");
            return txResult;
         }
         Branch branch = this.orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getExactlyOne();
         List<ScriptConfigToken> parentConfigs =
            (List<ScriptConfigToken>) this.accessor.getAll(branch.getParentBranch());
         if (!parentConfigs.isEmpty()) {
            txResult.getResults().error(
               "A configuration artifact already exists on the parent branch. Update this branch to edit the configuration.");
            return txResult;
         }
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }

      ScriptConfigToken scriptConfigToken = this.get(CoreBranches.COMMON);

      ObjectMapper mapper = new ObjectMapper();
      TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi);

      TransactionBuilderData data = new TransactionBuilderData();
      data.setBranch(branchId.getIdString());
      data.setTxComment("Create Zenith Configuration");
      data.setCreateArtifacts(new LinkedList<>());
      CreateArtifact configArt = scriptConfigToken.createArtifact("configArt");
      data.getCreateArtifacts().add(configArt);

      try {
         TransactionBuilder tx = txBdf.loadFromJson(mapper.writeValueAsString(data));
         TransactionToken token = tx.commit();
         txResult.setTx(token);
      } catch (JsonProcessingException ex) {
         txResult.getResults().error("Error processing tx json");
      }

      return txResult;
   }

   private class ScriptConfigAccessor extends ArtifactAccessorImpl<ScriptConfigToken> {

      public ScriptConfigAccessor(OrcsApi orcsApi) {
         super(CoreArtifactTypes.ScriptConfiguration, orcsApi);
      }

   }

}
