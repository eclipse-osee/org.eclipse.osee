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
import { iif, of } from 'rxjs';
import { PlConfigCurrentBranchService } from '../services/pl-config-current-branch.service';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import { editConfiguration } from '../types/pl-config-configurations';
import { modifyFeature } from '../types/pl-config-features';
import { testBranchApplicability } from './mockBranchService';
import {
  testBranchActions,
  testCommitResponse,
  testDataPlConfigBranchListingBranch,
  testDataResponse,
  testWorkFlow,
} from './mockTypes';

export const plCurrentBranchServiceMock: Partial<PlConfigCurrentBranchService> =
  {
    get branchState() {
      return of(testDataPlConfigBranchListingBranch);
    },
    get branchAction() {
      return of(testBranchActions);
    },
    commitBranch(
      parentBranchId: string | number | undefined,
      body: { committer: string; archive: string }
    ) {
      return of(testCommitResponse);
    },
    get branchApplicability() {
      return of(testBranchApplicability);
    },
    get branchWorkFlow() {
      return of(testWorkFlow);
    },
    get headers() {
      return of([
        'feature',
        'Product C',
        'Product D',
        'abGroup',
        'Product A',
        'Product B',
      ]);
    },
    get groupList() {
      return of(testBranchApplicability.groups);
    },
    modifyFeature(feature: modifyFeature) {
      return of(testDataResponse);
  },
  findViewByName(viewName: string) {
      return of(testBranchApplicability.views[0])
  },
  findViewById(viewId: string) {
    return of(testBranchApplicability.views[0])
  },
  findGroup(groupName: string) {
    return of(testBranchApplicability.groups[0])
  },
  isACfgGroup(name: string) {
    return iif(()=>name.includes('group')||name.includes('Group'),of(true),of(false))
  },
  editConfigurationDetails(body: editConfiguration) {
    return of(testDataResponse);
  },
  updateConfigurationGroup(cfgGroup: ConfigurationGroupDefinition) {
    return of(testDataResponse);
  },
  branchApproved: of('false'),
  teamsLeads:of([{id:'61106791',name:'Joe Smith'}])
  };
