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

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimReportsEndpoint;
import org.eclipse.osee.mim.types.MimReportToken;
import org.eclipse.osee.mim.types.NodeTraceReportItem;

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
      return mimApi.getMimArtifactsApi().getMimReports();
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterface(BranchId branch, long pageNum, long pageSize) {
      return mimApi.getMimReportsApi().getAllRequirementsToInterface(branch, pageNum, pageSize);
   }

   @Override
   public int getCountRequirementsToInterface(BranchId branch) {
      return mimApi.getMimReportsApi().getCountRequirementsToInterface(branch);
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch, long pageNum, long pageSize) {
      return mimApi.getMimReportsApi().getAllInterfaceToRequirements(branch, pageNum, pageSize);
   }

   @Override
   public int getCountInterfaceToRequirements(BranchId branch) {
      return mimApi.getMimReportsApi().getCountInterfaceToRequirements(branch);
   }

   @Override
   public NodeTraceReportItem getInterfacesFromRequirement(BranchId branch, ArtifactId artId) {
      return mimApi.getMimReportsApi().getInterfacesFromRequirement(branch, artId);
   }

   @Override
   public NodeTraceReportItem getRequirementsFromInterface(BranchId branch, ArtifactId artId) {
      return mimApi.getMimReportsApi().getRequirementsFromInterface(branch, artId);
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch, long pageNum,
      long pageSize) {
      return mimApi.getMimReportsApi().getAllRequirementsToInterfaceWithNoMatch(branch, pageNum, pageSize);
   }

   @Override
   public int getCountRequirementsToInterfaceWithNoMatch(BranchId branch) {
      return mimApi.getMimReportsApi().getCountRequirementsToInterfaceWithNoMatch(branch);
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(BranchId branch, long pageNum,
      long pageSize) {
      return mimApi.getMimReportsApi().getAllInterfaceToRequirementsWithNoMatch(branch, pageNum, pageSize);
   }

   @Override
   public int getCountInterfaceToRequirementsWithNoMatch(BranchId branch) {
      return mimApi.getMimReportsApi().getCountInterfaceToRequirementsWithNoMatch(branch);
   }

}