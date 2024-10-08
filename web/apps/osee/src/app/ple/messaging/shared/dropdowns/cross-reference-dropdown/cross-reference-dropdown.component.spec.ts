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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
	ConnectionService,
	CrossReferenceHttpService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import {
	CrossReferenceHttpServiceMock,
	connectionServiceMock,
	preferencesUiServiceMock,
} from '@osee/messaging/shared/testing';

import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { CrossReferenceDropdownComponent } from './cross-reference-dropdown.component';

describe('CrossReferenceDropdownComponent', () => {
	let component: CrossReferenceDropdownComponent;
	let fixture: ComponentFixture<CrossReferenceDropdownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(CrossReferenceDropdownComponent, {
			set: {
				providers: [
					{
						provide: CrossReferenceHttpService,
						useValue: CrossReferenceHttpServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
					{
						provide: ConnectionService,
						useValue: connectionServiceMock,
					},
					{
						provide: PreferencesUIService,
						useValue: preferencesUiServiceMock,
					},
				],
				viewProviders: [],
			},
		})
			.configureTestingModule({
				imports: [CrossReferenceDropdownComponent],
				providers: [
					provideNoopAnimations(),
					{
						provide: CrossReferenceHttpService,
						useValue: CrossReferenceHttpServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
					{
						provide: ConnectionService,
						useValue: connectionServiceMock,
					},
					{
						provide: PreferencesUIService,
						useValue: preferencesUiServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(CrossReferenceDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
