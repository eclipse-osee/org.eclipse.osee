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
import { StructureTableComponentMock } from '../lib/tables/structure-table/structure-table.component.mock';

import { SingleStructureTableComponent } from './single-structure-table.component';
import { CurrentStateServiceMock } from '../../shared/testing/current-structure.service.mock';
import { STRUCTURE_SERVICE_TOKEN } from '../../shared/tokens/injection/structure/token';
import { SINGLE_STRUCTURE_SERVICE } from '../../shared/tokens/injection/structure/single';
import { CurrentStructureSingleService } from '../../shared/services/ui/current-structure-single.service';
import { AsyncPipe } from '@angular/common';

describe('SingleStructureTableComponent', () => {
	let component: SingleStructureTableComponent;
	let fixture: ComponentFixture<SingleStructureTableComponent>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.overrideComponent(SingleStructureTableComponent, {
			set: {
				imports: [
					AsyncPipe,
					StructureTableComponentMock,
					RouterTestingModule,
				],
				providers: [
					{
						provide: CurrentStructureSingleService,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: SINGLE_STRUCTURE_SERVICE,
					},
				],
			},
		}).compileComponents();
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
