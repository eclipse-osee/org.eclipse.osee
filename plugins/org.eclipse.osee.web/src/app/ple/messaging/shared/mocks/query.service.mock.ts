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
import { QueryService } from '../services/http/query.service';
import { MimQuery } from '../types/MimQuery';

export const QueryServiceMock: Partial<QueryService> = {
	query(branchId: string, query: MimQuery<unknown>) {
		return of('Hello' as any);
	},

	queryExact(branchId: string, query: MimQuery<unknown>) {
		return of('Hello' as any);
	},
};
