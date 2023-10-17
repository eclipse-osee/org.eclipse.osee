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
import { Component, Input, OnInit } from '@angular/core';
import { PieChartComponent } from '../../pie-chart/pie-chart.component';
import { CIStats, teamStatsSentinel } from '../../../types/ci-stats';

@Component({
	selector: 'osee-test-points-pass-fail-chart',
	standalone: true,
	imports: [PieChartComponent],
	templateUrl: './test-points-pass-fail-chart.component.html',
})
export class TestPointsPassFailChartComponent implements OnInit {
	@Input() stats: CIStats = teamStatsSentinel;

	labels = ['Pass', 'Fail'];
	colors = ['green', 'red'];
	data: number[] = [];
	title = '';

	ngOnInit(): void {
		const totalPoints =
			this.stats.testPointsPass + this.stats.testPointsFail;
		const passPercent =
			totalPoints === 0
				? 0
				: Math.floor((this.stats.testPointsPass / totalPoints) * 100);
		this.title = this.stats.name + ' - ' + passPercent + '%';
		this.data = [this.stats.testPointsPass, this.stats.testPointsFail];
	}
}
