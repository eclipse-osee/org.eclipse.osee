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
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '@osee/environments';

import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { platformTypesMock } from '@osee/messaging/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { transactionBuilderMock } from '@osee/shared/transactions-legacy/testing';
import { txMock } from '@osee/transactions/testing';
import { TypesService } from './types.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('TypesService', () => {
	let service: TypesService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				{
					provide: TransactionBuilderService,
					useValue: transactionBuilderMock,
				},
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(TypesService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should create a platform type transaction', () => {
		expect(
			service.addNewPlatformTypeToTransaction(
				platformTypesMock[0],
				txMock,
				undefined
			)
		).toBe(txMock);
	});

	it('should perform a mutation', () => {
		service.performMutation({ branch: '10', txComment: '' }).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs');
		expect(req.request.method).toEqual('POST');
		req.flush({});
		httpTestingController.verify();
	});

	it('should fetch logical types', () => {
		service.logicalTypes.subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/logicalType'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should fetch logical type details', () => {
		service.getLogicalTypeFormDetail('10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/logicalType/' + 10
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get filtered types', () => {
		service.getPaginatedFilteredTypes('', '10', 10, 1).subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				10 +
				'/types/filter' +
				'?count=10&pageNum=1' +
				'&orderByAttributeType=' +
				ATTRIBUTETYPEIDENUM.NAME
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get a platform type', () => {
		service.getType('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 10 + '/types/' + 20
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});
});
