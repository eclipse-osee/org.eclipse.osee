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
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';

import { EditCoursesDropdownComponent } from './edit-courses-dropdown.component';

describe('EditCoursesDropdownComponent', () => {
	let component: EditCoursesDropdownComponent;
	let fixture: ComponentFixture<EditCoursesDropdownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatMenuModule,
				MatIconModule,
				EditCoursesDropdownComponent,
			],
			providers: [{ provide: MatDialog, useValue: {} }],
		}).compileComponents();

		fixture = TestBed.createComponent(EditCoursesDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
