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
import { EventEmitter, Output } from '@angular/core';
import { Component, Input } from '@angular/core';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import type { element, structure } from '@osee/messaging/shared/types';
import { applic } from '@osee/shared/types/applicability';

@Component({
	selector: 'osee-messaging-sub-element-table-field',
	template: '<p>Dummy</p>',
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class SubElementTableRowComponentMock {
	@Input() header: string = '';
	@Input() editMode: boolean = false;
	@Input() element: element = {
		id: '',
		name: '',
		description: '',
		notes: '',
		interfaceElementIndexEnd: 0,
		interfaceElementIndexStart: 0,
		interfaceElementAlterable: false,
		interfaceDefaultValue: '',
		enumLiteral: '',
		units: 'Hertz',
		platformType: new PlatformTypeSentinel(),
	};
	@Input() structure: structure = {
		id: '',
		name: '',
		nameAbbrev: '',
		description: '',
		interfaceMaxSimultaneity: '',
		interfaceMinSimultaneity: '',
		interfaceTaskFileType: 0,
		interfaceStructureCategory: '',
	};
	@Input() filter: string = '';
	@Output() menu = new EventEmitter<{
		event: MouseEvent;
		element: element;
		field?: string | number | boolean | applic;
	}>();
	@Output() navigate = new EventEmitter<string>();
}
