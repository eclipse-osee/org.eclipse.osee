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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { ScriptTableComponent } from './script-table/script-table.component';
import { BranchPickerComponent } from '../../../../shared/components/branch-picker/branch-picker/branch-picker.component';
import { CiDashboardControlsComponent } from '../../../lib/components/ci-dashboard-controls/ci-dashboard-controls.component';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-all-scripts',
	standalone: true,
	template: `<osee-ci-dashboard-controls />
		<div class="tw-h-[76vh] tw-px-4"><osee-script-table /></div>`,
	imports: [
		AsyncPipe,
		RouterLink,
		ScriptTableComponent,
		BranchPickerComponent,
		CiDashboardControlsComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AllScriptsComponent {
	uiService = inject(CiDashboardUiService);

	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
}

export default AllScriptsComponent;
