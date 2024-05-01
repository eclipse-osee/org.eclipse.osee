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
import { FormsModule } from '@angular/forms';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ConfigGroupDialogComponent } from './config-group-dialog.component';

describe('ConfigGroupDialogComponent', () => {
	let component: ConfigGroupDialogComponent;
	let fixture: ComponentFixture<ConfigGroupDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatFormFieldModule,
				MatInputModule,
				MatListModule,
				ConfigGroupDialogComponent,
				NoopAnimationsModule,
				FormsModule,
			],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						editable: true,
						configGroup: {
							id: '1',
							name: 'Group 1',
							views: [
								{
									id: '2',
									name: 'View 1',
									hasFeatureApplicabilities: true,
								},
								{
									id: '3',
									name: 'View 2',
									hasFeatureApplicabilities: true,
								},
								{
									id: '4',
									name: 'View 3',
									hasFeatureApplicabilities: true,
								},
							],
						},
					},
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ConfigGroupDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
