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

import { CreateBranchButtonComponent } from './create-branch-button.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {
	CurrentBranchInfoService,
	BranchInfoService,
	BranchRoutedUIService,
} from '@osee/shared/services';
import {
	testBranchInfo,
	BranchInfoServiceMock,
	branchRoutedUiServiceMock,
} from '@osee/shared/testing';
import { of } from 'rxjs';

describe('CreateBranchButtonComponent', () => {
	let component: CreateBranchButtonComponent;
	let fixture: ComponentFixture<CreateBranchButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateBranchButtonComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: createBranchDialogDataMock,
				},
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
				{
					provide: BranchInfoService,
					useValue: BranchInfoServiceMock,
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateBranchButtonComponent);
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
