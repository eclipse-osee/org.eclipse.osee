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

import { RouteStateService } from './route-state-service.service';

describe('RouteStateServiceService', () => {
	let service: RouteStateService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(RouteStateService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
