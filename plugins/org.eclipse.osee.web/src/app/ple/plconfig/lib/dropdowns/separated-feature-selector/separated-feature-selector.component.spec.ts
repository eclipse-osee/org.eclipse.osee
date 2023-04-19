/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';
import { plConfigTypesServiceMock } from '../../testing/pl-config-types.service.mock';

import { SeparatedFeatureSelectorComponent } from './separated-feature-selector.component';

describe('SeparatedFeatureSelectorComponent', () => {
	let component: SeparatedFeatureSelectorComponent;
	let fixture: ComponentFixture<SeparatedFeatureSelectorComponent>;

	beforeEach(async () => {
		const branchService = jasmine.createSpyObj('PlConfigBranchService', [
			'getBranchApplicability',
		]);
		await TestBed.configureTestingModule({
			imports: [SeparatedFeatureSelectorComponent, NoopAnimationsModule],
			providers: [
				{ provide: PlConfigBranchService, useValue: branchService },
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
				{
					provide: PlConfigTypesService,
					useValue: plConfigTypesServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(SeparatedFeatureSelectorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
