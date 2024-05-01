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
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { DeleteRowDialogComponent } from './delete-row-dialog.component';

describe('CheckboxContainerComponent', () => {
	let component: DeleteRowDialogComponent;
	let fixture: ComponentFixture<DeleteRowDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatDialogModule, DeleteRowDialogComponent],
			providers: [
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						action: 'delete',
						object: { 'Artifact Id': '23456' },
					},
				},
				{ provide: MatDialogRef, useValue: {} },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(DeleteRowDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
