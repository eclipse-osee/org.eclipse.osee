/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { MatDialog } from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { DialogServiceMock } from '../../testing/mockDialogService.mock';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';

import { provideRouter } from '@angular/router';
import { ApplicabilityTableComponent } from './applicability-table.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';

describe('ApplicabilityTableComponent', () => {
	let component: ApplicabilityTableComponent;
	let fixture: ComponentFixture<ApplicabilityTableComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ApplicabilityTableComponent],
			providers: [
				provideRouter([]),
				provideNoopAnimations(),
				{ provide: DialogService, useValue: DialogServiceMock },
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) }, //TODO this is a bug...not quite sure why DI isn't accepting the dialog service mock
				},
				{ provide: MatDialog, useValue: {} },
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ApplicabilityTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
