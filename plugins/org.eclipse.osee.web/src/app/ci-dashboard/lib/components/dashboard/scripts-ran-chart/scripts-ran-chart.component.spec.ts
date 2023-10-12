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
import { ScriptsRanChartComponent } from './scripts-ran-chart.component';

describe('ScriptsRanChartComponent', () => {
	let component: ScriptsRanChartComponent;
	let fixture: ComponentFixture<ScriptsRanChartComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ScriptsRanChartComponent],
		});
		fixture = TestBed.createComponent(ScriptsRanChartComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
