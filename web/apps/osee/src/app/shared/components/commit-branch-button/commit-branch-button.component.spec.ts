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
import { commitBranchServiceMock } from '../../testing/commit-branch.service.mock';
import { CommitBranchService } from '@osee/shared/services';

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
