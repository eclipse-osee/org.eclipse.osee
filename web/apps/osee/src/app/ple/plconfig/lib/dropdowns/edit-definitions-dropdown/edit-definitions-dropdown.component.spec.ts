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
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';

import { EditDefinitionsDropdownComponent } from './edit-definitions-dropdown.component';

describe('EditDefinitionsDropdownComponent', () => {
	let component: EditDefinitionsDropdownComponent;
	let fixture: ComponentFixture<EditDefinitionsDropdownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				HttpClientModule,
				MatMenuModule,
				MatDialogModule,
				EditDefinitionsDropdownComponent,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(EditDefinitionsDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
