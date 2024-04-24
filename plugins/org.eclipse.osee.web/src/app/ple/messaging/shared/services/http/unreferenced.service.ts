/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import {
	PlatformType,
	element,
	message,
	structure,
	subMessage,
} from '@osee/messaging/shared/types';
import { HttpParamsType } from '@osee/shared/types';

const _getQueryUrl = (branchId: string, type: string) => {
	return `${apiURL}/mim/branch/${branchId}/unreferenced/${type}`;
};

const _getCountUrl = (branchId: string, type: string) => {
	return `${apiURL}/mim/branch/${branchId}/unreferenced/${type}/count`;
};
const _getParams = (
	filter?: string,
	pageNum?: string | number,
	pageSize?: string | number
) => {
	let params: HttpParamsType = {};
	if (pageNum) {
		params = { ...params, pageNum: pageNum };
	}
	if (pageSize) {
		params = { ...params, count: pageSize };
	}
	if (filter && filter !== '') {
		params = { ...params, filter: filter };
	}
	return params;
};

@Injectable({
	providedIn: 'root',
})
export class UnreferencedService {
	private http = inject(HttpClient);

	getUnreferencedPlatformTypes(
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) {
		return this.http.get<PlatformType[]>(_getQueryUrl(branchId, 'types'), {
			params: _getParams(filter, pageNum, pageSize),
		});
	}

	getUnreferencedPlatformTypesCount(branchId: string, filter?: string) {
		return this.http.get<number>(_getCountUrl(branchId, 'types'), {
			params: _getParams(filter),
		});
	}

	getUnreferencedElements(
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) {
		return this.http.get<element[]>(_getQueryUrl(branchId, 'elements'), {
			params: _getParams(filter, pageNum, pageSize),
		});
	}

	getUnreferencedElementsCount(branchId: string, filter?: string) {
		return this.http.get<number>(_getCountUrl(branchId, 'elements'), {
			params: _getParams(filter),
		});
	}

	getUnreferencedStructures(
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) {
		return this.http.get<structure[]>(
			_getQueryUrl(branchId, 'structures'),
			{
				params: _getParams(filter, pageNum, pageSize),
			}
		);
	}

	getUnreferencedStructuresCount(branchId: string, filter?: string) {
		return this.http.get<number>(_getCountUrl(branchId, 'structures'), {
			params: _getParams(filter),
		});
	}

	getUnreferencedSubmessages(
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) {
		return this.http.get<subMessage[]>(
			_getQueryUrl(branchId, 'submessages'),
			{
				params: _getParams(filter, pageNum, pageSize),
			}
		);
	}

	getUnreferencedSubmessagesCount(branchId: string, filter?: string) {
		return this.http.get<number>(_getCountUrl(branchId, 'submessages'), {
			params: _getParams(filter),
		});
	}

	getUnreferencedMessages(
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) {
		return this.http.get<message[]>(_getQueryUrl(branchId, 'messages'), {
			params: _getParams(filter, pageNum, pageSize),
		});
	}
	getUnreferencedMessagesCount(branchId: string, filter?: string) {
		return this.http.get<number>(_getCountUrl(branchId, 'messages'), {
			params: _getParams(filter),
		});
	}
}
