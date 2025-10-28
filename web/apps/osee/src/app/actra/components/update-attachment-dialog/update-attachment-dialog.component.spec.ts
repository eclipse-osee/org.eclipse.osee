/*********************************************************************
 * Copyright (c) 2025 Boeing
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
	UpdateAttachmentDialogComponent,
	UpdateAttachmentDialogData,
} from './update-attachment-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AttachmentService } from '../../services/attachment.service';
import { AttachmentServiceMock } from '../../services/testing/attachments.service.mock';
import { MAX_ATTACHMENT_SIZE_BYTES } from '../../types/actra-types';

describe('UpdateAttachmentDialogComponent', () => {
	let component: UpdateAttachmentDialogComponent;
	let fixture: ComponentFixture<UpdateAttachmentDialogComponent>;

	const data: UpdateAttachmentDialogData = {
		attachment: {
			id: '321',
			fileName: 'Name',
			sizeBytes: 123,
		},
		maxFileSizeBytes: MAX_ATTACHMENT_SIZE_BYTES,
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [UpdateAttachmentDialogComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: AttachmentService,
					useValue: AttachmentServiceMock,
				},
				{
					provide: MAT_DIALOG_DATA,
					useValue: data,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(UpdateAttachmentDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
