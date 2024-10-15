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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import {
	testBranchApplicability,
	testCfgGroups,
} from '../../testing/mockBranchService';

import { ConfigurationGroupDropdownComponent } from './configuration-group-dropdown.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';

describe('ConfigurationGroupDropdownComponent', () => {
	let component: ConfigurationGroupDropdownComponent;
	let fixture: ComponentFixture<ConfigurationGroupDropdownComponent>;

	beforeEach(async () => {
		const _currentBranchService = jasmine.createSpyObj(
			'PlConfigCurrentBranchService',
			[],
			['cfgGroups', 'branchApplicability']
		);
		const uiService = jasmine.createSpyObj(
			'PlConfigUIStateService',
			[],
			['updateReqConfig']
		);
		await TestBed.configureTestingModule({
			imports: [ConfigurationGroupDropdownComponent],
			providers: [
				{ provide: MatDialog, useValue: {} },
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
				{
					provide: PlConfigCurrentBranchService,
					useValue: {
						branchApplicability: of(testBranchApplicability),
						cfgGroups: of(testCfgGroups),
					},
				},
				{ provide: PlConfigUIStateService, useValue: uiService },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ConfigurationGroupDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
