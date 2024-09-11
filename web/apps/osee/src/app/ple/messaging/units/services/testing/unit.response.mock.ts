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
import { applicabilitySentinel } from '@osee/applicability/types';
import { unit } from '@osee/messaging/units/types';

export const unitsMock: unit[] = [
	{
		id: '1',
		gammaId: '1',
		name: {
			id: '11',
			gammaId: '11',
			typeId: '1152921504606847088',
			value: 'dB',
		},
		measurement: {
			id: '111',
			gammaId: '111',
			typeId: '2478822847543373494',
			value: 'decibels',
		},
		applicability: applicabilitySentinel,
	},
	{
		id: '2',
		gammaId: '2',
		name: {
			id: '22',
			gammaId: '22',
			typeId: '1152921504606847088',
			value: 'DI',
		},
		measurement: {
			id: '222',
			gammaId: '222',
			typeId: '2478822847543373494',
			value: '',
		},
		applicability: applicabilitySentinel,
	},
	{
		id: '3',
		gammaId: '3',
		name: {
			id: '33',
			gammaId: '33',
			typeId: '1152921504606847088',
			value: 'Feet^2',
		},
		measurement: {
			id: '333',
			gammaId: '333',
			typeId: '2478822847543373494',
			value: 'Length',
		},
		applicability: applicabilitySentinel,
	},
	{
		id: '4',
		gammaId: '4',
		name: {
			id: '44',
			gammaId: '44',
			typeId: '1152921504606847088',
			value: 'minutes',
		},
		measurement: {
			id: '444',
			gammaId: '444',
			typeId: '2478822847543373494',
			value: 'Time',
		},
		applicability: applicabilitySentinel,
	},
];
