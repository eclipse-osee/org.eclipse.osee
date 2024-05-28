/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { HarnessLoader } from '@angular/cdk/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { BranchInfoService } from '@osee/shared/services';
import { ActionDropDownComponent } from './action-drop-down.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { UserDataAccountService } from '@osee/auth';
import {
	BranchInfoServiceMock,
	teamWorkflowDetailsMock,
} from '@osee/shared/testing';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { CommitBranchService } from '@osee/commit/services';
import { commitBranchServiceMock } from '@osee/commit/testing';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';
import { actionStateButtonServiceMock } from '../internal/services/action-state-button.service.mock';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';

describe('ActionDropDownComponent', () => {
	let component: ActionDropDownComponent;
	let fixture: ComponentFixture<ActionDropDownComponent>;
	let loader: HarnessLoader;
	beforeEach(async () => {
		await TestBed.overrideComponent(ActionDropDownComponent, {
			set: {
				providers: [
					{
						provide: ActionStateButtonService,
						useValue: actionStateButtonServiceMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{ provide: ActionService, useValue: actionServiceMock },
					{ provide: Router, useValue: { navigate: () => {} } },
					{
						provide: ActivatedRoute,
						useValue: {
							paramMap: of({
								branchId: '10',
								branchType: 'all',
							}),
						},
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
			},
		})
			.configureTestingModule({
				imports: [
					MatButtonModule,
					MatIconModule,
					MatDialogModule,
					NoopAnimationsModule,
				],
				declarations: [],
				providers: [
					{
						provide: ActionStateButtonService,
						useValue: actionStateButtonServiceMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{ provide: ActionService, useValue: actionServiceMock },
					{ provide: Router, useValue: { navigate: () => {} } },
					{
						provide: ActivatedRoute,
						useValue: {
							paramMap: of({
								branchId: '10',
								branchType: 'all',
							}),
						},
					},
				],
			})
			.compileComponents();
	});
	beforeEach(() => {
		fixture = TestBed.createComponent(ActionDropDownComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('teamWorkflow', teamWorkflowDetailsMock);
		fixture.detectChanges();
	});
	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
