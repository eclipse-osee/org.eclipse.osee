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
import {
	ChangeDetectionStrategy,
	Component,
	EventEmitter,
	Input,
	Output,
} from '@angular/core';
import { LayoutNotifierService } from '@osee/layout/notification';
import { applic } from '@osee/shared/types/applicability';
import { EditElementFieldComponent } from '../edit-element-field/edit-element-field.component';
import { SubElementTableNoEditFieldComponent } from '../sub-element-table-no-edit-field/sub-element-table-no-edit-field.component';
import { EnumLiteralsFieldComponent } from '../enum-literal-field/enum-literals-field.component';
import type {
	element,
	elementWithChanges,
	PlatformType,
	structure,
} from '@osee/messaging/shared/types';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { MatIconModule } from '@angular/material/icon';

@Component({
	selector: 'osee-messaging-sub-element-table-field',
	templateUrl: './sub-element-table-field.component.html',
	styles: [],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		EditElementFieldComponent,
		SubElementTableNoEditFieldComponent,
		NgIf,
		NgFor,
		AsyncPipe,
		MatIconModule,
		EnumLiteralsFieldComponent,
	],
})
export class SubElementTableFieldComponent {
	@Input() header!: keyof element | 'rowControls';
	@Input() editMode: boolean = false;

	@Input() element: element | elementWithChanges = {
		id: '',
		name: '',
		description: '',
		notes: '',
		interfaceDefaultValue: '',
		interfaceElementIndexEnd: 0,
		interfaceElementIndexStart: 0,
		platformType: new PlatformTypeSentinel(),
		arrayElements: [],
		applicability: {
			id: '1',
			name: 'Base',
		},
		units: '',
		interfaceElementAlterable: false,
		interfaceElementArrayHeader: false,
		enumLiteral: '',
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
	editableElementHeaders: (keyof element)[] = [
		'name',
		'platformType',
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
	layout = this.layoutNotifier.layout;

	@Output() menu = new EventEmitter<{
		event: MouseEvent;
		element: element;
		field?: string | number | boolean | applic | PlatformType;
	}>();

	constructor(private layoutNotifier: LayoutNotifierService) {}

	getEnumLiterals() {
		return this.element.enumLiteral.split('\n');
	}
	openGeneralMenu(
		event: MouseEvent,
		element: element,
		field?: string | number | boolean | applic | PlatformType
	) {
		this.menu.emit({ event, element, field });
	}

	hasChanges(v: element | elementWithChanges): v is elementWithChanges {
		return (
			(v as any).changes !== undefined ||
			(v as any).added !== undefined ||
			(v as any).deleted !== undefined
		);
	}
}
