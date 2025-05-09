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
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BranchRoutedUIService } from './branch-routed-ui.service';

describe('BranchRoutedUIService', () => {
	let service: BranchRoutedUIService;

	@Component({
		selector: 'osee-dummy',
		template: '<div>Dummy</div>',
		standalone: false,
	})
	class DummyComponent {}

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				RouterTestingModule.withRoutes([
					{ path: '', component: DummyComponent },
					{ path: ':branchType', component: DummyComponent },
					{
						path: ':branchType/:branchId',
						component: DummyComponent,
					},
				]),
			],
		});
		service = TestBed.inject(BranchRoutedUIService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
	describe('Core Functionality', () => {
		describe('Branch Type Navigation', () => {
			it('should utilize a split base url to form a url', () => {
				service.branchType = 'baseline';
				service.branchType = 'working';
				expect(service.type.getValue()).toEqual('working');
			});
		});

		describe('Id Navigation', () => {
			it('should utilize a split base url to form a url', () => {
				service.branchType = 'baseline';
				service.branchId = '0';
				service.branchId = '8';
				expect(service.id.getValue()).toEqual('8');
			});

			it('should not utilize a split base url to form a url', () => {
				service.branchId = '0';
				service.branchId = '8';
				expect(service.id.getValue()).toEqual('8');
			});
		});
	});
});
