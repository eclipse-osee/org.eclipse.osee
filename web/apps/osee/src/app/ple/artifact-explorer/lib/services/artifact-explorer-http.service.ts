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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { Observable } from 'rxjs';
import { HttpParamsType, attribute } from '@osee/shared/types';
import { AdvancedSearchCriteria } from '../types/artifact-search';
import {
	artifactWithRelations,
	artifactTokenWithIcon,
} from '@osee/artifact-with-relations/types';
import {
	publishingTemplateKeyGroups,
	publishMarkdownAsHtmlRequestData,
} from '../types/artifact-explorer';

@Injectable({
	providedIn: 'root',
})
export class ArtifactExplorerHttpService {
	private http = inject(HttpClient);

	public getartifactWithRelations(
		branchId: string,
		artifactId: string,
		viewId: string,
		includeRelations: boolean
	) {
		return this.http.get<artifactWithRelations>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/artifact/' +
				artifactId +
				'/related/direct',
			{
				params: { viewId: viewId, includeRelations: includeRelations },
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

	// Publishing

	public getPublishingTemplateKeyGroups(filterBySafeName: string) {
		let params: HttpParamsType = {};
		if (filterBySafeName && filterBySafeName !== '') {
			params = { ...params, filterBySafeName: filterBySafeName };
		}
		return this.http.get<publishingTemplateKeyGroups>(
			apiURL + '/define/templatemanager/getPublishingTemplateKeyGroups',
			{
				params: params,
			}
		);
	}

	public publishMarkdownAsHtml(
		data: publishMarkdownAsHtmlRequestData
	): Observable<HttpResponse<Blob>> {
		const formData = new FormData();

		// Serialize data to JSON and append to FormData
		const jsonData = JSON.stringify(data.publishMarkdownAsHtmlRequestData);
		formData.append(
			'publishMarkdownAsHtmlRequestData',
			new Blob([jsonData], { type: 'application/json' })
		);

		return this.http.post<Blob>(
			apiURL + '/define/word/publishMarkdownAsHtml',
			formData,
			{
				observe: 'response',
				responseType: 'blob' as 'json',
			}
		);
	}
}
