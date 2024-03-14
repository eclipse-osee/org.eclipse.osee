/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Injectable } from '@angular/core';
import { apiURL } from '@osee/environments';
import { Observable } from 'rxjs';
import {
	artifact,
	artifactTokenWithIcon,
	artifactWithDirectRelations,
} from '../types/artifact-explorer.data';
import { HttpParamsType, attribute } from '@osee/shared/types';
import { AdvancedSearchCriteria } from '../types/artifact-search';

@Injectable({
	providedIn: 'root',
})
export class ArtifactExplorerHttpService {
	constructor(private http: HttpClient) {}

	public getDirectRelations(
		branchId: string,
		artifactId: string,
		viewId: string
	): Observable<artifactWithDirectRelations> {
		return this.http.get<artifactWithDirectRelations>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/artifact/' +
				artifactId +
				'/related/direct',
			{
				params: { viewId: viewId },
			}
		);
	}

	public getArtifactTokensByFilter(
		branchId: string,
		filter: string,
		viewId: string,
		count: number,
		pageNum: number | string,
		searchCriteria?: AdvancedSearchCriteria
	): Observable<artifactTokenWithIcon[]> {
		const params = this.createParams(
			branchId,
			filter,
			viewId,
			searchCriteria,
			count,
			pageNum
		);
		return this.http.get<artifactTokenWithIcon[]>(
			apiURL + '/orcs/branch/' + branchId + '/artifact/search/token',
			{
				params: params,
			}
		);
	}

	public getArtifactsByFilter(
		branchId: string,
		filter: string,
		viewId: string,
		count: number,
		pageNum: number | string,
		searchCriteria?: AdvancedSearchCriteria
	) {
		const params = this.createParams(
			branchId,
			filter,
			viewId,
			searchCriteria,
			count,
			pageNum
		);
		return this.http.get<artifact[]>(
			apiURL + '/orcs/branch/' + branchId + '/artifact/search',
			{
				params: params,
			}
		);
	}

	public getArtifactsByFilterCount(
		branchId: string,
		filter: string,
		viewId: string,
		searchCriteria?: AdvancedSearchCriteria
	) {
		const params = this.createParams(
			branchId,
			filter,
			viewId,
			searchCriteria
		);
		return this.http.get<number>(
			apiURL + '/orcs/branch/' + branchId + '/artifact/search/count',
			{
				params: params,
			}
		);
	}

	public getArtifactForTab(branchId: string, artifactId: string) {
		return this.http.get<artifact>(
			apiURL + '/orcs/branch/' + branchId + '/artifact/load/' + artifactId
		);
	}

	private createParams(
		branchId: string,
		filter: string,
		viewId: string,
		searchCriteria?: AdvancedSearchCriteria,
		count?: number,
		pageNum?: number | string
	) {
		let params: HttpParamsType = {};
		if (branchId && branchId !== '') {
			params = { ...params, branchId: branchId };
		}
		if (filter && filter !== '') {
			params = { ...params, search: filter };
		}
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (pageNum && count) {
			params = { ...params, pageNum: pageNum, count: count };
		}
		if (searchCriteria) {
			if (searchCriteria.attributeTypes.length > 0) {
				params = {
					...params,
					attributeType: searchCriteria.attributeTypes.map(
						(a) => a.id
					),
				};
			}
			if (searchCriteria.artifactTypes.length > 0) {
				params = {
					...params,
					artifactType: searchCriteria.artifactTypes.map((a) => a.id),
				};
			}
			if (searchCriteria.exactMatch) {
				params = {
					...params,
					exact: searchCriteria.exactMatch,
				};
			}
			if (searchCriteria.searchById) {
				params = {
					...params,
					searchById: searchCriteria.searchById,
				};
			}
		}
		return params;
	}

	public getPathToArtifact(
		branchId: string,
		artifactId: string,
		viewId: string
	): Observable<string[][]> {
		let params: HttpParamsType = {};
		if (branchId && branchId !== '') {
			params = { ...params, branchId: branchId };
		}
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		return this.http.get<string[][]>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/artifact/' +
				artifactId +
				'/getPathToArtifact',
			{
				params: params,
			}
		);
	}

	public getArtifactTypeAttributes(artifactId: string) {
		return this.http.get<attribute[]>(
			apiURL + '/orcs/types/artifact/' + artifactId + '/attributes'
		);
	}
}
