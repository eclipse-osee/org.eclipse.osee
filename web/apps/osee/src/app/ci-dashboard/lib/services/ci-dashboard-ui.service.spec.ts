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
import { CiDashboardUiService } from './ci-dashboard-ui.service';

describe('CiDashboardUiService', () => {
	let service: CiDashboardUiService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(CiDashboardUiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should only emit branch id and type when they change', () => {
		const ids: string[] = [];
		const types: string[] = [];
		const idSub = service.branchId.subscribe((id) => ids.push(id));
		const typeSub = service.branchType.subscribe((type) =>
			types.push(type)
		);

		service.BranchId = '10';
		service.BranchId = '10';
		service.BranchId = '20';
		service.BranchType = 'working';
		service.BranchType = 'working';
		service.BranchType = '';

		expect(ids).toEqual(['0', '10', '20']);
		expect(types).toEqual(['', 'working', '']);
		idSub.unsubscribe();
		typeSub.unsubscribe();
	});
});
