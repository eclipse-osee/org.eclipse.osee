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
import { CiBatchService } from './ci-batch.service';
import { TmoHttpService } from './tmo-http.service';
import { tmoHttpServiceMock } from './tmo-http.service.mock';

describe('CiBatchService', () => {
	let service: CiBatchService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: TmoHttpService, useValue: tmoHttpServiceMock },
			],
		});
		service = TestBed.inject(CiBatchService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
