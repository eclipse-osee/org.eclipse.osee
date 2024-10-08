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

import { BranchIdService } from './branch-id.service';

describe('BranchIdService', () => {
	let service: BranchIdService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(BranchIdService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
