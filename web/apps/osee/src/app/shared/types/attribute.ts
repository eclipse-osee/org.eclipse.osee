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

export type attribute = {
	name: string;
	value: string;
	typeId: `${number}`;
	id: `${number}`;
	storeType: storeType;
	multiplicityId: string;
};

export type storeType =
	| 'Boolean'
	| 'Date'
	| 'Enumeration'
	| 'Integer'
	| 'Long'
	| 'String';

export const mockAttribute: attribute = {
	name: 'name',
	value: 'requirement',
	typeId: '1234',
	id: '7777',
	storeType: 'String',
	multiplicityId: '1',
};

export const nameAttribute: attribute = {
	name: 'Name',
	value: '',
	typeId: '1152921504606847088',
	id: '8888',
	storeType: 'String',
	multiplicityId: '1',
};
