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
import { UserContextRelationsService } from './user-context-relations.service';
export const UserContextRelationsServiceMock: Partial<UserContextRelationsService> =
	{
		get commands() {
			return of([
				{
					id: '1000001',
					name: 'Test Artifact',
					contextGroup: 'Test Context',
					attributes: {
						description: 'Test',
						'http method': 'GET',
					},
					parameter: {
						id: '1000002',
						name: 'Test Parameter',
						typeAsString: 'ParameterString',
						attributes: {
							description: 'Test',
							'default value': 'Test',
						},
						idIntValue: 1000002,
						idString: '1000002',
					},
					idIntValue: 1000001,
					idString: '1000001',
				},
			]);
		},
	};
