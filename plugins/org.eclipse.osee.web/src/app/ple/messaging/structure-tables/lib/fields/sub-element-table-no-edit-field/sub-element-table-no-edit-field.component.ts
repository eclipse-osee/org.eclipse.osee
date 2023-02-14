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
import { NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ActivatedRoute, RouterLink } from '@angular/router';
import type { element } from '@osee/messaging/shared';
import { SubElementTableNoEditFieldDynamicWidthComponent } from '../sub-element-table-no-edit-field-dynamic-width/sub-element-table-no-edit-field-dynamic-width.component';
import { SubElementTableNoEditFieldFilteredComponent } from '../sub-element-table-no-edit-field-filtered/sub-element-table-no-edit-field-filtered.component';
import { SubElementTableNoEditFieldNameComponent } from '../sub-element-table-no-edit-field-name/sub-element-table-no-edit-field-name.component';

@Component({
	selector: 'osee-messaging-sub-element-table-no-edit-field',
	templateUrl: './sub-element-table-no-edit-field.component.html',
	styleUrls: ['./sub-element-table-no-edit-field.component.sass'],
	standalone: true,
	imports: [
		SubElementTableNoEditFieldDynamicWidthComponent,
		SubElementTableNoEditFieldFilteredComponent,
		SubElementTableNoEditFieldNameComponent,
		RouterLink,
		MatFormFieldModule,
		NgIf,
		MatButtonModule,
	],
})
export class SubElementTableNoEditFieldComponent implements OnInit {
	@Input() filter: string = '';
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
	@Input() header: string = '';
	@Input() width: string = '';
	_branchId: string = '';
	_branchType: string = '';

	constructor(private route: ActivatedRoute) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((values) => {
			this._branchId = values.get('branchId') || '';
			this._branchType = values.get('branchType') || '';
		});
	}
}
