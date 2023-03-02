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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ApplicabilityListUIService } from '@osee/messaging/shared/services';
import { applicabilityListUIServiceMock } from '@osee/messaging/shared/testing';

import { ViewSelectorComponent } from './view-selector.component';

describe('ViewSelectorComponent', () => {
	let component: ViewSelectorComponent;
	let fixture: ComponentFixture<ViewSelectorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ViewSelectorComponent, NoopAnimationsModule],
			providers: [
				{
					provide: ApplicabilityListUIService,
					useValue: applicabilityListUIServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ViewSelectorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
