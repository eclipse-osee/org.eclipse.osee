/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.internal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.IcdEndpoint;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * A new instance of this REST endpoint is created for each REST call so this class does not require a thread-safe
 * design
 *
 * @author Audrey Denk
 */
public class IcdEndpointImpl implements IcdEndpoint {

   private final BranchId branch;
   private final ArtifactId viewId;
   private final OrcsApi orcsApi;
   private final MimApi mimApi;
   private final ArtifactId connectionId;

   public IcdEndpointImpl(BranchId branch, ArtifactId viewId, ArtifactId connectionId, MimApi mimApi) {
      this.branch = branch;
      this.viewId = viewId;
      this.mimApi = mimApi;
      this.connectionId = connectionId;
      this.orcsApi = mimApi.getOrcsApi();
   }

   @Override
   public Response getIcd(boolean diff) {
      StreamingOutput streamingOutput = new IcdStreamingOutput(mimApi, branch, viewId, connectionId, diff);
      String connectionName = orcsApi.getQueryFactory().fromBranch(branch).andId(connectionId).asArtifact().getName();
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "InterfaceWorkbook_" + connectionName + ".xls");
      return builder.build();
   }

}