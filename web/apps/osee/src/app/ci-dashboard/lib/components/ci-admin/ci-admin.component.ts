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

@Component({
	selector: 'osee-ci-admin',
	standalone: true,
	imports: [
		CiDashboardControlsComponent,
		SubsystemsListComponent,
		TeamsListComponent,
	],
	template: `
		<osee-ci-dashboard-controls
			[actionButton]="true"
			[ciSetSelector]="false" />
		<osee-subsystems-list />
		<osee-teams-list />
		<div class="tw-p-4">Subsystem and Team tables are coming soon!</div>
	`,
})
export default class CiAdminComponent {}
