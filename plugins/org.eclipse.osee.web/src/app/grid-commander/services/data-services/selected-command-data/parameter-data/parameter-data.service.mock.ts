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
import { Observable, of } from 'rxjs';
import { ParameterDataService } from './parameter-data.service';
import { Parameter } from '../../../../types/grid-commander-types/gc-user-and-contexts-relationships';

export const parameterDataServiceMock: Partial<ParameterDataService> = {
	get parameter$(): Observable<Parameter> {
		return of({
			id: '1',
			name: 'param',
			typeAsString: '',
			attributes: {},
		});
	},
};
