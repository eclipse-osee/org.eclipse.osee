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
import { apiURL } from '../../../../../environments/environment';
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';

import { GetArtifactDataTableService } from './get-artifact-data-table.service';
import { artifactAttributesByRowMock } from '../../../mock-data/mock-tableData';

describe('GetArtifactDataTableService', () => {
	let service: GetArtifactDataTableService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(GetArtifactDataTableService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should call /orcs/branch/{id}/artifact/table?artifactType={type}', () => {
		const testInfo: string[][] = artifactAttributesByRowMock;
		const id = '10';
		const type = '11';
		service.getArtifactTableData(type, id).subscribe();

		const req = httpTestingController.expectOne(
			apiURL + `/orcs/branch/${id}/artifact/table?artifactType=${type}`
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testInfo);
		httpTestingController.verify();
	});
});
