/*********************************************************************
 * Copyright (c) 2026 Boeing
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
	UpdateNativeContentDialogComponent,
	UpdateNativeContentDialogData,
} from './update-native-content-dialog.component';
import { MAX_FILE_SIZE_BYTES } from '@osee/shared/types';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

describe('UpdateNativeContentDialogComponent', () => {
	let component: UpdateNativeContentDialogComponent;
	let fixture: ComponentFixture<UpdateNativeContentDialogComponent>;

	const data: UpdateNativeContentDialogData = {
		file: {
			fileName: 'Name',
			sizeBytes: 123,
		},
		maxFileSizeBytes: MAX_FILE_SIZE_BYTES,
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [UpdateNativeContentDialogComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: data,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(UpdateNativeContentDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
