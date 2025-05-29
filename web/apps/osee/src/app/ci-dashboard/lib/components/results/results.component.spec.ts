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

import ResultsComponent from './results.component';
import { CiDashboardControlsMockComponent } from '../../testing/ci-dashboard-controls.component.mock';
import { CiDetailsListService } from '../../services/ci-details-list.service';
import { ciDetailsServiceMock } from '../../testing/ci-details.service.mock';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ScriptListComponent } from './script-list/script-list.component';
import { ResultListComponent } from './result-list/result-list.component';
import { AsyncPipe, NgClass } from '@angular/common';
import { ScriptTimelineComponent } from './script-timeline/script-timeline.component';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';

describe('ResultsComponent', () => {
	let component: ResultsComponent;
	let fixture: ComponentFixture<ResultsComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ResultsComponent, {
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
			imports: [ResultsComponent],
			providers: [
				{
					provide: CiDetailsListService,
					useValue: ciDetailsServiceMock,
				},
				{
					provide: ActivatedRoute,
					useValue: { queryParamMap: new Subject() },
				},
				provideNoopAnimations(),
			],
		});
		fixture = TestBed.createComponent(ResultsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
