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
import { affectedArtifactHttpServiceMock } from '@osee/messaging/shared/testing';
import { TestScheduler } from 'rxjs/testing';
import { UiService } from '@osee/shared/services';
import { AffectedArtifactService } from '../http/affected-artifact.service';

import { BranchedAffectedArtifactService } from './branched-affected-artifact.service';

describe('BranchedAffectedArtifactService', () => {
	let service: BranchedAffectedArtifactService;
	let scheduler: TestScheduler;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: AffectedArtifactService,
					useValue: affectedArtifactHttpServiceMock,
				},
			],
		});
		service = TestBed.inject(BranchedAffectedArtifactService);
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

	it('should fetch enum set array', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.idValue = '10';
			expectObservable(service.getEnumSetsByEnums('20')).toBe('(a|)', {
				a: [],
			});
		});
	});

	it('should fetch platform type array', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.idValue = '10';
			expectObservable(service.getPlatformTypesByEnumSet('20')).toBe(
				'(a|)',
				{ a: [] }
			);
		});
	});

	it('should fetch element array', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.idValue = '10';
			expectObservable(service.getElementsByType('20')).toBe('(a|)', {
				a: [],
			});
		});
	});

	it('should fetch structure array', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.idValue = '10';
			expectObservable(service.getStructuresByElement('20')).toBe(
				'(a|)',
				{ a: [] }
			);
		});
	});

	it('should fetch submessage array', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.idValue = '10';
			expectObservable(service.getSubMessagesByStructure('20')).toBe(
				'(a|)',
				{ a: [] }
			);
		});
	});

	it('should fetch element array', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.idValue = '10';
			expectObservable(service.getMessagesBySubMessage('20')).toBe(
				'(a|)',
				{ a: [] }
			);
		});
	});
});
