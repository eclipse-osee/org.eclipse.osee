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

import { BranchManagementComponent } from './branch-management.component';
import {
	BranchInfoService,
	CurrentBranchInfoService,
} from '@osee/shared/services';
import { BranchInfoServiceMock, testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';
import {
	ActionService,
	CurrentActionService,
} from '@osee/configuration-management/services';
import {
	actionServiceMock,
	currentActionServiceMock,
} from '@osee/configuration-management/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActionStateButtonService } from '../../../../../../configuration-management/components/internal/services/action-state-button.service';
import { actionStateButtonServiceMock } from '../../../../../../configuration-management/components/internal/services/action-state-button.service.mock';
import { CommitBranchService } from '@osee/commit/services';
import { commitBranchServiceMock } from '@osee/commit/testing';

describe('BranchManagementComponent', () => {
	let component: BranchManagementComponent;
	let fixture: ComponentFixture<BranchManagementComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [BranchManagementComponent, NoopAnimationsModule],
			providers: [
				{
					provide: CurrentBranchInfoService,
					useValue: {
						get currentBranch() {
							return of(testBranchInfo);
						},
						get parentBranch() {
							return of(testBranchInfo.parentBranch.id);
						},
					} as Partial<CurrentBranchInfoService>,
				},
				{ provide: ActionService, useValue: actionServiceMock },
				{
					provide: CurrentActionService,
					useValue: currentActionServiceMock,
				},
				{
					provide: ActionStateButtonService,
					useValue: actionStateButtonServiceMock,
				},
				{
					provide: CommitBranchService,
					useValue: commitBranchServiceMock,
				},
				{
					provide: BranchInfoService,
					useValue: BranchInfoServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(BranchManagementComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
