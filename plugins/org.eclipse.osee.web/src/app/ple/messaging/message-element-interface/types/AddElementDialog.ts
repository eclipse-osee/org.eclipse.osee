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

import { AddElementDialog } from './AddElementDialog.d';
export class DefaultAddElementDialog implements AddElementDialog {
	id = '';
	name = '';
	element = {
		id: '-1',
		name: '',
		description: '',
		notes: '',
		interfaceElementAlterable: true,
		interfaceElementIndexEnd: 0,
		interfaceElementIndexStart: 0,
		units: '',
		enumLiteral: '',
	};
	type = {
		id: '',
		name: '',
		description: '',
		interfaceLogicalType: '',
		interfacePlatform2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '',
		interfacePlatformTypeBitSize: '',
		interfacePlatformTypeBitsResolution: '',
		interfacePlatformTypeCompRate: '',
		interfacePlatformTypeDefaultValue: '',
		interfacePlatformTypeMaxval: '',
		interfacePlatformTypeMinval: '',
		interfacePlatformTypeMsbValue: '',
		interfacePlatformTypeUnits: '',
		interfacePlatformTypeValidRangeDescription: '',
	};

	constructor(id?: string, name?: string) {
		this.id = id ? id : this.id;
		this.name = name ? name : this.name;
	}
}
