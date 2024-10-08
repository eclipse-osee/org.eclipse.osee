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

import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import {
	element,
	ElementDialog,
	ElementDialogMode,
	PlatformType,
} from '@osee/messaging/shared/types';

export class DefaultAddElementDialog implements ElementDialog {
	id = '';
	name = '';
	startingElement: element = {
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		notes: {
			id: '-1',
			typeId: '1152921504606847085',
			gammaId: '-1',
			value: '',
		},
		interfaceDefaultValue: {
			id: '-1',
			typeId: '2886273464685805413',
			gammaId: '-1',
			value: '',
		},
		interfaceElementAlterable: {
			id: '-1',
			typeId: '2455059983007225788',
			gammaId: '-1',
			value: true,
		},
		interfaceElementIndexEnd: {
			id: '-1',
			typeId: '2455059983007225802',
			gammaId: '-1',
			value: 0,
		},
		interfaceElementIndexStart: {
			id: '-1',
			typeId: '2455059983007225801',
			gammaId: '-1',
			value: 0,
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
		platformType: new PlatformTypeSentinel(),
		arrayElements: [],
		enumLiteral: {
			id: '-1',
			typeId: '2455059983007225803',
			gammaId: '-1',
			value: '',
		},
		interfaceElementArrayHeader: {
			id: '-1',
			typeId: '3313203088521964923',
			gammaId: '-1',
			value: false,
		},
		interfaceElementBlockData: {
			id: '-1',
			typeId: '1523923981411079299',
			gammaId: '-1',
			value: false,
		},
		interfaceElementWriteArrayHeaderName: {
			id: '-1',
			typeId: '3313203088521964924',
			gammaId: '-1',
			value: false,
		},
		interfaceElementArrayIndexOrder: {
			id: '-1',
			typeId: '6818939106523472581',
			gammaId: '-1',
			value: 'OUTER_INNER',
		},
		interfaceElementArrayIndexDelimiterOne: {
			id: '-1',
			typeId: '6818939106523472582',
			gammaId: '-1',
			value: ' ',
		},
		interfaceElementArrayIndexDelimiterTwo: {
			id: '-1',
			typeId: '6818939106523472583',
			gammaId: '-1',
			value: ' ',
		},
	};
	element: element = {
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		notes: {
			id: '-1',
			typeId: '1152921504606847085',
			gammaId: '-1',
			value: '',
		},
		interfaceDefaultValue: {
			id: '-1',
			typeId: '2886273464685805413',
			gammaId: '-1',
			value: '',
		},
		interfaceElementAlterable: {
			id: '-1',
			typeId: '2455059983007225788',
			gammaId: '-1',
			value: true,
		},
		interfaceElementIndexEnd: {
			id: '-1',
			typeId: '2455059983007225802',
			gammaId: '-1',
			value: 0,
		},
		interfaceElementIndexStart: {
			id: '-1',
			typeId: '2455059983007225801',
			gammaId: '-1',
			value: 0,
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
		platformType: new PlatformTypeSentinel(),
		arrayElements: [],
		enumLiteral: {
			id: '-1',
			typeId: '2455059983007225803',
			gammaId: '-1',
			value: '',
		},
		interfaceElementArrayHeader: {
			id: '-1',
			typeId: '3313203088521964923',
			gammaId: '-1',
			value: false,
		},
		interfaceElementBlockData: {
			id: '-1',
			typeId: '1523923981411079299',
			gammaId: '-1',
			value: false,
		},
		interfaceElementWriteArrayHeaderName: {
			id: '-1',
			typeId: '3313203088521964924',
			gammaId: '-1',
			value: false,
		},
		interfaceElementArrayIndexOrder: {
			id: '-1',
			typeId: '6818939106523472581',
			gammaId: '-1',
			value: 'OUTER_INNER',
		},
		interfaceElementArrayIndexDelimiterOne: {
			id: '-1',
			typeId: '6818939106523472582',
			gammaId: '-1',
			value: ' ',
		},
		interfaceElementArrayIndexDelimiterTwo: {
			id: '-1',
			typeId: '6818939106523472583',
			gammaId: '-1',
			value: ' ',
		},
	};
	type = new PlatformTypeSentinel();
	mode: ElementDialogMode = 'add';
	allowArray = true;
	arrayChild = false;
	createdTypes: PlatformType[] = [];

	constructor(
		id?: string,
		name?: string,
		startingElement?: element,
		element?: element,
		mode?: ElementDialogMode,
		allowArray?: boolean,
		arrayChild?: boolean,
		createdTypes?: PlatformType[]
	) {
		this.id = id ? id : this.id;
		this.name = name ? name : this.name;
		this.mode = mode ? mode : this.mode;
		this.allowArray =
			allowArray !== undefined ? allowArray : this.allowArray;
		this.arrayChild = arrayChild || false;
		if (startingElement) {
			this.startingElement = startingElement;
		}
		if (element) {
			this.element = element;
		}
		if (createdTypes) {
			this.createdTypes = createdTypes;
		}
	}
}
