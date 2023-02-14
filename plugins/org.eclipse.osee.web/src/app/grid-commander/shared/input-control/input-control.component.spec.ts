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
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { CommandGroups } from '../../types/grid-commander-types/gc-user-and-contexts-relationships';

import { InputControlComponent } from './input-control.component';

describe('InputControlComponent', () => {
	let component: InputControlComponent<CommandGroups>;
	let fixture: ComponentFixture<InputControlComponent<CommandGroups>>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				CommonModule,
				FormsModule,
				HttpClientTestingModule,
				MatFormFieldModule,
				MatAutocompleteModule,
				MatDialogModule,
				MatIconModule,
				MatInputModule,
				NoopAnimationsModule,
				RouterTestingModule,
				InputControlComponent,
			],
			providers: [{ provide: MatDialogRef, useValue: {} }],
		}).compileComponents();

		fixture = TestBed.createComponent(InputControlComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
