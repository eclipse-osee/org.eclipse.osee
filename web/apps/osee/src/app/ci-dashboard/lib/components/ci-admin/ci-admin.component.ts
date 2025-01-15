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
import { Component, computed, inject } from '@angular/core';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { SubsystemsListComponent } from './subsystems-list/subsystems-list.component';
import { TeamsListComponent } from './teams-list/teams-list.component';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { CiSetsTableComponent } from './ci-sets-table/ci-sets-table.component';
import { CiAdminConfigComponent } from './ci-admin-config/ci-admin-config.component';
import { UiService } from '@osee/shared/services';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-ci-admin',
	imports: [
		CiDashboardControlsComponent,
		ExpansionPanelComponent,
		SubsystemsListComponent,
		TeamsListComponent,
		CiSetsTableComponent,
		CiAdminConfigComponent,
	],
	template: `
		<osee-ci-dashboard-controls
			[actionButton]="true"
			[ciSetSelector]="false" />
		@if (branchIdValid()) {
			<div class="tw-flex tw-flex-col tw-gap-4">
				<osee-ci-admin-config />
				<h5 class="tw-pl-4 tw-pt-4">Configuration Artifacts</h5>
				<osee-expansion-panel title="CI Sets">
					<osee-ci-sets-table />
				</osee-expansion-panel>
				<osee-expansion-panel title="Subsystems">
					<osee-subsystems-list />
				</osee-expansion-panel>
				<osee-expansion-panel title="Teams">
					<osee-teams-list />
				</osee-expansion-panel>
			</div>
		}
	`,
})
export default class CiAdminComponent {
	private readonly uiService = inject(UiService);

	branchId = toSignal(this.uiService.id, { initialValue: '-1' });
	branchIdValid = computed(() => {
		const branchId = this.branchId();
		return branchId !== '' && branchId !== '-1' && branchId !== '0';
	});
}
