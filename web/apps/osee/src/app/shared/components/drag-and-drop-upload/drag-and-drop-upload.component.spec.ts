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

import { DragAndDropUploadComponent } from './drag-and-drop-upload.component';

describe('DragAndDropUploadComponent', () => {
	let component: DragAndDropUploadComponent;
	let fixture: ComponentFixture<DragAndDropUploadComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [DragAndDropUploadComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(DragAndDropUploadComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
