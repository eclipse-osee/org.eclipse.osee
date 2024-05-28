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
import { CurrentActionDropDownComponent } from './current-action-drop-down.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { of } from 'rxjs';
import { testBranchInfo } from '@osee/shared/testing';
import {
	ActionDropdownStub,
	CreateActionButtonMockComponent,
	currentActionServiceMock,
} from '@osee/configuration-management/testing';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';
import { actionStateButtonServiceMock } from '../internal/services/action-state-button.service.mock';
import { CurrentActionService } from '@osee/configuration-management/services';

describe('CurrentActionDropDownComponent', () => {
	let component: CurrentActionDropDownComponent;
	let fixture: ComponentFixture<CurrentActionDropDownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(CurrentActionDropDownComponent, {
			set: {
				imports: [ActionDropdownStub, CreateActionButtonMockComponent],
			},
		})
			.configureTestingModule({
				imports: [CurrentActionDropDownComponent],
				providers: [
					{
						provide: ActionStateButtonService,
						useValue: actionStateButtonServiceMock,
					},
					{
						provide: CurrentBranchInfoService,
						useValue: { currentBranch: of(testBranchInfo) },
					},
					{
						provide: CurrentActionService,
						useValue: currentActionServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(CurrentActionDropDownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
