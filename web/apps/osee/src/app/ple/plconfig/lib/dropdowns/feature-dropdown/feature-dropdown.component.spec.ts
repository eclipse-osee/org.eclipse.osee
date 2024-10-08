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
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchApplicability } from '../../testing/mockBranchService';
import { XResultData } from '@osee/shared/types';

import { FeatureDropdownComponent } from './feature-dropdown.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';

describe('FeatureDropdownComponent', () => {
	let component: FeatureDropdownComponent;
	let fixture: ComponentFixture<FeatureDropdownComponent>;

	beforeEach(async () => {
		const testResponse: XResultData = {
			empty: false,
			errorCount: 0,
			errors: false,
			failed: false,
			ids: [],
			infoCount: 0,
			numErrors: 0,
			numErrorsViaSearch: 0,
			numWarnings: 0,
			numWarningsViaSearch: 0,
			results: [],
			success: true,
			tables: [],
			title: '',
			txId: '2',
			warningCount: 0,
		};
		const branchService = jasmine.createSpyObj('PlConfigBranchService', [
			'deleteFeature',
			'modifyFeature',
			'addFeature',
		]);
		const _addFeatureSpy = branchService.addFeature.and.returnValue(
			of(testResponse)
		);
		const _deleteFeatureSpy = branchService.deleteFeature.and.returnValue(
			of(testResponse)
		);
		const _modifyFeatureSpy = branchService.modifyFeature.and.returnValue(
			of(testResponse)
		);
		await TestBed.configureTestingModule({
			imports: [FeatureDropdownComponent],
			providers: [
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
				{
					provide: PlConfigCurrentBranchService,
					useValue: {
						branchApplicability: of(testBranchApplicability),
					},
				},
				{ provide: PlConfigBranchService, useValue: branchService },
				{ provide: MatDialog, useValue: {} },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(FeatureDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
