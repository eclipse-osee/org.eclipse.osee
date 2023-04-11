/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { navigationElement } from '@osee/shared/types';

import { HelpService } from './help.service';
import { helpHttpServiceMock } from '@osee/shared/testing';
import { HelpHttpService } from '@osee/shared/services/help';

describe('HelpService', () => {
	let service: HelpService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: HelpHttpService, useValue: helpHttpServiceMock },
			],
		});
		service = TestBed.inject(HelpService);
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

	it('should get nav elements', () => {
		scheduler.run(() => {
			const navElements: navigationElement[] = [
				{
					label: 'Help Header',
					cypressLabel: 'cy-help-nav-1',
					pageTitle: '',
					isDropdown: true,
					isDropdownOpen: false,
					requiredRoles: [],
					routerLink: 'page/1',
					icon: '',
					children: [
						{
							label: 'Help Page 1',
							cypressLabel: 'cy-help-nav-11',
							pageTitle: '',
							isDropdown: false,
							isDropdownOpen: false,
							requiredRoles: [],
							routerLink: 'page/11',
							icon: '',
							children: [],
						},
					],
				},
			];
			scheduler
				.expectObservable(service.getHelpNavElements('APP'))
				.toBe('(a|)', { a: navElements });
		});
	});
});
