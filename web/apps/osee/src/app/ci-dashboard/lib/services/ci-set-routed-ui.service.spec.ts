/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { CiSetRoutedUiService } from './ci-set-routed-ui.service';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';

describe('CiSetRoutedUiService', () => {
	let service: CiSetRoutedUiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: ActivatedRoute,
					useValue: { queryParamMap: new Subject() },
				},
			],
		});
		service = TestBed.inject(CiSetRoutedUiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
