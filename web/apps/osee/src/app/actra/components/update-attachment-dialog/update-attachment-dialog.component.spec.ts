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

import { UpdateAttachmentDialogComponent } from './update-attachment-dialog.component';

describe('UpdateAttachmentDialogComponent', () => {
	let component: UpdateAttachmentDialogComponent;
	let fixture: ComponentFixture<UpdateAttachmentDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [UpdateAttachmentDialogComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(UpdateAttachmentDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
