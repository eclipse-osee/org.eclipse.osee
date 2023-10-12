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
import { ScriptsPassFailChartComponent } from './scripts-pass-fail-chart.component';

describe('ScriptsPassFailChartComponent', () => {
	let component: ScriptsPassFailChartComponent;
	let fixture: ComponentFixture<ScriptsPassFailChartComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ScriptsPassFailChartComponent],
		});
		fixture = TestBed.createComponent(ScriptsPassFailChartComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
