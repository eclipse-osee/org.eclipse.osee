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
import { RouterTestingModule } from '@angular/router/testing';
import { TestScheduler } from 'rxjs/testing';

import { SingleStructureTableComponent } from './single-structure-table.component';
import { AsyncPipe } from '@angular/common';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { StructureTableComponentMock } from '@osee/messaging/structure-tables/testing';
import { CurrentStructureSingleService } from '@osee/messaging/shared/services';
import {
	STRUCTURE_SERVICE_TOKEN,
	SINGLE_STRUCTURE_SERVICE,
} from '@osee/messaging/shared/tokens';
import { provideRouter } from '@angular/router';

describe('SingleStructureTableComponent', () => {
	let component: SingleStructureTableComponent;
	let fixture: ComponentFixture<SingleStructureTableComponent>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.overrideComponent(SingleStructureTableComponent, {
			set: {
				imports: [AsyncPipe, StructureTableComponentMock],
				providers: [
					{
						provide: CurrentStructureSingleService,
						useValue: CurrentStateServiceMock,
					},
					SINGLE_STRUCTURE_SERVICE,
				],
			},
		})
			.configureTestingModule({
				providers: [provideRouter([])],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SingleStructureTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual.filteredData).toEqual(expected.filteredData);
			}))
	);
	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
