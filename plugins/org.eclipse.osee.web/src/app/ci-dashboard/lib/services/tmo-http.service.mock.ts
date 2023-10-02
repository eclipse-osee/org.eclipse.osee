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
import { TmoHttpService } from './tmo-http.service';
import {
	defReferenceMock,
	programsMock,
	resultReferenceMock,
	testCaseReferenceMock,
	testPointReferenceMock,
} from '../testing/tmo.response.mock';

export const tmoHttpServiceMock: Partial<TmoHttpService> = {
	getProgramList() {
		return of(programsMock);
	},

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
};
