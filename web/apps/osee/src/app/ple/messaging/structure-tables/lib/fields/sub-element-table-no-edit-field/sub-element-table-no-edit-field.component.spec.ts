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
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubElementTableNoEditFieldComponent } from './sub-element-table-no-edit-field.component';

describe('SubElementTableNoEditFieldComponent', () => {
	let component: SubElementTableNoEditFieldComponent;
	let fixture: ComponentFixture<SubElementTableNoEditFieldComponent>;
	let router: any;

	beforeEach(async () => {
		router = jasmine.createSpyObj(
			'Router',
			['navigate', 'createUrlTree', 'serializeUrl'],
			['paramMap']
		);
		await TestBed.configureTestingModule({
			imports: [RouterTestingModule],
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
		fixture = TestBed.createComponent(SubElementTableNoEditFieldComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
