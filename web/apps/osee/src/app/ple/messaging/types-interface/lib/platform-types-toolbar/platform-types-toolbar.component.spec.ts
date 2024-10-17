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

import { PlatformTypesToolbarComponent } from './platform-types-toolbar.component';
import { settingsDialogData } from '@osee/messaging/shared/types';
import { of } from 'rxjs';
import { transactionMock } from '@osee/transactions/testing';
import { CurrentTypesService } from '../services/current-types.service';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('PlatformTypesToolbarComponent', () => {
	let component: PlatformTypesToolbarComponent;
	let fixture: ComponentFixture<PlatformTypesToolbarComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(PlatformTypesToolbarComponent, {
			add: {
				providers: [
					{
						provide: CurrentTypesService,
						useValue: {
							typeDataCount: of(10),
							currentPage: of(0),
							currentPageSize: of(10),
							inEditMode: of(true),
							updatePreferences(
								_preferences: settingsDialogData
							) {
								return of(transactionMock);
							},
						},
					},
				],
			},
		})
			.configureTestingModule({
				imports: [PlatformTypesToolbarComponent],
				providers: [provideNoopAnimations()],
			})
			.compileComponents();

		fixture = TestBed.createComponent(PlatformTypesToolbarComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
