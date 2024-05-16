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
import { WorldHttpService } from './world-http.service';
import { world, worldRow } from 'src/app/world/world';

export const worldHttpServiceMock: Partial<WorldHttpService> = {
	getWorldData(collId: string, custId: string) {
		return of(worldDataMock);
	},
	getWorldDataStored(collId) {
		return of(worldDataMock);
	},
};

const worldDataMock: world = {
	orderedHeaders: [
		'Goal Order',
		'Name',
		'Type',
		'State',
		'Priority',
		'Change Type',
		'Assignees',
		'ATS Id',
		'Created Date',
		'Targeted Version',
		'Notes',
	],
	rows: [
		{
			Type: 'Decision Review',
			'Targeted Version': '',
			'Created Date': '2024-05-16',
			'Goal Order': 'Unhandled Column',
			State: 'Decision',
			Priority: '',
			Assignees: 'Joe Smith',
			'ATS Id': 'RVW13',
			'Change Type': '',
			Notes: '',
			Name: 'Is the resolution of Action TW24 valid?',
		},
		{
			Type: 'Peer-To-Peer Review',
			'Targeted Version': 'SAW_Bld_2',
			'Created Date': '2024-05-16',
			'Goal Order': 'Unhandled Column',
			State: 'Review',
			Priority: '',
			Assignees: 'Joe Smith; Kay Jones',
			'ATS Id': 'RVW15',
			'Change Type': '',
			Notes: '',
			Name: '2 - Peer Review algorithm used in code',
		},
	],
};
