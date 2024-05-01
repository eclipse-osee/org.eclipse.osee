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

import { ConnectionsUiService } from './connections-ui.service';

describe('ConnectionsUiService', () => {
	let service: ConnectionsUiService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(ConnectionsUiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
