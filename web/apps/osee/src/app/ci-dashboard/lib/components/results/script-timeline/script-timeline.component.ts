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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { NgChartsModule } from 'ng2-charts';
import { CiDetailsListService } from '../../../services/ci-details-list.service';
import { AsyncPipe, NgIf } from '@angular/common';
import { of, switchMap } from 'rxjs';
import 'chartjs-adapter-date-fns';
import { TimelineResultsChartComponent } from './timeline-results-chart/timeline-results-chart.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-script-timeline',
	imports: [NgIf, AsyncPipe, NgChartsModule, TimelineResultsChartComponent],
	template: `<div>
		<ng-container *ngIf="scriptResultsBySet | async as _results"
			><osee-timeline-results-chart [timelineData]="_results"
		/></ng-container>
	</div>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScriptTimelineComponent {
	ciDetailsService = inject(CiDetailsListService);

	scriptResultsBySet = this.ciDetailsService.scriptResultsBySet.pipe(
		takeUntilDestroyed(),
		switchMap((results) => of(results.reverse()))
	);

	title = 'History';
}
