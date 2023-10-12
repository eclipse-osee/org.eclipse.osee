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
import { TeamStats, teamStatsSentinel } from '../../../types/team-stats';

@Component({
	selector: 'osee-scripts-ran-chart',
	standalone: true,
	imports: [PieChartComponent],
	templateUrl: './scripts-ran-chart.component.html',
})
export class ScriptsRanChartComponent implements OnInit {
	@Input() stats: TeamStats = teamStatsSentinel;

	labels = ['Ran', 'Not'];
	colors = ['green', 'red'];
	data: number[] = [];
	title = '';

	ngOnInit(): void {
		const totalScripts = this.stats.scriptsRan + this.stats.scriptsNotRan;
		const passPercent =
			totalScripts === 0
				? 0
				: Math.floor((this.stats.scriptsRan / totalScripts) * 100);
		this.title = this.stats.teamName + ' - ' + passPercent + '%';
		this.data = [this.stats.scriptsRan, this.stats.scriptsNotRan];
	}
}
