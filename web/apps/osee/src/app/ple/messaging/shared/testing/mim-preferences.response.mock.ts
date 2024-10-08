/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import type {
	MimPreferences,
	structure,
	message,
	subMessage,
	element,
	MimUserGlobalPreferences,
	displayableStructureFields,
} from '@osee/messaging/shared/types';

export const MimPreferencesMock: MimPreferences<
	displayableStructureFields & message & subMessage & element
> = {
	id: '61106791',
	name: 'Joe Smith',
	globalPrefs: {
		id: '1',
		name: 'Global Prefs',
		wordWrap: true,
	},
	columnPreferences: [
		{
			name: 'name',
			enabled: true,
		},
		{
			name: 'description',
			enabled: true,
		},
		{
			name: 'interfaceMaxSimultaneity',
			enabled: true,
		},
		{
			name: 'interfaceMinSimultaneity',
			enabled: true,
		},
		{
			name: 'interfaceTaskFileType',
			enabled: true,
		},
		{
			name: 'interfaceStructureCategory',
			enabled: true,
		},
		{
			name: 'platformType',
			enabled: true,
		},
		{
			name: 'interfaceElementAlterable',
			enabled: true,
		},
		{
			name: 'notes',
			enabled: true,
		},
		{
			name: 'numElements',
			enabled: false,
		},
		{
			name: 'sizeInBytes',
			enabled: false,
		},
		{
			name: 'bytesPerSecondMinimum',
			enabled: false,
		},
		{
			name: 'bytesPerSecondMaximum',
			enabled: false,
		},
		{
			name: 'applicability',
			enabled: true,
		},
		{
			name: 'beginWord',
			enabled: false,
		},
		{
			name: 'endWord',
			enabled: false,
		},
		{
			name: 'beginByte',
			enabled: false,
		},
		{
			name: 'endByte',
			enabled: false,
		},
	],
	inEditMode: true,
	hasBranchPref: true,
};

export const testGlobalUserPrefs: MimUserGlobalPreferences = {
	id: '1',
	name: 'MIM Global User Preferences',
	wordWrap: false,
};
