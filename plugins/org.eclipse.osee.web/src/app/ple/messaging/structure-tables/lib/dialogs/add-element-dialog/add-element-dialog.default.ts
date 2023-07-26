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
} from '@osee/messaging/shared/types';

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
		arrayElements: [],
		units: '',
		enumLiteral: '',
	};
	type = new PlatformTypeSentinel();
	mode: ElementDialogMode = 'add';
	allowArray = true;

	constructor(
		id?: string,
		name?: string,
		element?: Partial<element>,
		mode?: ElementDialogMode,
		allowArray?: boolean
	) {
		this.id = id ? id : this.id;
		this.name = name ? name : this.name;
		this.mode = mode ? mode : this.mode;
		this.allowArray =
			allowArray !== undefined ? allowArray : this.allowArray;
		if (element) {
			this.element = element;
		}
	}
}
