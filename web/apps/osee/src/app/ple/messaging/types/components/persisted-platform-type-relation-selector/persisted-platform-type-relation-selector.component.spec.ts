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

import { PersistedPlatformTypeRelationSelectorComponent } from './persisted-platform-type-relation-selector.component';
import { WarningDialogService } from '@osee/messaging/shared/services';
import {
	platformTypesMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { MockPlatformTypeDropdownComponent } from '@osee/messaging/types/dropdown/testing';
import { FormsModule } from '@angular/forms';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';

describe('PersistedPlatformTypeRelationSelectorComponent', () => {
	let component: PersistedPlatformTypeRelationSelectorComponent;
	let fixture: ComponentFixture<PersistedPlatformTypeRelationSelectorComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(
			PersistedPlatformTypeRelationSelectorComponent,
			{
				set: {
					imports: [FormsModule, MockPlatformTypeDropdownComponent],
					providers: [
						{
							provide: WarningDialogService,
							useValue: warningDialogServiceMock,
						},
						{
							provide: CurrentTransactionService,
							useValue: currentTransactionServiceMock,
						},
					],
				},
			}
		)
			.configureTestingModule({
				imports: [PersistedPlatformTypeRelationSelectorComponent],
				providers: [
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(
			PersistedPlatformTypeRelationSelectorComponent
		);
		fixture.componentRef.setInput('artifactId', '1234');
		fixture.componentRef.setInput('platformType', platformTypesMock[0]);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
