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
import { PieChartComponent } from '../../../components/pie-chart/pie-chart.component';
import { CIStats, teamStatsSentinel } from '../../../types/ci-stats';

@Component({
	selector: 'osee-scripts-pass-fail-chart',
	imports: [PieChartComponent],
	templateUrl: './scripts-pass-fail-chart.component.html',
})
export class ScriptsPassFailChartComponent implements OnInit {
	@Input() stats: CIStats = teamStatsSentinel;

	labels = ['Pass', 'Fail', 'Abort', "Dispo'd"];
	colors = ['green', 'red', 'orange', 'yellow'];
	data: number[] = [];
	title = '';

	ngOnInit(): void {
		const totalScripts =
			this.stats.scriptsPass +
			this.stats.scriptsFail +
			this.stats.scriptsAbort +
			this.stats.scriptsDispo;
		const passPercent =
			totalScripts === 0
				? 0
				: Math.floor((this.stats.scriptsPass / totalScripts) * 100);
		this.title = this.stats.name + ' - ' + passPercent + '%';
		this.data = [
			this.stats.scriptsPass,
			this.stats.scriptsFail,
			this.stats.scriptsAbort,
			this.stats.scriptsDispo,
		];
	}
}
