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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { NamedId, branch } from '@osee/shared/types';
import { Observable, of } from 'rxjs';
import { share, tap } from 'rxjs/operators';
import { trackableFeature } from '../types/features/base';
import {
	PlConfigApplicUIBranchMapping,
	view,
} from '../types/pl-config-applicui-branch-mapping';
import { cfgGroup } from '../types/pl-config-branch';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import {
	configGroup,
	configuration,
	editConfiguration,
} from '../types/pl-config-configurations';
import {
	applicWithConstraints,
	featureConstraintData,
} from '../types/pl-config-feature-constraints';
import { modifyFeature, writeFeature } from '../types/pl-config-features';
import { HttpParamsType, XResultData } from '@osee/shared/types';
import { plConfigTable } from '../types/pl-config-table';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Injectable({
	providedIn: 'root',
})
export class PlConfigBranchService {
	cachedBranch: string | number | undefined;
	applicabilityTagCache = new Map<string, NamedId>();

	private http = inject(HttpClient);
	public getBranches(type: string): Observable<branch[]> {
		return this.http.get<branch[]>(apiURL + '/ats/ple/branches/' + type);
	}
	public addFeatureConstraint(
		data: featureConstraintData,
		branchId: string | number | undefined
	): Observable<XResultData> {
		let params: HttpParamsType = {};
		if (
			data.featureConstraint.applicability1.id !== '-1' &&
			data.featureConstraint.applicability2.id !== '-1'
		) {
			params = {
				...params,
				applicability1: data.featureConstraint.applicability1.id,
				applicability2: data.featureConstraint.applicability2.id,
			};
		}
		return this.http.post<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/constraint',
			null,
			{ params: params }
		);
	}
	public getApplicsWithFeatureConstraints(
		branchId: number | string | undefined
	): Observable<applicWithConstraints[]> {
		return this.http.get<applicWithConstraints[]>(
			apiURL + '/orcs/branch/' + branchId + '/applic/constraints'
		);
	}

	public getApplicabilityTable(
		branchId: number | string,
		viewId: number | string,
		pageNum?: number | string,
		count?: number | string,
		filter?: number | string
	) {
		let params: HttpParamsType = {};
		if (viewId !== '') {
			params = {
				...params,
				viewId: viewId,
			};
		}
		if (pageNum && count) {
			params = { ...params, pageNum: pageNum, count: count };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<plConfigTable>(
			apiURL + '/orcs/branch/' + branchId + '/applicability',
			{ params: params }
		);
	}

	public getApplicabilityTableCount(
		branchId: number | string,
		viewId: number | string,
		filter?: number | string
	) {
		let params: HttpParamsType = {};
		if (viewId !== '') {
			params = {
				...params,
				viewId: viewId,
			};
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL + '/orcs/branch/' + branchId + '/applicability/count',
			{ params: params }
		);
	}

	public getFeatureValues(
		branchId: number | string,
		configId: number | string,
		featureId: number | string,
		pageNum?: number | string,
		count?: number | string,
		filter?: number | string
	) {
		let params: HttpParamsType = {};
		if (pageNum && count) {
			params = { ...params, pageNum: pageNum, count: count };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<
			{
				e1: number;
				e2: number;
				gammaId: number;
				value: string;
				constrained: boolean;
				constrainedBy: string;
			}[]
		>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applicability/feature/' +
				featureId +
				'/' +
				configId,
			{ params: params }
		);
	}

	public getFeatureValuesCount(
		branchId: number | string,
		configId: number | string,
		featureId: number | string,
		filter?: number | string
	) {
		let params: HttpParamsType = {};
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applicability/feature/' +
				featureId +
				'/' +
				configId +
				'/count',
			{ params: params }
		);
	}
	public deleteFeatureConstraint(
		branchId: number | string | undefined,
		data: featureConstraintData
	): Observable<XResultData> {
		let params: HttpParamsType = {};
		if (
			data.featureConstraint.applicability1.id !== '-1' &&
			data.featureConstraint.applicability2.id !== '-1'
		) {
			params = {
				...params,
				applicability1: data.featureConstraint.applicability1.id,
				applicability2: data.featureConstraint.applicability2.id,
			};
		}
		return this.http.delete<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/constraint',
			{ params: params }
		);
	}
	public getBranchApplicability(
		branchId: number | string | undefined,
		viewId: number | string
	): Observable<PlConfigApplicUIBranchMapping> {
		let params: HttpParamsType = {};
		if (viewId !== '') {
			params = {
				...params,
				config: viewId,
			};
		}
		return this.http
			.get<PlConfigApplicUIBranchMapping>(
				apiURL + '/orcs/applicui/branch/' + branchId + '/all',
				{ params: params }
			)
			.pipe(share());
	}
	public getFeatureConstraintConflicts(
		branchId: number | string | undefined,
		childApplicId: number | string,
		parentApplicId: number | string
	): Observable<string[]> {
		let params: HttpParamsType = {};
		if (parentApplicId !== '') {
			params = {
				...params,
				childApplicability: childApplicId,
				parentApplicability: parentApplicId,
			};
		}
		return this.http.get<string[]>(
			apiURL + '/orcs/branch/' + branchId + '/applic/constraintConflicts',
			{ params: params }
		);
	}
	public addConfiguration(
		branchId: string | number | undefined,
		body: configuration
	): Observable<XResultData> {
		return this.http.post<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/view/',
			body
		);
	}
	public deleteConfiguration(
		configurationId: string,
		branchId?: string
	): Observable<XResultData> {
		return this.http.delete<XResultData>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applic/view/' +
				configurationId
		);
	}
	public editConfiguration(
		branchId: string | number | undefined,
		body: editConfiguration
	) {
		if (
			body.copyFrom === '' ||
			body.copyFrom === null ||
			body.copyFrom === undefined
		) {
			body.copyFrom = '';
		}
		if (
			body.configurationGroup === null ||
			body.configurationGroup === undefined ||
			body.configurationGroup.length === 0
		) {
			body.configurationGroup = [];
		}
		return this.http.put<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/view',
			body
		);
	}
	public addFeature(
		branchId: string | number | undefined,
		feature: writeFeature
	) {
		const body = feature;
		body.name = feature.name
			.toUpperCase()
			.replace(/[^a-zA-Z0-9-_() ]/g, '');
		return this.http.post<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/feature',
			body
		);
	}
	public modifyFeature(
		branchId: string | number | undefined,
		feature: modifyFeature
	) {
		return this.http.put<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/feature',
			feature
		);
	}
	public deleteFeature(
		branchId: string | number | undefined,
		featureId: number | string
	) {
		return this.http.delete<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/feature/' + featureId
		);
	}
	public addCompoundApplicability(
		branchId: string | number | undefined,
		compApplicName: string
	) {
		return this.http.post<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/compound',
			compApplicName
		);
	}
	public deleteCompoundApplicability(
		branchId: string | number | undefined,
		compApplicId: number | string
	) {
		return this.http.delete<XResultData>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applic/compound/' +
				compApplicId
		);
	}
	public modifyConfiguration(
		branchId: string | number | undefined,
		featureId: string,
		body: string
	): Observable<XResultData> {
		return this.http.put<XResultData>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applic/view/' +
				featureId +
				'/applic',
			body
		);
	}
	public setApplicability(
		branchId: string | number | undefined,
		featureId: string,
		viewId: string,
		applicabilities: string[]
	) {
		return this.http.put<XResultData>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applicability/applic/' +
				featureId +
				'/' +
				viewId,
			applicabilities
		);
	}

	public getCfgGroupsForView(
		branchId: string | number | undefined,
		viewId: string | number
	) {
		return this.http.get<cfgGroup[]>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applicability/views/' +
				viewId +
				'/groups'
		);
	}

	public getView(
		branchId: string | number | undefined,
		viewId: string | number
	) {
		return this.http.get<view>(
			apiURL + '/orcs/branch/' + branchId + '/applic/view/def/' + viewId
		);
	}
	public getViewsByIds(
		branchId: string | number | undefined,
		viewIds: string[]
	) {
		return this.http.get<view[]>(
			apiURL + '/orcs/branch/' + branchId + '/applicability/views',
			{
				params: {
					id: viewIds,
				},
			}
		);
	}

	public getViewsOrderedByName(branchId: string | number | undefined) {
		return this.http.get<view[]>(
			apiURL + '/orcs/branch/' + branchId + '/applicability/views',
			{
				params: { orderByAttribute: ATTRIBUTETYPEIDENUM.NAME },
			}
		);
	}

	public getFeatures(branchId: number | string) {
		const params: HttpParamsType = {
			orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
		};
		return this.http.get<trackableFeature[]>(
			apiURL + '/orcs/branch/' + branchId + '/applicability/features',
			{ params: params }
		);
	}

	public getFeatureById(
		branchId: string | number,
		featureId: string | number
	) {
		return this.http.get<trackableFeature>(
			apiURL + '/orcs/branch/' + branchId + '/applic/feature/' + featureId
		);
	}
	public getCfgGroups(
		branchId: string | number | undefined
	): Observable<cfgGroup[]> {
		return this.http.get<cfgGroup[]>(
			apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/'
		);
	}
	public getCfgGroupDetail(
		branchId: string | number | undefined,
		cfgGroupId: string | number | undefined
	) {
		return this.http.get<configGroup>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/applic/cfggroup/def/' +
				cfgGroupId
		);
	}
	public addConfigurationGroup(
		branchId: string | number | undefined,
		cfgGroup: ConfigurationGroupDefinition
	): Observable<XResultData> {
		return this.http.post<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/',
			cfgGroup
		);
	}
	public deleteConfigurationGroup(
		branchId: string | number | undefined,
		id: string
	) {
		return this.http.delete<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/' + id
		);
	}
	public updateConfigurationGroup(
		branchId: string | number | undefined,
		cfgGroup: ConfigurationGroupDefinition
	) {
		return this.http.put<XResultData>(
			apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/',
			cfgGroup
		);
	}

	public getApplicabilityToken(
		branchId: string | number | undefined,
		applicablityToken: string
	) {
		if (this.cachedBranch === undefined) {
			this.cachedBranch = branchId;
		}
		if (this.cachedBranch !== branchId) {
			this.cachedBranch = branchId;
			this.applicabilityTagCache.clear();
		}
		if (this.applicabilityTagCache.has(applicablityToken)) {
			return of(
				this.applicabilityTagCache.get(applicablityToken) as NamedId
			);
		} else {
			return this.http
				.get<NamedId>(
					apiURL +
						'/orcs/branch/' +
						branchId +
						'/applic/applicabilityToken/' +
						applicablityToken
				)
				.pipe(
					tap((value) => {
						this.applicabilityTagCache.set(
							applicablityToken,
							value
						);
					})
				);
		}
	}
}
