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
import { ManageActionButtonComponent } from './manage-action-button.component';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';

describe('ManageActionButtonComponent', () => {
	let component: ManageActionButtonComponent;
	let fixture: ComponentFixture<ManageActionButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ManageActionButtonComponent],
			providers: [
				{ provide: ActionService, useValue: actionServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ManageActionButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
