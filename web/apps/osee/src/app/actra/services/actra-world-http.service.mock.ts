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
import { worldDataMock } from '../types/actra-types';
import { ActraWorldHttpService } from './actra-world-http.service';

export const ActraWorldHttpServiceMock: Partial<ActraWorldHttpService> = {
	getWorldData(collId: string, custId: string) {
		return of(worldDataMock);
	},
	getWorldDataMy() {
		return of(worldDataMock);
	},
};
