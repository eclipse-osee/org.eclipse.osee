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
import { BranchInfoService } from 'src/app/ple-services/http/branch-info.service';
import { BranchInfoServiceMock } from 'src/app/ple-services/http/branch-info.service.mock';
import { DifferenceReportService } from 'src/app/ple-services/http/difference-report.service';
import { DifferenceReportServiceMock } from 'src/app/ple-services/http/difference-report.service.mock';
import { changeInstance } from '../types/change-report';

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
