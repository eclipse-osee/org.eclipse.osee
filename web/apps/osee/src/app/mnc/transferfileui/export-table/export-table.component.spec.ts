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
import { ExportTableComponent } from './export-table.component';
import { transferFileHttpServiceMock } from '../../../mnc/lib/testing/transfer-file-http.service.mock';
import { TransferFileService } from '../../../mnc/services/transfer-file/transfer-file.service';
import { TransferData } from '../../../mnc/types/transfer-file/transferdata';

describe('ExportTableComponent', () => {
	let component: ExportTableComponent;
	let fixture: ComponentFixture<ExportTableComponent>;
	const transferDataMock: TransferData[] = [
		{
			source: '124388928741',
			date: '05/29/2024 15:37',
			file: 'OSEETransfer-124388928741-20240529153726-0949.zip',
		},
	];
	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ExportTableComponent],
			providers: [
				{
					provide: TransferFileService,
					useValue: transferFileHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ExportTableComponent);
		fixture.componentRef.setInput('exportedData', transferDataMock);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
