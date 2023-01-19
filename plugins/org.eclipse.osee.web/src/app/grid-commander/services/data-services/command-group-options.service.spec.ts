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
import { RouterTestingModule } from '@angular/router/testing';
import { TestScheduler } from 'rxjs/testing';

import { CommandGroupOptionsService } from './command-group-options.service';
import { GetUserContextRelationsMock } from '../../mock-data/get-user-context-relations.mock';
import { GetUserContextRelations } from '../fetch-data-services/get-user-context-relations.service';

describe('CommandGroupOptionsService', () => {
	let service: CommandGroupOptionsService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule, RouterTestingModule],
			providers: [
				{
					provide: GetUserContextRelations,
					useValue: GetUserContextRelationsMock,
				},
			],
		});
		service = TestBed.inject(CommandGroupOptionsService);
		httpTestingController = TestBed.inject(HttpTestingController);
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
});
