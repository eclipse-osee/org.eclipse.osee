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
import {
	DisplayableElementProps,
	displayableStructureFields,
} from '@osee/messaging/shared/types';
export const defaultEditElementProfile: (keyof DisplayableElementProps)[] = [
	'name',
	'description',
	'platformType',
	'interfaceElementIndexStart',
	'interfaceElementIndexEnd',
	'interfaceLogicalType',
	'interfaceDefaultValue',
	'interfacePlatformTypeMinval',
	'interfacePlatformTypeMaxval',
	'beginWord',
	'endWord',
	'beginByte',
	'endByte',
	'interfaceElementAlterable',
	'notes',
	'applicability',
	'interfacePlatformTypeUnits',
];

export const defaultViewElementProfile: (keyof DisplayableElementProps)[] = [
	'name',
	'interfaceLogicalType',
	'interfacePlatformTypeUnits',
	'interfacePlatformTypeMinval',
	'interfacePlatformTypeMaxval',
	'interfaceElementAlterable',
	'description',
	'enumLiteral',
	// 'interfacePlatformTypeDescription',
	'notes',
];

export const defaultEditStructureProfile: (
	| keyof displayableStructureFields
	| 'txRate'
	| 'publisher'
	| 'messageNumber'
	| 'messagePeriodicity'
)[] = [
	'name',
	'description',
	'interfaceMinSimultaneity',
	'interfaceMaxSimultaneity',
	'interfaceTaskFileType',
	'interfaceStructureCategory',
	'numElements',
	'sizeInBytes',
	'bytesPerSecondMinimum',
	'bytesPerSecondMaximum',
	'applicability',
	'txRate',
];

export const defaultViewStructureProfile: (
	| keyof displayableStructureFields
	| 'txRate'
	| 'publisher'
	| 'messageNumber'
	| 'messagePeriodicity'
)[] = [
	'name',
	'description',
	'interfaceStructureCategory',
	'txRate',
	'interfaceMinSimultaneity',
	'interfaceMaxSimultaneity',
	'numElements',
	'publisher',
	'messageNumber',
	'interfaceTaskFileType',
	'sizeInBytes',
];
