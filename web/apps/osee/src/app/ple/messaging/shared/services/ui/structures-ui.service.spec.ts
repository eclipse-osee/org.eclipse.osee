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
import { TestScheduler } from 'rxjs/testing';
import { StructuresUiService } from './structures-ui.service';

describe('UiService', () => {
	let service: StructuresUiService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(StructuresUiService);
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

	it('should set branch value', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: '1', b: '2' };
			const expectedMarble = 'a';
			scheduler
				.expectObservable(service.BranchId)
				.toBe(expectedMarble, expectedfilterValues);
			service.BranchIdString = '1';
			service.BranchIdString = '1';
			service.BranchIdString = '2';
			service.BranchIdString = '1';
		});
	});

	it('should set message value', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: '1', b: '2' };
			const expectedMarble = 'a';
			scheduler
				.expectObservable(service.messageId)
				.toBe(expectedMarble, expectedfilterValues);
			service.messageIdString = '1';
			service.messageIdString = '1';
			service.messageIdString = '2';
			service.messageIdString = '1';
		});
	});
});
