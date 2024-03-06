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
import { MergeManagerTableComponent } from './merge-manager-table.component';
import { commitBranchServiceMock } from '@osee/shared/testing';
import { CommitBranchService } from '@osee/shared/services';

describe('MergeManagerTableComponent', () => {
	let component: MergeManagerTableComponent;
	let fixture: ComponentFixture<MergeManagerTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MergeManagerTableComponent],
			providers: [
				{
					provide: CommitBranchService,
					useValue: commitBranchServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MergeManagerTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
