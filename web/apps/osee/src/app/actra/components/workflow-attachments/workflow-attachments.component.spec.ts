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

import { WorkflowAttachmentsComponent } from './workflow-attachments.component';
import { MatDialogRef } from '@angular/material/dialog';
import { AttachmentService } from '../../services/attachment.service';
import { AttachmentServiceMock } from '../../services/testing/attachments.service.mock';

describe('WorkflowAttachmentsComponent', () => {
	let component: WorkflowAttachmentsComponent;
	let fixture: ComponentFixture<WorkflowAttachmentsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [WorkflowAttachmentsComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: AttachmentService,
					useValue: AttachmentServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(WorkflowAttachmentsComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('teamWorkflowId', '123');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
