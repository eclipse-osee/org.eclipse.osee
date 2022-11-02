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
import { SideNavService } from './side-nav.service';

describe('SideNavService', () => {
	let service: SideNavService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(SideNavService);
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

	it('should open the sidenav', () => {
		scheduler.run(({ expectObservable }) => {
			service.sideNav = {
				opened: true,
				field: '',
				currentValue: '',
				previousValue: undefined,
				transaction: { id: '1', branchId: '1' },
				user: '',
				date: '',
			};
			expectObservable(service.opened).toBe('a', { a: true });
		});
	});
});
