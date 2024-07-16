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
import { Component, Input, OnInit } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatLabel } from '@angular/material/form-field';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { elementSentinel, type element } from '@osee/messaging/shared/types';
import { SubElementTableNoEditFieldDynamicWidthComponent } from '../sub-element-table-no-edit-field-dynamic-width/sub-element-table-no-edit-field-dynamic-width.component';
import { SubElementTableNoEditFieldFilteredComponent } from '../sub-element-table-no-edit-field-filtered/sub-element-table-no-edit-field-filtered.component';
import { SubElementTableNoEditFieldNameComponent } from '../sub-element-table-no-edit-field-name/sub-element-table-no-edit-field-name.component';

@Component({
	selector: 'osee-messaging-sub-element-table-no-edit-field',
	templateUrl: './sub-element-table-no-edit-field.component.html',
	styles: [':host{display: block;width: 100%;height: 100%;}'],
	standalone: true,
	imports: [
		SubElementTableNoEditFieldDynamicWidthComponent,
		SubElementTableNoEditFieldFilteredComponent,
		SubElementTableNoEditFieldNameComponent,
		RouterLink,
		MatLabel,
		MatAnchor,
	],
})
export class SubElementTableNoEditFieldComponent implements OnInit {
	@Input() filter: string = '';
	@Input() element: element = elementSentinel;
	@Input() header!: keyof element;
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
