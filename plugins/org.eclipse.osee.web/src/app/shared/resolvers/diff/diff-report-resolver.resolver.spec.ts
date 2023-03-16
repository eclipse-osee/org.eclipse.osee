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
import { ResolveFn } from '@angular/router';
import {
	BranchInfoService,
	DifferenceReportService,
} from '@osee/shared/services';
import {
	BranchInfoServiceMock,
	DifferenceReportServiceMock,
} from '@osee/shared/testing';
import { changeInstance } from '@osee/shared/types/change-report';

import { diffReportResolverFn } from './diff-report-resolver.resolver';

describe('DiffReportResolverResolver', () => {
	const resolver: ResolveFn<changeInstance[] | undefined> = (route, state) =>
		TestBed.runInInjectionContext(() => diffReportResolverFn(route, state));

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: DifferenceReportService,
					useValue: DifferenceReportServiceMock,
				},
			],
		});
		//resolver = TestBed.inject(DiffReportResolver);
	});

	it('should be created', () => {
		expect(resolver).toBeTruthy();
	});
});
