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

import { PersistedApplicabilityDropdownComponent } from './persisted-applicability-dropdown.component';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import { applicabilitySentinel } from '@osee/applicability/types';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

describe('PersistedApplicabilityDropdownComponent', () => {
	let component: ParentDriverComponent;
	let fixture: ComponentFixture<ParentDriverComponent>;

	@Component({
		selector: 'osee-test-standalone-form',
		standalone: true,
		imports: [FormsModule, PersistedApplicabilityDropdownComponent],
		template: `<form #testForm="ngForm">
			<osee-persisted-applicability-dropdown
				[artifactId]="artifactId()"
				[applicability]="artifactApplicability()" />
		</form>`,
	})
	class ParentDriverComponent {
		artifactId = signal(`1` as const);
		artifactApplicability = signal(applicabilitySentinel);
	}

	beforeEach(async () => {
		await TestBed.overrideComponent(
			PersistedApplicabilityDropdownComponent,
			{
				set: {
					imports: [MockApplicabilityDropdownComponent],
				},
			}
		)
			.configureTestingModule({
				imports: [PersistedApplicabilityDropdownComponent],
				providers: [
					{
						provide: CurrentTransactionService,
						useValue: currentTransactionServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ParentDriverComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
