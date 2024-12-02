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
	OnInit,
	input,
	signal,
} from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NgChartsModule } from 'ng2-charts';
import { CITimelineStats } from '../../../types/ci-stats';
import { ChartConfiguration } from 'chart.js';
import 'chartjs-adapter-date-fns';
import enUS from 'date-fns/locale/en-US';
import { format } from 'date-fns';

@Component({
	selector: 'osee-timeline-chart',
	template: `<div class="tw-text-center">
			<mat-label class="tw-text-lg tw-font-bold">{{ title }}</mat-label>
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
export class TimelineChartComponent implements OnInit {
	timelineData = input.required<CITimelineStats>();

	labels = ['Pass', 'Fail', 'Abort', "Dispo'd"];
	data: number[] = [];
	title = '';

	total: number[] = [];
	passing: number[] = [];

	lineChartData = signal<ChartConfiguration['data']>({
		labels: [],
		datasets: [],
	});

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

	ngOnInit(): void {
		const results = this.timelineData().ciStats;
		this.lineChartData().datasets = [];
		this.lineChartData().labels = [];

		this.title = this.timelineData().name;

		this.lineChartData().datasets.push({
			data: results.map(
				(result) => result.scriptsPass + result.scriptsDispo
			),
			backgroundColor: '#33A346',
			borderColor: '#33A346',
			pointBackgroundColor: '#33A346',
			fill: 'origin',
		});
		this.lineChartData().datasets.push({
			data: results.map((result) => result.scriptsFail),
			backgroundColor: '#C34F37',
			borderColor: '#C34F37',
			pointBackgroundColor: '#C34F37',
			fill: 'origin',
		});

		this.lineChartData().labels = results.map((result) =>
			format(
				new Date(result.scriptsExecutionDate),
				"yyyy-MM-dd'T'HH:mm:ss.SSSxxx"
			)
		);
	}
}
