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
import { MergeManagerDialogComponent } from './merge-manager-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommitBranchService } from '@osee/shared/services';
import { commitBranchServiceMock } from '@osee/shared/testing';

describe('MergeManagerDialogComponent', () => {
	let component: MergeManagerDialogComponent;
	let fixture: ComponentFixture<MergeManagerDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MergeManagerDialogComponent],
			providers: [
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						sourceBranch: '1234',
						parentBranch: '5678',
						validateResults: {
							committable: true,
							conflictCount: 2,
							conflictsResolved: 1,
						},
					},
				},
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: CommitBranchService,
					useValue: commitBranchServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MergeManagerDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
