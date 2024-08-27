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
import { CreateCiSetDialogComponent } from './create-ci-set-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CISet, CISetSentinel } from '../../../../types/tmo';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('CreateCiSetDialogComponent', () => {
	let component: CreateCiSetDialogComponent;
	let fixture: ComponentFixture<CreateCiSetDialogComponent>;
	const dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
	const dialogData: CISet = {
		...CISetSentinel,
		name: {
			id: '1234',
			gammaId: '3456',
			typeId: '1152921504606847088',
			value: 'Test Set',
		},
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateCiSetDialogComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: MatDialogRef,
					useValue: dialogRef,
				},
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateCiSetDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
