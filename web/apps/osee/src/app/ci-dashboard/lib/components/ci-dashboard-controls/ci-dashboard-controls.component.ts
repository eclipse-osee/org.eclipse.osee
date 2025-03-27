/*********************************************************************
 * Copyright (c) 2022 Boeing
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
	effect,
	inject,
	input,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { BranchPickerComponent } from '@osee/shared/components';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { SetDropdownComponent } from './set-dropdown/set-dropdown.component';
import { CurrentActionDropDownComponent } from '@osee/configuration-management/components';

@Component({
	selector: 'osee-ci-dashboard-controls',
	imports: [
		BranchPickerComponent,
		SetDropdownComponent,
		CurrentActionDropDownComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `<div
		class="tw-flex tw-w-full tw-items-center tw-justify-between tw-p-4">
		<div class="tw-flex tw-w-full tw-flex-row tw-items-end tw-gap-4">
			@if (branchPicker()) {
				<osee-branch-picker
					class="tw-min-w-[350px] tw-max-w-lg"></osee-branch-picker>
			}

			@if (branchIdValid() && branchType()) {
				<div>
					@if (ciSetSelector()) {
						<osee-set-dropdown />
					}
				</div>
			}

			<!-- Any content can be inserted between the CI Set selector and the action controls -->
			<ng-content></ng-content>
		</div>
		@if (actionButton() && branchIdValid() && branchType()) {
			<div class="tw-min-w-[210px]">
				<osee-current-action-drop-down />
			</div>
		}
	</div>`,
})
export class CiDashboardControlsComponent {
	branchPicker = input(true);
	ciSetSelector = input(true);
	actionButton = input(false);

	private route = inject(ActivatedRoute);
	private uiService = inject(CiDashboardUiService);

	private _paramMap = toSignal(this.route.paramMap);
	private _paramEffect = effect(() => {
		const params = this._paramMap();
		if (!params) {
			return;
		}
		this.uiService.BranchId = params.get('branchId') || '';
		this.uiService.BranchType =
			(params.get('branchType') as 'working' | 'baseline' | '') || '';
	});

	branchType = toSignal(this.uiService.branchType);
	private _branchId = toSignal(this.uiService.branchId);

	branchIdValid = computed(
		() =>
			this._branchId() !== '' &&
			this._branchId() !== '-1' &&
			this._branchId() !== '0'
	);
}
