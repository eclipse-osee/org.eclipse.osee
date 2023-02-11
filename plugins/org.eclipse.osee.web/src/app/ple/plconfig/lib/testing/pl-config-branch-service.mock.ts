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
import { Observable, of } from 'rxjs';
import { PlConfigBranchService } from '../services/pl-config-branch-service.service';
import { NamedId } from '@osee/shared/types';
import {
	PlConfigApplicUIBranchMapping,
	PlConfigApplicUIBranchMappingImpl,
} from '../types/pl-config-applicui-branch-mapping';
import { cfgGroup } from '../types/pl-config-branch';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import {
	configuration,
	editConfiguration,
	configurationGroup,
} from '../types/pl-config-configurations';
import { writeFeature, modifyFeature } from '../types/pl-config-features';
import { branch, response } from '@osee/shared/types';

export const PlConfigBranchServiceMock: Partial<PlConfigBranchService> = {
	getBranches: function (type: string): Observable<branch[]> {
		throw new Error('Function not implemented.');
	},
	getBranchApplicability: function (
		id: string | number | undefined
	): Observable<PlConfigApplicUIBranchMapping> {
		return of(new PlConfigApplicUIBranchMappingImpl());
	},
	addConfiguration: function (
		branchId: string | number | undefined,
		body: configuration
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	deleteConfiguration: function (
		configurationId: string,
		branchId?: string
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	editConfiguration: function (
		branchId: string | number | undefined,
		body: editConfiguration
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	addFeature: function (
		branchId: string | number | undefined,
		feature: writeFeature
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	modifyFeature: function (
		branchId: string | number | undefined,
		feature: modifyFeature
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	deleteFeature: function (
		branchId: string | number | undefined,
		featureId: string | number
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	modifyConfiguration: function (
		branchId: string | number | undefined,
		featureId: string,
		body: string
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	synchronizeGroup: function (
		branchId: string | number | undefined,
		configId: string
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	getCfgGroups: function (
		branchId: string | number | undefined
	): Observable<cfgGroup[]> {
		throw new Error('Function not implemented.');
	},
	getCfgGroupDetail: function (
		branchId: string | number | undefined,
		cfgGroupId: string | number | undefined
	): Observable<configurationGroup> {
		throw new Error('Function not implemented.');
	},
	addConfigurationGroup: function (
		branchId: string | number | undefined,
		cfgGroup: ConfigurationGroupDefinition
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	deleteConfigurationGroup: function (
		branchId: string | number | undefined,
		id: string
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	updateConfigurationGroup: function (
		branchId: string | number | undefined,
		cfgGroup: ConfigurationGroupDefinition
	): Observable<response> {
		throw new Error('Function not implemented.');
	},
	getApplicabilityToken: function (
		branchId: string | number | undefined,
		applicablityToken: string
	): Observable<NamedId> {
		throw new Error('Function not implemented.');
	},
};
