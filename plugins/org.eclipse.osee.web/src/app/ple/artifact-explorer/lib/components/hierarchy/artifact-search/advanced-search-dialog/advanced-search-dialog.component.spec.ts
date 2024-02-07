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

import { AdvancedSearchDialogComponent } from './advanced-search-dialog.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { defaultAdvancedSearchCriteria } from '../../../../types/artifact-search';
import { AdvancedSearchFormMockComponent } from '../advanced-search-form/advanced-search-form.component.mock';
import { AdvancedSearchFormComponent } from '../advanced-search-form/advanced-search-form.component';

describe('AdvancedSearchDialogComponent', () => {
	let component: AdvancedSearchDialogComponent;
	let fixture: ComponentFixture<AdvancedSearchDialogComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(AdvancedSearchDialogComponent, {
			add: {
				imports: [AdvancedSearchFormMockComponent],
			},
			remove: {
				imports: [AdvancedSearchFormComponent],
			},
		})
			.configureTestingModule({
				imports: [AdvancedSearchDialogComponent],
				providers: [
					{
						provide: MAT_DIALOG_DATA,
						useValue: {
							...defaultAdvancedSearchCriteria,
						},
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(AdvancedSearchDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
