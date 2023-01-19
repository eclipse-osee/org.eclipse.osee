/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { CheckboxContainerComponent } from './checkbox-container.component';

describe('CheckboxContainerComponent', () => {
	let component: CheckboxContainerComponent;
	let fixture: ComponentFixture<CheckboxContainerComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatFormFieldModule,
				MatInputModule,
				MatCheckboxModule,
				FormsModule,
			],
			declarations: [CheckboxContainerComponent],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CheckboxContainerComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
