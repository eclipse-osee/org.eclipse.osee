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

import { NodeReviewComponent } from './node-review.component';
import { connectionMock, nodesMock } from '@osee/messaging/shared/testing';

describe('NodeReviewComponent', () => {
	let component: NodeReviewComponent;
	let fixture: ComponentFixture<NodeReviewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NodeReviewComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(NodeReviewComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('connection', connectionMock);
		fixture.componentRef.setInput('node', nodesMock[0]);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
