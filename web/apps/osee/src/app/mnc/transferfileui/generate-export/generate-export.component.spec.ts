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
import { GenerateExportComponent } from './generate-export.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { transferFileHttpServiceMock } from '../../../mnc/lib/testing/transfer-file-http.service.mock';
import { TransferFileService } from '../../../mnc/services/transfer-file/transfer-file.service';

describe('GenerateExportComponent', () => {
	let component: GenerateExportComponent;
	let fixture: ComponentFixture<GenerateExportComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GenerateExportComponent, HttpClientTestingModule],
			providers: [
				{
					provide: TransferFileService,
					useValue: transferFileHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(GenerateExportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
