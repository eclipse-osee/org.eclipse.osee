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
import { ChartConfiguration, ChartDataset } from 'chart.js';
import 'chartjs-adapter-date-fns';
import enUS from 'date-fns/locale/en-US';

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
					maxTicksLimit: 12,
				},
			},
		},
		elements: {
			line: {
				tension: 0.3,
			},
			point: {
				radius: 3,
				hitRadius: 6,
				hoverRadius: 4,
			},
		},
	});

	lineChartData = computed<ChartConfiguration['data']>(() => {
		const days = this.timeline().days ?? [];

		const points = days
			.map((d) => {
				const x = this.toMs(d.executionDate as unknown);
				const pass = Number(d.scriptsPass);
				const fail = Number(d.scriptsFail);
				const abort = Number(d.abort);
				if (
					x === null ||
					!Number.isFinite(pass) ||
					!Number.isFinite(fail) ||
					!Number.isFinite(abort)
				) {
					return null;
				}
				return { x, pass, fail, abort };
			})
			.filter(
				(
					p
				): p is {
					x: number;
					pass: number;
					fail: number;
					abort: number;
				} => !!p
			)
			.sort((a, b) => a.x - b.x);

		const datasets: ChartDataset<'line', { x: number; y: number }[]>[] = [
			{
				label: this.stateLabels[0],
				data: points.map((day) => ({ x: day.x, y: day.pass })),
				backgroundColor: '#33A346',
				borderColor: '#33A346',
				pointBackgroundColor: '#33A346',
				fill: 'origin',
			},
			{
				label: this.stateLabels[1],
				data: points.map((day) => ({ x: day.x, y: day.fail })),
				backgroundColor: '#C34F37',
				borderColor: '#C34F37',
				pointBackgroundColor: '#C34F37',
				fill: 'origin',
			},
			{
				label: this.stateLabels[2],
				data: points.map((day) => ({ x: day.x, y: day.abort })),
				backgroundColor: '#FFC107',
				borderColor: '#FFC107',
				pointBackgroundColor: '#FFC107',
				fill: 'origin',
				hidden: !this.showAbort(),
			},
		];

		return { datasets };
	});

	private toMs(t: unknown): number | null {
		if (t == null) return null;

		if (t instanceof Date) {
			const ms = t.getTime();
			return Number.isFinite(ms) ? ms : null;
		}
		if (typeof t === 'number') {
			const ms = t < 1e11 ? t * 1000 : t;
			return Number.isFinite(ms) ? ms : null;
		}
		if (typeof t === 'string') {
			const asNum = Number(t);
			if (Number.isFinite(asNum)) {
				const ms = asNum < 1e11 ? asNum * 1000 : asNum;
				return Number.isFinite(ms) ? ms : null;
			}
			const ms = Date.parse(t);
			return Number.isFinite(ms) ? ms : null;
		}
		return null;
	}
}
