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

import { ViewSelectorComponent } from './view-selector.component';
import { ApplicabilityListUIService } from '@osee/shared/services';
import { applicabilityListUIServiceMock } from '@osee/shared/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('ViewSelectorComponent', () => {
	let component: ViewSelectorComponent;
	let fixture: ComponentFixture<ViewSelectorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ViewSelectorComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: ApplicabilityListUIService,
					useValue: applicabilityListUIServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ViewSelectorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('view', {
			id: '1',
			name: 'Base',
		});
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
