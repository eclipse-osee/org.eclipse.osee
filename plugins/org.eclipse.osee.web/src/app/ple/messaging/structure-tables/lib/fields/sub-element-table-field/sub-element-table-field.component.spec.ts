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

import { SubElementTableFieldComponent } from './sub-element-table-field.component';

describe('SubElementTableRowComponent', () => {
	let component: SubElementTableFieldComponent;
	let fixture: ComponentFixture<SubElementTableFieldComponent>;
	let router: any;

	beforeEach(async () => {
		router = jasmine.createSpyObj(
			'Router',
			['navigate', 'createUrlTree', 'serializeUrl'],
			['paramMap']
		);
		await TestBed.configureTestingModule({
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
		fixture = TestBed.createComponent(SubElementTableFieldComponent);
		component = fixture.componentInstance;
		component.header = 'applicability';
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should get enum literals as array of strings', () => {
		component.element = {
			...component.element,
			enumLiteral: '',
		};
		expect(component.getEnumLiterals()).toEqual(['']);
		component.element = {
			...component.element,
			enumLiteral: 'test',
		};
		expect(component.getEnumLiterals()).toEqual(['test']);
		component.element = {
			...component.element,
			enumLiteral: 'test\ntesting\ntested',
		};
		expect(component.getEnumLiterals()).toEqual([
			'test',
			'testing',
			'tested',
		]);
	});
});
