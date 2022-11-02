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
import { MatTableDataSource } from '@angular/material/table';
import { RouterTestingModule } from '@angular/router/testing';
import { TestScheduler } from 'rxjs/testing';
import { StructureTableComponentMock } from '../../mocks/components/StructureTable.mock';
import { structuresMock } from '../../../shared/mocks/Structures.mock';
import { CurrentStateServiceMock } from '../../mocks/services/CurrentStateService.mock';
import { CurrentStructureService } from '../../services/current-structure.service';
import { structure } from '../../../shared/types/structure';

import { SingleStructureTableComponent } from './single-structure-table.component';

describe('SingleStructureTableComponent', () => {
	let component: SingleStructureTableComponent;
	let fixture: ComponentFixture<SingleStructureTableComponent>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [RouterTestingModule],
			providers: [
				{
					provide: CurrentStructureService,
					useValue: CurrentStateServiceMock,
				},
			],
			declarations: [
				SingleStructureTableComponent,
				StructureTableComponentMock,
			],
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

	it('should return structures', () => {
		scheduler.run(({ expectObservable }) => {
			const dataSource = new MatTableDataSource<structure>([
				structuresMock[0],
			]);
			const expectedValues = { a: dataSource.filteredData };
			const expected = '(a|)';
			expectObservable(component.messageData).toBe(
				expected,
				expectedValues
			);
		});
	});
});
