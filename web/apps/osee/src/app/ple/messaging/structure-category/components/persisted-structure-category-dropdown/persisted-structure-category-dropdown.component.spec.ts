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

import { PersistedStructureCategoryDropdownComponent } from './persisted-structure-category-dropdown.component';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import { MockStructureCategoryDropdownComponent } from '@osee/messaging/structure-category/structure-category-dropdown/testing';
import { applicabilitySentinel } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

describe('PersistedStructureCategoryDropdownComponent', () => {
	let component: ParentDriverComponent;
	let fixture: ComponentFixture<ParentDriverComponent>;

	@Component({
		selector: 'osee-test-standalone-form',
		standalone: true,
		imports: [FormsModule, PersistedStructureCategoryDropdownComponent],
		template: `<form #testForm="ngForm">
			<osee-persisted-structure-category-dropdown
				[artifactId]="artifactId()"
				[artifactApplicability]="artifactApplicability()"
				[(value)]="value" />
		</form>`,
	})
	class ParentDriverComponent {
		artifactId = signal(`1` as const);
		artifactApplicability = signal(applicabilitySentinel);
		value = signal({
			id: '-1',
			typeId: ATTRIBUTETYPEIDENUM.INTERFACESTRUCTURECATEGORY,
			gammaId: '-1',
			value: '',
		});
	}

	beforeEach(async () => {
		await TestBed.overrideComponent(
			PersistedStructureCategoryDropdownComponent,
			{
				set: {
					imports: [MockStructureCategoryDropdownComponent],
				},
			}
		)
			.configureTestingModule({
				imports: [PersistedStructureCategoryDropdownComponent],
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
