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
package org.eclipse.osee.ats.rest.internal.report;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.report.AtsReportEndpointApi;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AtsReportEndpointImpl implements AtsReportEndpointApi {

   private final AtsApi atsApi;

   public AtsReportEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getAttrDiffReport(String date, String artTypeId, String attrTypeIds) {
      ArtifactTypeToken useArtType = atsApi.tokenService().getArtifactType(Long.valueOf(artTypeId));
      List<AttributeTypeToken> useAttrTypes = new ArrayList<>();
      for (String attrTypeId : attrTypeIds.split(",")) {
         AttributeTypeToken useAttrType = atsApi.tokenService().getAttributeType(Long.valueOf(attrTypeId));
         useAttrTypes.add(useAttrType);
      }
      return (new AtsReportOperations(atsApi)).getAttrDiffReport(date, useArtType, useAttrTypes);
   }

   @Override
   public XResultData getRestCoverageReport() {
      AtsRestCoverageReport rpt = new AtsRestCoverageReport(atsApi);
      return rpt.run();
   }

}