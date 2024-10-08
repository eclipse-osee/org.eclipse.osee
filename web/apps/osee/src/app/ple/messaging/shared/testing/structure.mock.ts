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
import type { structure } from '@osee/messaging/shared/types';
import { elementsMock } from './element.response.mock';
import { applicabilitySentinel } from '@osee/applicability/types';

export const structuresMock3: structure[] = [
	{
		id: '1',
		gammaId: '-1',
		numElements: 0,
		sizeInBytes: 0,
		incorrectlySized: false,
		autogenerated: false,
		bytesPerSecondMaximum: 0,
		bytesPerSecondMinimum: 0,
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: 'hello',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		interfaceMaxSimultaneity: {
			id: '-1',
			typeId: '2455059983007225756',
			gammaId: '-1',
			value: '1',
		},
		interfaceMinSimultaneity: {
			id: '-1',
			typeId: '2455059983007225755',
			gammaId: '-1',
			value: '0',
		},
		interfaceTaskFileType: {
			id: '-1',
			typeId: '2455059983007225760',
			gammaId: '-1',
			value: 0,
		},
		interfaceStructureCategory: {
			id: '-1',
			typeId: '2455059983007225764',
			gammaId: '-1',
			value: 'Miscellaneous',
		},
		applicability: applicabilitySentinel,
		elements: elementsMock,
	},
];

export const structuresMock2: structure[] = [
	{
		id: '2',
		gammaId: '-1',
		numElements: 0,
		sizeInBytes: 0,
		incorrectlySized: false,
		autogenerated: true,
		bytesPerSecondMaximum: 0,
		bytesPerSecondMinimum: 0,
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: 'hello',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		interfaceMaxSimultaneity: {
			id: '-1',
			typeId: '2455059983007225756',
			gammaId: '-1',
			value: '1',
		},
		interfaceMinSimultaneity: {
			id: '-1',
			typeId: '2455059983007225755',
			gammaId: '-1',
			value: '0',
		},
		interfaceTaskFileType: {
			id: '-1',
			typeId: '2455059983007225760',
			gammaId: '-1',
			value: 0,
		},
		interfaceStructureCategory: {
			id: '-1',
			typeId: '2455059983007225764',
			gammaId: '-1',
			value: 'Miscellaneous',
		},
		applicability: applicabilitySentinel,
		elements: elementsMock,
	},
];
