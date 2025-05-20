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
import { CommitBranchButtonComponent } from './commit-branch-button.component';
import { CommitBranchService } from '@osee/commit/services';
import { commitBranchServiceMock } from '@osee/commit/testing';
import { BranchRoutedUIService } from '@osee/shared/services';
import { branchRoutedUiServiceMock } from '@osee/shared/testing';

describe('CommitBranchButtonComponent', () => {
	let component: CommitBranchButtonComponent;
	let fixture: ComponentFixture<CommitBranchButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CommitBranchButtonComponent],
			providers: [
				{
					provide: CommitBranchService,
					useValue: commitBranchServiceMock,
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CommitBranchButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
