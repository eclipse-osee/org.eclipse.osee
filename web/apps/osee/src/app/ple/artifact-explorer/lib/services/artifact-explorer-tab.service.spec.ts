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

import { ArtifactExplorerTabService } from './artifact-explorer-tab.service';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { of } from 'rxjs';
import { testBranchInfo } from '@osee/shared/testing';

describe('ArtifactExplorerTabService', () => {
	let service: ArtifactExplorerTabService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
			],
		});
		service = TestBed.inject(ArtifactExplorerTabService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
