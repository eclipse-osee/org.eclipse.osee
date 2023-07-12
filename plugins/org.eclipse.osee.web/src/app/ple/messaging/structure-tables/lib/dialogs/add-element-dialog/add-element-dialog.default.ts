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
import { element } from '@osee/messaging/shared/types';
import { ElementDialog } from '../../element-dialog';

export class DefaultAddElementDialog implements ElementDialog {
	id = '';
	name = '';
	element: Partial<element> = {
		id: '-1',
		name: '',
		description: '',
		notes: '',
		interfaceDefaultValue: '',
		interfaceElementAlterable: true,
		interfaceElementIndexEnd: 0,
		interfaceElementIndexStart: 0,
		applicability: {
			id: '1',
			name: 'Base',
		},
		platformType: new PlatformTypeSentinel(),
		units: '',
		enumLiteral: '',
	};
	type = {
		id: '',
		name: '',
		description: '',
		interfaceLogicalType: '',
		interfacePlatformType2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '',
		interfacePlatformTypeBitSize: '',
		interfacePlatformTypeBitsResolution: '',
		interfacePlatformTypeCompRate: '',
		interfaceDefaultValue: '',
		interfacePlatformTypeMaxval: '',
		interfacePlatformTypeMinval: '',
		interfacePlatformTypeMsbValue: '',
		interfacePlatformTypeUnits: '',
		interfacePlatformTypeValidRangeDescription: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
		enumSet: {
			id: '-1',
			name: '',
			description: '',
			applicability: {
				id: '1',
				name: 'Base',
			},
		},
	};

	constructor(id?: string, name?: string, element?: Partial<element>) {
		this.id = id ? id : this.id;
		this.name = name ? name : this.name;
		if (element) {
			this.element = element;
		}
	}
}
