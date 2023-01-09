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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LayoutNotifierService } from '../../../../../../layout/lib/notification/layout-notifier.service';
import { applic } from '../../../../../../types/applicability/applic';
import { element } from '../../../../shared/types/element';
import { structure } from '../../../../shared/types/structure';
import { EditElementFieldComponent } from '../edit-element-field/edit-element-field.component';
import { SubElementTableNoEditFieldComponent } from '../sub-element-table-no-edit-field/sub-element-table-no-edit-field.component';
import { EnumLiteralsFieldComponent } from '../enum-literal-field/enum-literals-field.component';

@Component({
	selector: 'osee-messaging-sub-element-table-field',
	templateUrl: './sub-element-table-field.component.html',
	styleUrls: ['./sub-element-table-field.component.sass'],
	standalone: true,
	imports: [
		EditElementFieldComponent,
		SubElementTableNoEditFieldComponent,
		NgIf,
		NgFor,
		AsyncPipe,
		EnumLiteralsFieldComponent,
	],
})
export class SubElementTableFieldComponent {
	@Input() header: string = '';
	@Input() editMode: boolean = false;

	@Input() element: element = {
		id: '',
		name: '',
		description: '',
		notes: '',
		interfaceDefaultValue: '',
		interfaceElementIndexEnd: 0,
		interfaceElementIndexStart: 0,
		applicability: {
			id: '1',
			name: 'Base',
		},
		units: '',
		interfaceElementAlterable: false,
		enumLiteral: '',
	};

	@Input() structure: structure = {
		id: '',
		name: '',
		description: '',
		interfaceMaxSimultaneity: '',
		interfaceMinSimultaneity: '',
		interfaceTaskFileType: 0,
		interfaceStructureCategory: '',
	};
	editableElementHeaders: string[] = [
		'name',
		'platformTypeName2',
		'interfaceElementAlterable',
		'description',
		'notes',
		'applicability',
		'units',
		'interfaceElementIndexStart',
		'interfaceElementIndexEnd',
		'enumLiteral',
		'interfaceDefaultValue',
	];
	@Input() filter: string = '';
	@Input() wordWrap: boolean = false;
	layout = this.layoutNotifier.layout;

	@Output() menu = new EventEmitter<{
		event: MouseEvent;
		element: element;
		field?: string | number | boolean | applic;
	}>();
	constructor(private layoutNotifier: LayoutNotifierService) {}
	getEnumLiterals() {
		return this.element.enumLiteral.split('\n');
	}
	openGeneralMenu(
		event: MouseEvent,
		element: element,
		field?: string | number | boolean | applic
	) {
		this.menu.emit({ event, element, field });
	}
}
