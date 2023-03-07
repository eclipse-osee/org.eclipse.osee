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

import { EditRolesDropdownComponent } from './edit-roles-dropdown.component';

describe('EditRolesDropdownComponent', () => {
	let component: EditRolesDropdownComponent;
	let fixture: ComponentFixture<EditRolesDropdownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatMenuModule, MatIconModule, EditRolesDropdownComponent],
			providers: [{ provide: MatDialog, useValue: {} }],
		}).compileComponents();

		fixture = TestBed.createComponent(EditRolesDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
