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
import {
	ApplicabilityListUIService,
	CurrentBranchInfoService,
	ViewsRoutedUiService,
} from '@osee/shared/services';
import {
	applicabilityListUIServiceMock,
	testBranchInfo,
	viewsRoutedUiServiceMock,
} from '@osee/shared/testing';

import { CurrentViewSelectorComponent } from './current-view-selector.component';
import { of } from 'rxjs';

describe('CurrentViewSelectorComponent', () => {
	let component: CurrentViewSelectorComponent;
	let fixture: ComponentFixture<CurrentViewSelectorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CurrentViewSelectorComponent, NoopAnimationsModule],
			providers: [
				{
					provide: ApplicabilityListUIService,
					useValue: applicabilityListUIServiceMock,
				},
				{
					provide: ViewsRoutedUiService,
					useValue: viewsRoutedUiServiceMock,
				},
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CurrentViewSelectorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
