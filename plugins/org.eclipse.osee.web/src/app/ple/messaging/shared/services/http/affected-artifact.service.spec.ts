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
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '../../../../../../environments/environment';

import { AffectedArtifactService } from './affected-artifact.service';

describe('AffectedArtifactService', () => {
	let service: AffectedArtifactService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(AffectedArtifactService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should query affected EnumSets', () => {
		const branchId = '10';
		const otherId = '20';
		service.getEnumSetsByEnums('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + branchId + '/affected/enums/' + otherId
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should query affected Platform Types', () => {
		const branchId = '10';
		const otherId = '20';
		service.getPlatformTypesByEnumSet('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + branchId + '/affected/enumsets/' + otherId
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should query affected Elements', () => {
		const branchId = '10';
		const otherId = '20';
		service.getElementsByType('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + branchId + '/affected/types/' + otherId
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should query affected Structures', () => {
		const branchId = '10';
		const otherId = '20';
		service.getStructuresByElement('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + branchId + '/affected/elements/' + otherId
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should query affected SubMessages', () => {
		const branchId = '10';
		const otherId = '20';
		service.getSubMessagesByStructure('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/affected/structures/' +
				otherId
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should query affected Messages', () => {
		const branchId = '10';
		const otherId = '20';
		service.getMessagesBySubMessage('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/affected/submessages/' +
				otherId
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});
});
