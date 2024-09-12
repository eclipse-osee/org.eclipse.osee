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
import { CreatePeerReviewButtonComponent } from './create-peer-review-button.component';
import { createActionServiceMock } from '@osee/configuration-management/testing';
import { CreateActionService } from '@osee/configuration-management/services';
import { PeerReviewUiService } from '../../services/peer-review-ui.service';
import { PeerReviewUiServiceMock } from '../../testing/peer-review-ui.service.mock';

describe('CreatePeerReviewButtonComponent', () => {
	let component: CreatePeerReviewButtonComponent;
	let fixture: ComponentFixture<CreatePeerReviewButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreatePeerReviewButtonComponent],
			providers: [
				{
					provide: CreateActionService,
					useValue: createActionServiceMock,
				},
				{
					provide: PeerReviewUiService,
					useValue: PeerReviewUiServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreatePeerReviewButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
