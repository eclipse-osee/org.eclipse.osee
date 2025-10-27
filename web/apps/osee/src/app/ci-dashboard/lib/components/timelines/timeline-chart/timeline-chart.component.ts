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
	showAbort = input<boolean>(true);

	stateLabels = ['Pass', 'Fail', 'Abort', "Dispo'd"];
	title = computed(() => this.timeline().team);

	labels = computed(() => {
		const days = this.timeline().days;
		return days.map((day) =>
			format(new Date(day.executionDate), "yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
		);
	});

	lineChartOptions = signal<ChartConfiguration['options']>({
		responsive: true,
		maintainAspectRatio: false,
		plugins: {
			legend: {
				display: false,
			},
			tooltip: {
				enabled: true,
				mode: 'index',
				intersect: false,
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
		elements: {
			line: {
				tension: 0.3,
			},
		},
	});

	lineChartData = computed(() => {
		const days = this.timeline().days;
		const data: ChartConfiguration['data'] = {
			labels: this.labels(),
			datasets: [],
		};

		data.datasets.push({
			label: this.stateLabels[0], // 'Pass'
			data: days.map((day) => day.scriptsPass),
			backgroundColor: '#33A346',
			borderColor: '#33A346',
			pointBackgroundColor: '#33A346',
			fill: 'origin',
		});

		data.datasets.push({
			label: this.stateLabels[1], // 'Fail'
			data: days.map((day) => day.scriptsFail),
			backgroundColor: '#C34F37',
			borderColor: '#C34F37',
			pointBackgroundColor: '#C34F37',
			fill: 'origin',
		});

		data.datasets.push({
			label: this.stateLabels[2], // 'Abort'
			data: days.map((day) => day.abort),
			backgroundColor: '#FFC107',
			borderColor: '#FFC107',
			pointBackgroundColor: '#FFC107',
			fill: 'origin',
			hidden: !this.showAbort(),
		});
		return data;
	});
}
