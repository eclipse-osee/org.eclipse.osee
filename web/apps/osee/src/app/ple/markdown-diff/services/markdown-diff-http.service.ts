/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { changeReportRow } from '@osee/shared/types/change-report';
import { map } from 'rxjs';
import { MarkdownDiffEntry } from '../types/markdown-diff';

/** Attribute type the markdown change report is scoped to (server-side filter). */
const MARKDOWN_CONTENT_TYPE = 'Markdown Content';

@Injectable({
	providedIn: 'root',
})
export class MarkdownDiffHttpService {
	private http = inject(HttpClient);

	/**
	 * Fetches one page of Markdown Content changes between the two branches.
	 * Filtering (by `attributeType` and free-text) and pagination happen on the
	 * server via the `/filtered` endpoint; the client only maps the returned
	 * rows to display entries. A `pageSize` of 0 asks the server for every
	 * matching row (used by the export flow).
	 */
	getMarkdownChanges(
		branchId: string,
		parentBranchId: string,
		filter = '',
		pageNum = 0,
		pageSize = 10
	) {
		let params = new HttpParams()
			.set('attributeType', MARKDOWN_CONTENT_TYPE)
			.set('pageNum', pageNum.toString())
			.set('count', pageSize.toString());
		if (filter) {
			params = params.set('filter', filter);
		}
		return this.http
			.get<
				changeReportRow[]
			>(`${apiURL}/orcs/branches/${branchId}/changes/${parentBranchId}/filtered`, { params })
			.pipe(
				map((rows) =>
					rows.map(
						(row): MarkdownDiffEntry => ({
							artifactId: row.ids,
							artifactName: row.names,
							changeType: row.changeType,
							changeDescription:
								this.deriveChangeDescription(row),
							wasValue: row.wasValue ?? '',
							isValue: row.isValue ?? '',
						})
					)
				)
			);
	}

	/**
	 * Total number of Markdown Content changes matching the branch pair and
	 * free-text filter — the paginator length. Computed on the server (no paging
	 * params) so it reflects the full matching set, not just a page.
	 */
	getMarkdownChangesCount(
		branchId: string,
		parentBranchId: string,
		filter = ''
	) {
		let params = new HttpParams().set(
			'attributeType',
			MARKDOWN_CONTENT_TYPE
		);
		if (filter) {
			params = params.set('filter', filter);
		}
		return this.http.get<number>(
			`${apiURL}/orcs/branches/${branchId}/changes/${parentBranchId}/filtered/count`,
			{ params }
		);
	}

	private deriveChangeDescription(row: changeReportRow): string {
		const type = row.changeType.toLowerCase();
		if (type.includes('deleted')) {
			return 'Artifact Deleted';
		}
		if (type === 'new') {
			return 'Markdown Content Added';
		}
		if (type === 'modified') {
			return 'Markdown Content Modified';
		}
		if (type === 'applicability') {
			return 'Applicability Changed';
		}
		return row.changeType;
	}
}
