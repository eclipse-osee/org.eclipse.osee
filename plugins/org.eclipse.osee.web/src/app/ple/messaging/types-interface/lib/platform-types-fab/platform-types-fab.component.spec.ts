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

import { PlatformTypesFabComponent } from './platform-types-fab.component';
import { CurrentTypesService } from '../services/current-types.service';
import { of } from 'rxjs';
import { transactionResultMock } from '@osee/shared/transactions/testing';

describe('PlatformTypesFabComponent', () => {
	let component: PlatformTypesFabComponent;
	let fixture: ComponentFixture<PlatformTypesFabComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(PlatformTypesFabComponent, {
			remove: {
				providers: [CurrentTypesService],
			},
			add: {
				providers: [
					{
						provide: CurrentTypesService,
						useValue: {
							inEditMode: of(true),
							createType: of(transactionResultMock),
						},
					},
				],
			},
		})
			.configureTestingModule({
				imports: [PlatformTypesFabComponent],
			})
			.compileComponents();

		fixture = TestBed.createComponent(PlatformTypesFabComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
