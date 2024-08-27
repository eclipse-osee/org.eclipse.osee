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
import { Component } from '@angular/core';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { SubsystemsListComponent } from './subsystems-list/subsystems-list.component';
import { TeamsListComponent } from './teams-list/teams-list.component';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { CiSetsTableComponent } from './ci-sets-table/ci-sets-table.component';

@Component({
	selector: 'osee-ci-admin',
	standalone: true,
	imports: [
		CiDashboardControlsComponent,
		ExpansionPanelComponent,
		SubsystemsListComponent,
		TeamsListComponent,
		CiSetsTableComponent,
	],
	template: `
		<osee-ci-dashboard-controls
			[actionButton]="true"
			[ciSetSelector]="false" />
		<div class="tw-flex tw-flex-col tw-gap-4">
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
	`,
})
export default class CiAdminComponent {}
