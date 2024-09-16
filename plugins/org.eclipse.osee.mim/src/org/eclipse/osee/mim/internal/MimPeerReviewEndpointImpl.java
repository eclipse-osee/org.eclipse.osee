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
package org.eclipse.osee.mim.internal;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimPeerReviewEndpoint;
import org.eclipse.osee.mim.types.ApplyResult;
import org.eclipse.osee.mim.types.PeerReviewApplyData;

/**
 * @author Audrey Denk
 */
public class MimPeerReviewEndpointImpl implements MimPeerReviewEndpoint {

   private final MimApi mimApi;

   public MimPeerReviewEndpointImpl(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Override
   public ApplyResult applyWorkingBranches(BranchId prBranch, PeerReviewApplyData data) {
      return mimApi.getMimPeerReviewApi().applyWorkingBranches(prBranch, data);
   }  
   
   @Override
   public List<BranchId> getAppliedBranches(BranchId prBranch) {
      return mimApi.getMimPeerReviewApi().getAppliedBranches(prBranch);
   }

}
