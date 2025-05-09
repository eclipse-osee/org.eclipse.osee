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

import { CreateBranchDialogComponent } from './create-branch-dialog.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

describe('CreateBranchDialogComponent', () => {
	let component: CreateBranchDialogComponent;
	let fixture: ComponentFixture<CreateBranchDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateBranchDialogComponent, NoopAnimationsModule],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: createBranchDialogDataMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateBranchDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});

const createBranchDialogDataMock = {
	branchName: '',
};
