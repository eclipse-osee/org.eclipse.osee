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
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { ActionService } from '@osee/shared/services';
import { ActionDropDownComponent } from './action-drop-down.component';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';
import {
	actionStateButtonServiceMock,
	actionStateButtonServiceMockApprove,
	actionStateButtonServiceMockCommit,
} from '../internal/services/action-state-button.service.mock';
import { MatButtonHarness } from '@angular/material/button/testing';
import { CreateAction } from '@osee/shared/types/configuration-management';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActionUserService } from '../internal/services/action-user.service';
import { MockActionUserService } from '../internal/services/action-user.service.mock';
import { UserDataAccountService } from '@osee/auth';
import { actionServiceMock, MockUserResponse } from '@osee/shared/testing';
import { userDataAccountServiceMock } from '@osee/auth/testing';

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
						provide: ActionUserService,
						useValue: MockActionUserService,
					},
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
		fixture.detectChanges();
	});
	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
