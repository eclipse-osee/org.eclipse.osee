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
import { EventEmitter, Output } from '@angular/core';
import { Component, Input } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { applicabilitySentinel } from '@osee/applicability/types';
import type { structure } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-sub-element-array-table',
	template: '<p>Dummy</p>',
	standalone: true,
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class SubElementArrayTableComponentMock {
	@Input() data: any = {};
	@Input() dataSource: MatTableDataSource<any> =
		new MatTableDataSource<any>();
	@Input() filter: string = '';

	@Input() element: any = {};
	@Output() expandRow = new EventEmitter();
	@Input() subMessageHeaders: string[] = [];
	@Input() editMode: boolean = false;
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
}
