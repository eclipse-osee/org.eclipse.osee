/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.define.operations.reports;

import java.util.List;
import java.util.Objects;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.api.reports.ReportsOperations;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Loren K. Ashley
 */

public class ReportsOperationsImpl implements ReportsOperations {

   /**
    * Saves the single instance of the {@link ReportsOperationsImpl}.
    */

   private static ReportsOperationsImpl reportsOperationsImpl = null;

   /**
    * Gets or creates the single instance of the {@link ReportsOperationsImpl} class.
    *
    * @param orcsApi the {@link OrcsApi} handle.
    * @param defineOperations the {@link DefineOperations} handle.
    * @return the single {@link ReportsOperationsImpl} class.
    */

   public synchronized static ReportsOperationsImpl create(OrcsApi orcsApi, DefineOperations defineOperations) {
      //@formatter:off
      return
         Objects.isNull( ReportsOperationsImpl.reportsOperationsImpl )
            ? ReportsOperationsImpl.reportsOperationsImpl =
                 new ReportsOperationsImpl(orcsApi,defineOperations)
            : ReportsOperationsImpl.reportsOperationsImpl;
      //@formatter:on
   }

   /**
    * Sets the statically saved instance of the {@link ReportsOperationsImpl} class to <code>null</code>.
    */

   public synchronized static void free() {
      ReportsOperationsImpl.reportsOperationsImpl = null;
   }

   private final OrcsApi orcsApi;
   private final DefineOperations defineOperations;

   private ReportsOperationsImpl(OrcsApi orcsApi, DefineOperations defineOperations) {
      this.orcsApi = orcsApi;
      this.defineOperations = defineOperations;
   }

   @Override
   public StreamingOutput applicabilityImpact(BranchId branch, String publish, List<ArtifactTypeToken> artTypes, List<AttributeTypeToken> attrTypes) {

      boolean publishUpdates = (publish.equals("true")) ? true : false;

      Branch branchArt =
         this.orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getAtMostOneOrDefault(Branch.SENTINEL);

      StreamingOutput streamingOutput =
         new FeatureImpactStreamingOutput(branchArt, orcsApi, defineOperations, publishUpdates, artTypes, attrTypes);

      return streamingOutput;
   }

}
