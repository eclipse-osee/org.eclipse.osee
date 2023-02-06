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
import { structuresNameServiceMock } from '@osee/messaging/shared/testing';
import { TestScheduler } from 'rxjs/testing';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { StructureNamesService } from '../http/structure-names.service';

import { CurrentStructureNamesService } from './current-structure-names.service';

describe('CurrentStructureNamesService', () => {
	let service: CurrentStructureNamesService;
	let scheduler: TestScheduler;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: StructureNamesService,
					useValue: structuresNameServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentStructureNamesService);
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

	it('should fetch an array equal to getNames()', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.idValue = '10';
			expectObservable(service.names).toBe('a', { a: [] });
		});
	});
});
