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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { SubElementTableNoEditFieldDynamicWidthComponent } from '../sub-element-table-no-edit-field-dynamic-width/sub-element-table-no-edit-field-dynamic-width.component';
import { SubElementTableNoEditFieldFilteredComponent } from '../sub-element-table-no-edit-field-filtered/sub-element-table-no-edit-field-filtered.component';
import { SubElementTableNoEditFieldNameComponent } from '../sub-element-table-no-edit-field-name/sub-element-table-no-edit-field-name.component';
import { SubElementTableNoEditFieldComponent } from '../sub-element-table-no-edit-field/sub-element-table-no-edit-field.component';

import { SubElementTableRowComponent } from './sub-element-table-row.component';

describe('SubElementTableRowComponent', () => {
	let component: SubElementTableRowComponent;
	let fixture: ComponentFixture<SubElementTableRowComponent>;
	let router: any;

	beforeEach(async () => {
		router = jasmine.createSpyObj(
			'Router',
			['navigate', 'createUrlTree', 'serializeUrl'],
			['paramMap']
		);
		await TestBed.configureTestingModule({
			declarations: [
				SubElementTableRowComponent,
				SubElementTableNoEditFieldComponent,
				SubElementTableNoEditFieldDynamicWidthComponent,
				SubElementTableNoEditFieldFilteredComponent,
				SubElementTableNoEditFieldNameComponent,
			],
			providers: [
				{ provide: Router, useValue: router },
				{
					provide: ActivatedRoute,
					useValue: {
						paramMap: of(
							convertToParamMap({
								branchId: '10',
								branchType: 'working',
							})
						),
					},
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubElementTableRowComponent);
		component = fixture.componentInstance;
		component.header = 'applicability';
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
