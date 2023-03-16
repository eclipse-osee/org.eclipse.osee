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
import { Observable, of } from 'rxjs';
import { AttributeService } from '@osee/shared/services';

let bogusObservable$ = new Observable<string>();

export const attributeServiceMock: Partial<AttributeService> = {
	getMarkDownContent(
		branchId: string,
		artifactID: string,
		attributeID: string
	) {
		return bogusObservable$;
	},
};
