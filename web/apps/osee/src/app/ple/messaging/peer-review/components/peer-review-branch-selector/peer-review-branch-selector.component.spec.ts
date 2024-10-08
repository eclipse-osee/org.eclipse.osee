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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PeerReviewBranchSelectorComponent } from './peer-review-branch-selector.component';
import { PeerReviewUiService } from '../../services/peer-review-ui.service';
import { PeerReviewUiServiceMock } from '../../testing/peer-review-ui.service.mock';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('PeerReviewBranchSelectorComponent', () => {
	let component: PeerReviewBranchSelectorComponent;
	let fixture: ComponentFixture<PeerReviewBranchSelectorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PeerReviewBranchSelectorComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: PeerReviewUiService,
					useValue: PeerReviewUiServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(PeerReviewBranchSelectorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
