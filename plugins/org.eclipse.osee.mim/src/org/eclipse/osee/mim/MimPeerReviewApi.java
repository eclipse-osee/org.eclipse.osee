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
package org.eclipse.osee.mim;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.ApplyResult;
import org.eclipse.osee.mim.types.PeerReviewApplyData;

/**
 * @author Audrey Denk
 */
public interface MimPeerReviewApi {
   BranchId resetPeerReviewBranch(BranchId prBranch);
   ApplyResult applyWorkingBranches(BranchId prBranch, PeerReviewApplyData data);
   List<BranchId> getAppliedBranches(BranchId prBranch);
   }
