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
import { MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { of } from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testBranchApplicability } from '../../testing/mockBranchService';

import { CompoundApplicabilityDropdownComponent } from './compound-applicability-dropdown.component';

describe('CompoundApplicabilityDropdownComponent', () => {
	let component: CompoundApplicabilityDropdownComponent;
	let fixture: ComponentFixture<CompoundApplicabilityDropdownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				{
					provide: PlConfigCurrentBranchService,
					useValue: {
						branchApplicability: of(testBranchApplicability),
					},
				},
			],
			imports: [
				MatDialogModule,
				MatMenuModule,
				CompoundApplicabilityDropdownComponent,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			CompoundApplicabilityDropdownComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
