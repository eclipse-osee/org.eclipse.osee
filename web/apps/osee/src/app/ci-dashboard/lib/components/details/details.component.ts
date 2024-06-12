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
import { CommonModule } from '@angular/common';
import { ScriptListComponent } from './script-list/script-list.component';
import { ResultListComponent } from './result-list/result-list.component';
import { TestPointTableComponent } from './test-point-table/test-point-table.component';
import { RouterLink } from '@angular/router';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { ScriptTimelineComponent } from './script-timeline/script-timeline.component';
import { RunInfoComponent } from './run-info/run-info.component';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-scripts',
	standalone: true,
	template: `<osee-ci-dashboard-controls />
		<div class="tw-flex tw-flex-col tw-gap-y-24 tw-px-4">
			<div class="tw-flex tw-max-h-96 tw-gap-x-6">
				<osee-script-list /><osee-result-list />
				<div class="tw-overflow-x-auto"><osee-test-point-table /></div>
			</div>
			<div class="tw-flex tw-gap-x-6">
				<div class="tw-h-1/2 tw-w-3/4"><osee-script-timeline /></div>
				<div><osee-run-info /></div>
			</div>
		</div>`,
	imports: [
		CommonModule,
		ScriptListComponent,
		ResultListComponent,
		TestPointTableComponent,
		ScriptTimelineComponent,
		RunInfoComponent,
		RouterLink,
		CiDashboardControlsComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DetailsComponent {
	uiService = inject(CiDashboardUiService);

	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
}
