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
import { TestBed } from '@angular/core/testing';
import { enumerationSetServiceMock } from '@osee/messaging/shared/testing';
import { UiService } from '@osee/shared/services';
import { transactionResultMock } from '@osee/transactions/testing';
import { TestScheduler } from 'rxjs/testing';
import { EnumerationSetService } from '../http/enumeration-set.service';

import { applicabilitySentinel } from '@osee/applicability/types';
import { EnumerationUIService } from './enumeration-ui.service';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';

describe('EnumerationUIService', () => {
	let service: EnumerationUIService;
	let uiService: UiService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
				},
			],
		});
		service = TestBed.inject(EnumerationUIService);
		uiService = TestBed.inject(UiService);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should change an enum set', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(
					service.changeEnumSet(
						{
							id: '-1',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'asdf',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: '',
							},
							applicability: applicabilitySentinel,
							enumerations: [],
						},
						{
							id: '-1',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: '',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: '',
							},
							applicability: applicabilitySentinel,
							enumerations: [],
						}
					)
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});
});
