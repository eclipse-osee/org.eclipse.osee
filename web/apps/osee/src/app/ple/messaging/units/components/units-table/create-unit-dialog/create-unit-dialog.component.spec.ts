/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { CreateUnitDialogComponent } from './create-unit-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { dialogRef } from '@osee/messaging/shared/testing';
import { unit } from '@osee/messaging/units/types';
import { unitsMock } from '@osee/messaging/units/services/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('CreateUnitDialogComponent', () => {
	let component: CreateUnitDialogComponent;
	let fixture: ComponentFixture<CreateUnitDialogComponent>;
	const dialogData: unit = unitsMock[0];

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateUnitDialogComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: MatDialogRef,
					useValue: dialogRef,
				},
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateUnitDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
