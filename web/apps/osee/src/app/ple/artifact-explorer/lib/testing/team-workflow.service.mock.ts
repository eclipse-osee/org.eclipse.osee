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
import { of } from 'rxjs';
import { TeamWorkflowService } from '../services/team-workflow.service';
import { attribute } from '@osee/shared/types';

export const teamWorkflowServiceMock: Partial<TeamWorkflowService> = {
	allTeamWorkflowAttributes: of([
		{
			id: '1',
			multiplicityId: '1',
			name: 'Test Attribute',
			storeType: 'String',
			typeId: '123',
			value: 'Test value',
		},
	] as attribute[]),
};
