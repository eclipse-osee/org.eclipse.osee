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

import { LatestActionDropDownComponent } from './latest-action-drop-down.component';
import { ActionService } from '@osee/shared/services';
import { actionServiceMock } from '@osee/shared/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('LatestActionDropDownComponent', () => {
	let component: LatestActionDropDownComponent;
	let fixture: ComponentFixture<LatestActionDropDownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(LatestActionDropDownComponent, {
			set: {
				providers: [
					{ provide: ActionService, useValue: actionServiceMock },
				],
			},
		})
			.configureTestingModule({
				imports: [LatestActionDropDownComponent],
				providers: [provideNoopAnimations()],
			})
			.compileComponents();

		fixture = TestBed.createComponent(LatestActionDropDownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
