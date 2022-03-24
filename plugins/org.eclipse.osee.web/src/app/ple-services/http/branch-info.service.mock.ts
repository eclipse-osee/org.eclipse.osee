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
import { of } from "rxjs";
import { testBranchInfo, testBranchListing } from "src/app/ple/plconfig/testing/mockBranchService";
import { response } from '../../ple/messaging/connection-view/mocks/Response.mock';
import { testCommitResponse, testDataResponse } from '../../ple/plconfig/testing/mockTypes';
import { BranchInfoService } from "./branch-info.service";

export const BranchInfoServiceMock: Partial<BranchInfoService> = {
    getBranch(id: string) {
        return of(testBranchInfo)
    },
    getBranches(type: string, category?: string, searchType?: boolean) {
        return of(testBranchListing)
    },
    commitBranch(branchId: string | number | undefined, parentBranchId: string | number | undefined, body: { committer: string, archive: string }) {
        return of(testCommitResponse)
    },
    setBranchCategory(branchId: string | number | undefined, category: string) {
        return of(testDataResponse)
      }
}