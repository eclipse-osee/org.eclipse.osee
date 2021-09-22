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
import { PlConfigCurrentBranchService } from "../services/pl-config-current-branch.service";
import { testBranchApplicability } from "./mockBranchService";
import { testBranchActions, testCommitResponse, testDataPlConfigBranchListingBranch, testWorkFlow } from "./mockTypes";

export const plCurrentBranchServiceMock: Partial<PlConfigCurrentBranchService> = {
    
    
          get branchState() {
            return of(testDataPlConfigBranchListingBranch)
          },
          get branchAction() {
            return of(testBranchActions);
          },  
          commitBranch(parentBranchId: string | number | undefined, body: { committer: string, archive: string }) {    
            return of(testCommitResponse);
          },
          get branchApplicability() {
            return of(testBranchApplicability);
          },
          get branchWorkFlow() {
            return of(testWorkFlow);
          }
           
          }