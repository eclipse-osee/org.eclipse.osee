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
import { MatFormFieldModule } from '@angular/material/form-field';
import { ChartConfiguration, ChartData } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';

@Component({
	selector: 'osee-pie-chart',
	standalone: true,
	imports: [NgChartsModule, MatFormFieldModule],
	templateUrl: './pie-chart.component.html',
})
export class PieChartComponent implements OnInit {
	@Input() title: string = '';
	@Input() labels: string[] = [];
	@Input() data: number[] = [];
	@Input() colors: string[] = [];

	pieChartData: ChartData<'pie', number[]> = {
		datasets: [],
	};

	pieChartOptions: ChartConfiguration['options'] = {
		responsive: true,
		plugins: {
			legend: {
				display: false,
			},
		},
	};

	ngOnInit(): void {
		this.pieChartData = {
			labels: this.labels,
			datasets: [
				{
					data: this.data,
					backgroundColor: this.colors,
				},
			],
		};
	}
}
