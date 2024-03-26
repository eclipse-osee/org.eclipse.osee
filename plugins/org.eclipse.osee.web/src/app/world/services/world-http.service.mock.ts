import { of } from 'rxjs';
import { WorldHttpService } from './world-http.service';

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
export const worldHttpServiceMock: Partial<WorldHttpService> = {
	getWorldData(collId: string, custId: string) {
		return of({
			orderedHeaders: [],
			rows: [],
		});
	},
};
