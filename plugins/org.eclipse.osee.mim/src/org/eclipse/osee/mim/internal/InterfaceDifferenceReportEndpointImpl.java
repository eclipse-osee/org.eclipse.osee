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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.InterfaceDifferenceReportApi;
import org.eclipse.osee.mim.InterfaceDifferenceReportEndpoint;
import org.eclipse.osee.mim.types.MimChangeSummary;
import org.eclipse.osee.mim.types.MimDifferenceReport;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceDifferenceReportEndpointImpl implements InterfaceDifferenceReportEndpoint {

   private final BranchId branch;
   private final InterfaceDifferenceReportApi interfaceDifferenceReportApi;

   public InterfaceDifferenceReportEndpointImpl(BranchId branch, InterfaceDifferenceReportApi interfaceDifferenceReportApi) {
      this.branch = branch;
      this.interfaceDifferenceReportApi = interfaceDifferenceReportApi;
   }

   @Override
   public MimDifferenceReport getDifferenceReport(BranchId branch2) {
      return interfaceDifferenceReportApi.getDifferenceReport(branch, branch2);
   }

   @Override
   public MimChangeSummary getChangeSummary(BranchId branch2, ArtifactId view) {
      if (view == null) {
         view = ArtifactId.SENTINEL;
      }
      return interfaceDifferenceReportApi.getChangeSummary(branch, branch2, view);
   }

}
