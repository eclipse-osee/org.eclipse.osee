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

export interface attribute<T = AttributeValue> {
	name: string;
	value: T;
	typeId: string;
	id: string;
	storeType: storeType;
	multiplicityId: string;
}

export type AttributeValue = string | Date;

export type storeType =
	| 'Boolean'
	| 'Date'
	| 'Enumeration'
	| 'Integer'
	| 'Long'
	| 'String';
