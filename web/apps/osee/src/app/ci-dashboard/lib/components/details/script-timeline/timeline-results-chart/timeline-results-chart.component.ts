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
import {
	Component,
	computed,
	effect,
	input,
	signal,
	viewChild,
} from '@angular/core';
import { ChartConfiguration } from 'chart.js';
import { enUS } from 'date-fns/locale';
import { add, parseISO } from 'date-fns';
import { BaseChartDirective, NgChartsModule } from 'ng2-charts';
import { format } from 'date-fns';
import { ResultReference } from 'src/app/ci-dashboard/lib/types';

@Component({
	selector: 'osee-timeline-results-chart',
	standalone: true,
	template: `<canvas
		baseChart
		[data]="chartConfig()"
		[type]="'line'"
		[options]="lineChartOptions()">
	</canvas>`,
	imports: [NgChartsModule],
})
export class TimelineResultsChartComponent {
	timelineData = input.required<ResultReference[]>();

	lineChartOptions = signal<ChartConfiguration['options']>({
		responsive: true,
		maintainAspectRatio: false,
		plugins: {
			legend: {
				display: false,
			},
		},
		scales: {
			y: {
				beginAtZero: true,
				stacked: true,
				ticks: {
					precision: 0,
				},
			},
			x: {
				type: 'time',
				adapters: {
					date: {
						locale: enUS,
					},
				},
				time: {
					unit: 'day',
				},
				ticks: {
					source: 'auto',
				},
			},
		},
	});

	chart = viewChild.required(BaseChartDirective);

	chartConfig = computed(() => this.calcData(this.timelineData()));

	_update = effect(() => {
		this.chartConfig();
		this.chart().update();
	});

	calcData(results: ResultReference[]): ChartConfiguration['data'] {
		return {
			labels: results.map((result) =>
				format(
					new Date(result.executionDate),
					"yyyy-MM-dd'T'HH:mm:ss.SSSxxx"
				)
			),
			datasets: [
				{
					data: results.map((result) => result.passedCount),
					backgroundColor: '#33A346',
					borderColor: '#33A346',
					pointBackgroundColor: '#33A346',
					fill: 'origin',
				},
				{
					data: results.map((result) => result.failedCount),
					backgroundColor: '#C34F37',
					borderColor: '#C34F37',
					pointBackgroundColor: '#C34F37',
					fill: 'origin',
				},
			],
		};
	}
}
