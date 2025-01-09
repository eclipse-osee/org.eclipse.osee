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
import { ScriptTimelineComponent } from './script-timeline.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CiDetailsService } from '../../../services/ci-details.service';
import { ciDetailsServiceMock } from '../../../testing/ci-details.service.mock';
import { AsyncPipe, NgIf } from '@angular/common';
import { TimelineResultsChartComponent } from './timeline-results-chart/timeline-results-chart.component';

describe('ScriptTimelineComponent', () => {
	let component: ScriptTimelineComponent;
	let fixture: ComponentFixture<ScriptTimelineComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				AsyncPipe,
				NgIf,
				ScriptTimelineComponent,
				TimelineResultsChartComponent,
			],
			providers: [
				{ provide: CiDetailsService, useValue: ciDetailsServiceMock },
				provideNoopAnimations(),
			],
		});
		fixture = TestBed.createComponent(ScriptTimelineComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
