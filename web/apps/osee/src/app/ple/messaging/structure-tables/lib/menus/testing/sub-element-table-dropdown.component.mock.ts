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
import { Component, input, Input } from '@angular/core';
import {
	DisplayableElementProps,
	PlatformType,
	elementSentinel,
	type element,
	type structure,
} from '@osee/messaging/shared/types';
import { applic, applicabilitySentinel } from '@osee/applicability/types';

@Component({
	selector:
		'osee-sub-element-table-dropdown[element][structure][header][branchId][branchType][editMode]',
	template: '<p>Dummy</p>',
	standalone: true,
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class MockSubElementTableComponent {
	@Input() element: element = elementSentinel;

	@Input() structure: structure = {
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
	};

	@Input() header!: keyof DisplayableElementProps;
	@Input() field?: string | number | boolean | PlatformType | applic;

	editMode = input(false);
}
