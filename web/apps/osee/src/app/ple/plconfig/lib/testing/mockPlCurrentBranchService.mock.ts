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
import { MockXResultData } from '@osee/shared/testing';
import { iif, of } from 'rxjs';
import { PlConfigCurrentBranchService } from '../services/pl-config-current-branch.service';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import { editConfiguration } from '../types/pl-config-configurations';
import { modifyFeature } from '../types/pl-config-features';
import { testBranchApplicability } from './mockBranchService';

export const plCurrentBranchServiceMock: Partial<PlConfigCurrentBranchService> =
	{
		modifyFeature(feature: modifyFeature) {
			return of(MockXResultData);
		},
		getView(viewId: string) {
			return of(testBranchApplicability.views[0]);
		},
		editConfigurationDetails(body: editConfiguration) {
			return of(MockXResultData);
		},
		updateConfigurationGroup(cfgGroup: ConfigurationGroupDefinition) {
			return of(MockXResultData);
		},
		getFeatureById(featureId: string) {
			return of(testBranchApplicability.features[0]);
		},
		getCfgGroupDetail(cfgGroup: string, useDiffs: boolean) {
			return of(testBranchApplicability.groups[0]);
		},
		getCfgGroupsForView(viewId: string) {
			return of([]);
		},
		getViewsByIds(viewIds: string[]) {
			return of([]);
		},
		features: of(testBranchApplicability.features),
		applicabilityTableData: of({
			table: [],
			headers: [],
			headerLengths: [],
		}),
		applicabilityTableDataCount: of(100),
	};
