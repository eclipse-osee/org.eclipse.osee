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

import DetailsComponent from './details.component';
import { CiDashboardControlsMockComponent } from '../../testing/ci-dashboard-controls.component.mock';
import { CiDetailsService } from '../../services/ci-details.service';
import { ciDetailsServiceMock } from '../../testing/ci-details.service.mock';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ScriptListComponent } from './script-list/script-list.component';
import { ResultListComponent } from './result-list/result-list.component';
import { AsyncPipe, NgClass } from '@angular/common';
import { ScriptTimelineComponent } from './script-timeline/script-timeline.component';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';

describe('DetailsComponent', () => {
	let component: DetailsComponent;
	let fixture: ComponentFixture<DetailsComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(DetailsComponent, {
			set: {
				imports: [
					CiDashboardControlsMockComponent,
					ScriptListComponent,
					ResultListComponent,
					ScriptTimelineComponent,
					AsyncPipe,
					NgClass,
					MatFormField,
					MatLabel,
					MatIcon,
					MatInput,
				],
			},
		}).configureTestingModule({
			imports: [DetailsComponent],
			providers: [
				{ provide: CiDetailsService, useValue: ciDetailsServiceMock },
				provideNoopAnimations(),
			],
		});
		fixture = TestBed.createComponent(DetailsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
