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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { dialogRef } from '../../../../shared/testing/dialog-ref.util.mock';

import { DeleteMessageDialogComponent } from './delete-message-dialog.component';

describe('DeleteMessageDialogComponent', () => {
	let component: DeleteMessageDialogComponent;
	let fixture: ComponentFixture<DeleteMessageDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [],
			imports: [
				MatDialogModule,
				MatButtonModule,
				DeleteMessageDialogComponent,
			],
			providers: [
				{ provide: MatDialogRef, useValue: dialogRef },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						message: {
							name: 'abcdef',
						},
					},
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(DeleteMessageDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
