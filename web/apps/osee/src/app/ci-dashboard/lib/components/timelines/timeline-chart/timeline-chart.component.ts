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
	ChangeDetectionStrategy,
	Component,
	computed,
	input,
	signal,
} from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NgChartsModule } from 'ng2-charts';
import { Timeline } from '../../../types/ci-stats';
import { ChartConfiguration } from 'chart.js';
import 'chartjs-adapter-date-fns';
import enUS from 'date-fns/locale/en-US';
import { format } from 'date-fns';

@Component({
	selector: 'osee-timeline-chart',
	template: `<div class="tw-text-center">
			<mat-label class="tw-text-lg tw-font-bold">{{ title() }}</mat-label>
		</div>
		<div>
			<canvas
				baseChart
				[data]="lineChartData()"
				[type]="'line'"
				[options]="lineChartOptions()"></canvas>
		</div>`,
	imports: [NgChartsModule, MatFormFieldModule],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimelineChartComponent {
	timeline = input.required<Timeline>();

	labels = ['Pass', 'Fail', 'Abort', "Dispo'd"];
	title = computed(() => this.timeline().team);

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

	lineChartData = computed(() => {
		const data: ChartConfiguration['data'] = {
			labels: [],
			datasets: [],
		};
		const days = this.timeline().days;
		data.datasets.push({
			data: days.map((day) => day.scriptsPass),
			backgroundColor: '#33A346',
			borderColor: '#33A346',
			pointBackgroundColor: '#33A346',
			fill: 'origin',
		});
		data.datasets.push({
			data: days.map((day) => day.scriptsFail),
			backgroundColor: '#C34F37',
			borderColor: '#C34F37',
			pointBackgroundColor: '#C34F37',
			fill: 'origin',
		});
		data.labels = days.map((day) =>
			format(new Date(day.executionDate), "yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
		);
		return data;
	});
}
