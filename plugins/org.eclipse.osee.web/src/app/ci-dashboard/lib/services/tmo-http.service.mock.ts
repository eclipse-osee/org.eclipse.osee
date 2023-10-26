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
import { of } from 'rxjs';
import { TmoHttpService } from './tmo-http.service';
import {
	defReferenceMock,
	resultReferenceMock,
	scriptBatchResultMock,
	testCaseReferenceMock,
	testPointReferenceMock,
} from '../testing/tmo.response.mock';

export const tmoHttpServiceMock: Partial<TmoHttpService> = {
	getScriptDefList() {
		return of(defReferenceMock);
	},

	getScriptResultList() {
		return of(resultReferenceMock);
	},

	getTestCaseList() {
		return of(testCaseReferenceMock);
	},

	getTestPointList() {
		return of(testPointReferenceMock);
	},

	getBatches(
		branchId: string,
		setId: string,
		filter: string,
		pageNum: string | number,
		pageSize: number
	) {
		return of(scriptBatchResultMock);
	},

	getBatchesCount(branchId: string, setId: string, filter: string) {
		return of(1);
	},

	getBatchResults(
		branchId: string,
		batchId: string,
		pageNum: string | number,
		pageSize: number
	) {
		return of(resultReferenceMock);
	},

	getBatchResultsCount(branchId: string, batchId: string) {
		return of(2);
	},
};
