/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { CommitManagerButtonComponent } from './commit-manager-button.component';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';
import { teamWorkflowDetailsMock } from '@osee/shared/testing';

describe('CommitManagerButtonComponent', () => {
	let component: CommitManagerButtonComponent;
	let fixture: ComponentFixture<CommitManagerButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CommitManagerButtonComponent],
			providers: [
				{ provide: ActionService, useValue: actionServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CommitManagerButtonComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput(
			'teamWorkflowId',
			teamWorkflowDetailsMock.id
		);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
