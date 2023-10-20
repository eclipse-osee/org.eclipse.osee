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
import { ScriptTableComponent } from './script-table/script-table.component';
import { SetReference, setReferenceSentinel } from '../../../lib/types/tmo';
import { BranchPickerComponent } from '../../../../shared/components/branch-picker/branch-picker/branch-picker.component';
import { CiDashboardControlsComponent } from '../../../lib/components/ci-dashboard-controls/ci-dashboard-controls.component';
import { TmoService } from '../../../lib/services/tmo.service';

@Component({
	selector: 'osee-all-scripts',
	standalone: true,
	templateUrl: './all-scripts.component.html',
	imports: [
		CommonModule,
		AsyncPipe,
		NgFor,
		NgIf,
		RouterLink,
		ScriptTableComponent,
		BranchPickerComponent,
		CiDashboardControlsComponent,
	],
})
export class AllScriptsComponent implements OnInit {
	selectedSet = this.tmoService.setId.value;

	constructor(
		private route: ActivatedRoute,
		private routerState: UiService,
		private tmoService: TmoService
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.routerState.idValue = params.get('branchId') || '';
			this.routerState.typeValue =
				(params.get('branchType') as 'working' | 'baseline' | '') || '';
		});
	}

	@Input() set: SetReference = setReferenceSentinel;
	@Input('master') masterName = '';
}

export default AllScriptsComponent;
