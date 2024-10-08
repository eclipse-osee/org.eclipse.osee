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

import { MessageUiService } from './messages-ui.service';

describe('UiService', () => {
	let service: MessageUiService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(MessageUiService);
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

	it('should set update value', () => {
		scheduler.run(({ cold }) => {
			const expectedfilterValues = { a: true, b: false };
			const delayMarble = '-a';
			const expectedMarble = '101ms a';
			cold(delayMarble).subscribe(() => (service.updateMessages = true));
			scheduler
				.expectObservable(service.UpdateRequired)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});
});
