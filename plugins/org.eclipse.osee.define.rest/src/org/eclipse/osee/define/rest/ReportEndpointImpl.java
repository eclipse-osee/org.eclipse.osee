/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.rest;

import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.ReportEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author David W. Miller
 */
public final class ReportEndpointImpl implements ReportEndpoint {

   private final DefineApi defineApi;

   public ReportEndpointImpl(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   @Override
   public Response getReportFromTemplate(BranchId branch, ArtifactId view, ArtifactId templateArt) {
      return defineApi.getReportOperations().getReportFromTemplate(branch, view, templateArt);
   }

}
