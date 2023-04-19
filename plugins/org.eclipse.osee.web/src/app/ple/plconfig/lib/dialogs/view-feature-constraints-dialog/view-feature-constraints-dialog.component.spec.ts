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

import { ViewFeatureConstraintsDialogComponent } from './view-feature-constraints-dialog.component';
import { of } from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { testApplicsWithFeatureConstraints } from '../../testing/mockBranchService';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('ViewFeatureConstraintsDialogComponent', () => {
	let component: ViewFeatureConstraintsDialogComponent;
	let fixture: ComponentFixture<ViewFeatureConstraintsDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ViewFeatureConstraintsDialogComponent],
			providers: [
				{
					provide: PlConfigCurrentBranchService,
					useValue: {
						applicsWithFeatureConstraints: of(
							testApplicsWithFeatureConstraints
						),
					},
				},
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			ViewFeatureConstraintsDialogComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
