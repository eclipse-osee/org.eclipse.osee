/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { CiBatchService } from '../services/ci-batch.service';
import { scriptBatchResultMock } from 'src/app/ci-dashboard/lib/testing/tmo.response.mock';

export const ciBatchServiceMock: Partial<CiBatchService> = {
	get selectedBatchId() {
		return of('1');
	},
	set SelectedBatchId(batchId: string) {},
	get selectedBatch() {
		return of(scriptBatchResultMock[0]);
	},
};
