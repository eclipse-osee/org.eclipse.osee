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
import { UnitsTableComponent } from './units-table.component';
import { UnitsService } from '@osee/messaging/units/services';
import { unitsServiceMock } from '@osee/messaging/units/services/testing';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('UnitsTableComponent', () => {
	let component: UnitsTableComponent;
	let fixture: ComponentFixture<UnitsTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [UnitsTableComponent],
			providers: [
				provideNoopAnimations(),
				{ provide: UnitsService, useValue: unitsServiceMock },
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(UnitsTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
