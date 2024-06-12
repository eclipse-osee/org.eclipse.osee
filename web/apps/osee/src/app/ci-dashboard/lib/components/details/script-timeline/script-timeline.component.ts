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
	effect,
	inject,
	viewChild,
} from '@angular/core';
import { MatFormField } from '@angular/material/form-field';
import { NgChartsModule } from 'ng2-charts';
import { CiDetailsService } from '../../../services/ci-details.service';
import { ChartConfiguration } from 'chart.js';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { of, switchMap, tap } from 'rxjs';
import 'chartjs-adapter-date-fns';
import { TimelineResultsChartComponent } from './timeline-results-chart/timeline-results-chart.component';

@Component({
	selector: 'osee-script-timeline',
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		NgChartsModule,
		MatFormField,
		TimelineResultsChartComponent,
	],
	template: `<div>
		<ng-container *ngIf="scriptResults | async as _results"
			><osee-timeline-results-chart [timelineData]="_results"
		/></ng-container>
	</div>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScriptTimelineComponent {
	ciDetailsService = inject(CiDetailsService);

	scriptResults = this.ciDetailsService.scriptResults.pipe(
		switchMap((results) => of(results.reverse()))
	);

	title = 'History';
}
