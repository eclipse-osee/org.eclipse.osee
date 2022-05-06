/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimReportsEndpoint;
import org.eclipse.osee.mim.types.MimReportToken;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan Baldwin
 */
public class MimReportsEndpointImpl implements MimReportsEndpoint {

   private final MimApi mimApi;

   public MimReportsEndpointImpl(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Override
   public List<MimReportToken> getReports() {
      List<MimReportToken> reports = new LinkedList<>();
      for (ArtifactReadable art : mimApi.getOrcsApi().getQueryFactory().fromBranch(BranchId.valueOf(570L)).andIsOfType(
         CoreArtifactTypes.MimReport).asArtifacts()) {
         if (art.isValid()) {
            reports.add(new MimReportToken(art));
         }
      }
      return reports;
   }

}