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
import { PeerReviewUiService } from './peer-review-ui.service';
import { BranchInfoService } from '@osee/shared/services';
import { BranchInfoServiceMock } from '@osee/shared/testing';
import { PeerReviewService } from './peer-review.service';
import { PeerReviewServiceMock } from '../testing/peer-review.service.mock';

describe('PeerReviewUiService', () => {
	let service: PeerReviewUiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{ provide: PeerReviewService, useValue: PeerReviewServiceMock },
			],
		});
		service = TestBed.inject(PeerReviewUiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
