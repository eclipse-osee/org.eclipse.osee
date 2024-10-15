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
import { UpdateFromParentButtonComponent } from './update-from-parent-button.component';
import {
	BranchInfoService,
	BranchRoutedUIService,
} from '@osee/shared/services';
import {
	BranchInfoServiceMock,
	branchRoutedUiServiceMock,
} from '@osee/shared/testing';
import { CommitBranchService } from '@osee/commit/services';
import { commitBranchServiceMock } from '@osee/commit/testing';

describe('UpdateFromParentButtonComponent', () => {
	let component: UpdateFromParentButtonComponent;
	let fixture: ComponentFixture<UpdateFromParentButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [UpdateFromParentButtonComponent],
			providers: [
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
				{
					provide: BranchInfoService,
					useValue: BranchInfoServiceMock,
				},
				{
					provide: CommitBranchService,
					useValue: commitBranchServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(UpdateFromParentButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
