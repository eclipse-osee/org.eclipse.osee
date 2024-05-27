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
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { transferFileHttpServiceMock } from '../../../mnc/lib/testing/transfer-file-http.service.mock';
import { TransferFileService } from '../../../mnc/services/transfer-file/transfer-file.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('GenerateExportComponent', () => {
	let component: GenerateExportComponent;
	let fixture: ComponentFixture<GenerateExportComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GenerateExportComponent],
			providers: [
				{
					provide: TransferFileService,
					useValue: transferFileHttpServiceMock,
				},
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
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
