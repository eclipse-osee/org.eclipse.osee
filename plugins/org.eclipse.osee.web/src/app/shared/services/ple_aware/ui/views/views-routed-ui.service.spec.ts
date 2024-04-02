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

import { ViewsRoutedUiService } from './views-routed-ui.service';
import { ActivatedRoute } from '@angular/router';

describe('ViewsRoutedUiService', () => {
	let service: ViewsRoutedUiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [{ provide: ActivatedRoute, useValue: {} }],
		});
		service = TestBed.inject(ViewsRoutedUiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
