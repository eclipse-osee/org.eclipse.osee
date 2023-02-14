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
import { CommandPaletteComponent } from './command-palette.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { HelperdialogComponent } from '../helperdialog/helperdialog.component';
import { MatIconModule } from '@angular/material/icon';
import { CheckboxContainerComponent } from '../checkbox-container/checkbox-container.component';

describe('CommandPaletteComponent', () => {
	let component: CommandPaletteComponent;
	let fixture: ComponentFixture<CommandPaletteComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatAutocompleteModule,
				MatDialogModule,
				MatIconModule,
				CommonModule,
				HttpClientTestingModule,
				CommandPaletteComponent,
				HelperdialogComponent,
				CheckboxContainerComponent,
			],
			providers: [{ provide: MatDialogRef, useValue: {} }],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CommandPaletteComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
