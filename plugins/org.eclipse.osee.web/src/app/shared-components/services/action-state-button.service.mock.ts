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
import { of } from 'rxjs';
import { testBranchInfo } from '../../ple/plconfig/testing/mockBranchService';
import { testBranchActions, testCommitResponse, testDataResponse, testDataTransitionResponse, testDataUser, testNameValuePairArray, testnewActionResponse, testWorkFlow } from '../../ple/plconfig/testing/mockTypes';
import { actionableItem, PLConfigCreateAction} from '../../ple/plconfig/types/pl-config-actions';
import { ActionStateButtonService } from './action-state-button.service';

export const actionStateButtonServiceMock: Partial<ActionStateButtonService> = {
    branchAction: of(testBranchActions),
    branchWorkFlow: of(testWorkFlow),
    branchState: of(testBranchInfo),
    branchApproved: of('true'),
    teamsLeads: of(testNameValuePairArray),
    branchTransitionable: of('true'),
    addActionInitialStep: of(testDataUser),
    commitBranch(parentBranchId: string, body: { committer: string; archive: string; }) {
        return of(testCommitResponse)
    },
    branchApprovable: of('true'),
    branchCommitable: of('true'),
    doCommitBranch: of(testDataTransitionResponse),
    doTransition: of(testDataTransitionResponse),
    doApproveBranch: of(testDataResponse),
    doAddAction(value: PLConfigCreateAction, category: string) {
        return of(testnewActionResponse)
    },
    actionableItems:of([{
        id: "123",
        name:"First ARB"
      },
        {
          id: "456",              
          name: "Second ARB"
      }
      ])
}