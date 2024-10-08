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

import { RouterStateService } from './router-state.service';

describe('RouterStateService', () => {
	let service: RouterStateService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(RouterStateService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
	describe('Core Functionality', () => {
		describe('Branch Type', () => {
			describe('Valid States', () => {
				it('should set type to baseline', () => {
					service.type = 'baseline';
					expect(service.type).toEqual('baseline');
				});

				it('should set type to working', () => {
					service.type = 'working';
					expect(service.type).toEqual('working');
				});
			});
		});
		describe('Branch Id', () => {
			describe('Valid States', () => {
				it('should set id to 8', () => {
					service.id = '8';
					expect(service.id).toEqual('8');
				});
			});
		});
	});
});
