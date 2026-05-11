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
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { plConfigTypesServiceMock } from '../../testing/pl-config-types.service.mock';

import { ViewSelectorComponent } from '@osee/shared/components';
import { MockViewSelectorComponent } from '@osee/shared/components/testing';
import { AddConfigurationDialogComponent } from './add-configuration-dialog.component';
import { vi } from 'vitest';
import { of } from 'rxjs';

describe('AddConfigurationDialogComponent', () => {
	let component: AddConfigurationDialogComponent;
	let fixture: ComponentFixture<AddConfigurationDialogComponent>;

	beforeEach(async () => {
		const branchService = {
			getBranchApplicability: vi
				.fn()
				.mockName('PlConfigBranchService.getBranchApplicability'),
		};
		await TestBed.overrideComponent(AddConfigurationDialogComponent, {
			remove: {
				imports: [ViewSelectorComponent],
			},
			add: {
				imports: [MockViewSelectorComponent],
			},
		})
			.configureTestingModule({
				imports: [AddConfigurationDialogComponent],
				providers: [
					provideNoopAnimations(),
					{ provide: PlConfigBranchService, useValue: branchService },
					PlConfigCurrentBranchService,
					{
						provide: PlConfigTypesService,
						useValue: plConfigTypesServiceMock,
					},
					{ provide: MatDialogRef, useValue: {} },
					{
						provide: MAT_DIALOG_DATA,
						useValue: {
							currentBranch: '3182843164128526558',
							copyFrom: { id: '0', name: '' },
							title: '',
							productApplicabilities: [],
							group: [{ id: '0', name: '', configurations: [] }],
						},
					},
				],
			})
			.compileComponents();
		const currentBranchServiceReal = TestBed.inject(
			PlConfigCurrentBranchService
		);
		vi.spyOn(currentBranchServiceReal, 'cfgGroups', 'get').mockReturnValue(
			of([])
		);
		vi.spyOn(
			currentBranchServiceReal,
			'productTypes',
			'get'
		).mockReturnValue(of([]));
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(AddConfigurationDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
