/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { CurrentNodeService } from './current-node.service';
import { NodeService } from '@osee/messaging/shared/services';
import { nodeServiceMock } from '@osee/messaging/shared/testing';

describe('CurrentNodeService', () => {
	let service: CurrentNodeService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [{ provide: NodeService, useValue: nodeServiceMock }],
		});
		service = TestBed.inject(CurrentNodeService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
