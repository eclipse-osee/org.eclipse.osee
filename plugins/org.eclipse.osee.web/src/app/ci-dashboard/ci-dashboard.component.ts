/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UiService } from '@osee/shared/services';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ScriptTableComponent } from './lib/components/script-table/script-table.component';
import { ProgramDropdownComponent } from './lib/components/program-dropdown/program-dropdown.component';
import { ProgramReference } from './lib/types/tmo';

@Component({
	selector: 'osee-ci-dashboard',
	standalone: true,
	templateUrl: './ci-dashboard.component.html',
	imports: [
		CommonModule,
		AsyncPipe,
		NgFor,
		NgIf,
		RouterLink,
		ScriptTableComponent,
		ProgramDropdownComponent,
	],
})
export class CiDashboardComponent implements OnInit {
	constructor(
		private route: ActivatedRoute,
		private routerState: UiService
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.routerState.idValue = params.get('branchId') || '';
			this.routerState.typeValue =
				(params.get('branchType') as 'working' | 'baseline' | '') || '';
		});
	}

	@Input() program!: ProgramReference;
	@Input('master') masterName = '';
}

export default CiDashboardComponent;
