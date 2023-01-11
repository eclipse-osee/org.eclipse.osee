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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import {
	MatRadioChange,
	MatRadioGroup,
	MatRadioModule,
} from '@angular/material/radio';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BranchTypeSelectorComponent } from './branch-type-selector.component';

describe('BranchTypeSelectorComponent', () => {
	let component: BranchTypeSelectorComponent;
	let fixture: ComponentFixture<BranchTypeSelectorComponent>;
	let router: Router;

	@Component({
		selector: 'osee-dummy',
		template: '<div>Dummy</div>',
	})
	class DummyComponent {}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				RouterTestingModule.withRoutes([
					{ path: '', component: DummyComponent },
					{ path: ':branchType', component: DummyComponent },
					{
						path: ':branchType/:branchId',
						component: DummyComponent,
					},
				]),
				MatRadioModule,
				FormsModule,
				BranchTypeSelectorComponent,
			],
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(BranchTypeSelectorComponent);
		component = fixture.componentInstance;
		router = TestBed.inject(Router);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should change the branch type to hello', () => {
		const spy = spyOn(router, 'navigate').and.returnValue(
			new Promise(() => true)
		);
		component.changeBranchType('hello');
		expect(component.branchType).toEqual('hello');
	});
});
