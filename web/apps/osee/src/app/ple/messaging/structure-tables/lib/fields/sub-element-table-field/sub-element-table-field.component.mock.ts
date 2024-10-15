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
import { EventEmitter, Output, input, model } from '@angular/core';
import { Component, Input } from '@angular/core';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import type {
	DisplayableElementProps,
	element,
	structure,
} from '@osee/messaging/shared/types';
import { applic, applicabilitySentinel } from '@osee/applicability/types';

@Component({
	selector: 'osee-messaging-sub-element-table-field',
	template: '<p>Dummy</p>',
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class SubElementTableRowComponentMock {
	header = input.required<keyof DisplayableElementProps | 'rowControls'>();
	editMode = input.required<boolean>();

	element = model.required<element>();
	structure = input<structure>({
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
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
			value: '',
		},
		interfaceMinSimultaneity: {
			id: '-1',
			typeId: '2455059983007225755',
			gammaId: '-1',
			value: '',
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
			value: '',
		},
		applicability: applicabilitySentinel,
		elements: [],
	});
	filter = input<string>('');
}
