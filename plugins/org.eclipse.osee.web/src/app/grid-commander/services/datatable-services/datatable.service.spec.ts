/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { TestBed } from '@angular/core/testing';
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { ResponseTableData } from '../../../grid-commander/types/grid-commander-types/table-data-types';
import { columnDataMock } from '../../mock-data/mock-tableData';

import { DataTableService } from './datatable.service';

describe('DataTableService', () => {
	let service: DataTableService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
			providers: [DataTableService],
		});
		service = TestBed.inject(DataTableService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('will set bs _combinedDataTable to the data of type ResponseTableData that is passed in', (done: DoneFn) => {
		const mockResponseTableData: ResponseTableData = {
			tableOptions: {
				columns: [{ name: 'testKey', type: 'testType' }],
			},
			data: [
				[
					'test1',
					'this is a test1',
					'test@test1.com',
					'number',
					'permissionType1',
					'1!',
				],
			],
		};

		service.combinedDataTableResponseData = mockResponseTableData;
		service._combinedDataTable.subscribe((val) => {
			expect(val).toBeInstanceOf(Object);
			expect(val).toEqual(mockResponseTableData);
			done();
		});
	});

	it('get columnSchema should return array of length >= 0', (done: DoneFn) => {
		service.columnSchemaVals = columnDataMock;
		service.columnSchema.subscribe((val) => {
			expect(val).toBeInstanceOf(Array);
			expect(val.length).toBeGreaterThanOrEqual(0);
			done();
		});
	});

	it('get displayedCols should return an array of strings', (done: DoneFn) => {
		service.displayedColumns = ['testCol1', 'testCol2', 'testCol3'];

		service.displayedCols.subscribe((val) => {
			expect(val).toBeInstanceOf(Array);
			expect(val.length).toBeGreaterThanOrEqual(0);
			done();
		});
	});
});
