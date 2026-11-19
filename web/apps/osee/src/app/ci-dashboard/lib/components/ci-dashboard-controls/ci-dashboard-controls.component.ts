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
import { CiSetRoutedUiService } from '../../services/ci-set-routed-ui.service';
import { SetDropdownComponent } from './set-dropdown/set-dropdown.component';
import { OverflowMenuComponent } from './overflow-menu/overflow-menu.component';
import { CurrentActionDropDownComponent } from '@osee/configuration-management/components';
@Component({
	selector: 'osee-ci-dashboard-controls',
	imports: [
		BranchPickerComponent,
		SetDropdownComponent,
		CurrentActionDropDownComponent,
		OverflowMenuComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `<section
		aria-label="Zenith dashboard controls"
		class="tw-flex tw-w-full tw-flex-row tw-flex-wrap tw-items-stretch tw-gap-x-10 tw-gap-y-4 tw-p-4">
		@if (branchPicker()) {
			<div
				role="group"
				aria-label="Branch"
				aria-describedby="zenith-branch-description"
				class="tw-flex tw-w-full tw-min-w-0 tw-flex-col tw-gap-2 sm:tw-w-auto sm:tw-min-w-[350px] sm:tw-max-w-lg sm:tw-flex-1">
				<span
					id="zenith-branch-description"
					class="tw-sr-only">
					Lists only branches tagged with the CI category. Baseline
					and Working switch which kind of branch is listed.
				</span>
				<osee-branch-picker
					class="tw-w-full"
					category="4" />
			</div>
		}
		@if (showSetSection()) {
			<div
				role="group"
				aria-label="CI Set"
				aria-describedby="zenith-set-description"
				class="tw-flex tw-w-full tw-min-w-0 tw-flex-col tw-gap-2 sm:tw-w-auto sm:tw-min-w-[350px] sm:tw-flex-1">
				<span
					id="zenith-set-description"
					class="tw-sr-only">
					CI Sets belong to a branch. The list shows the Sets of the
					selected branch.
				</span>
				@if (ciSetSelector()) {
					@if (branchIdValid() && branchType()) {
						<osee-set-dropdown class="tw-w-full" />
					} @else {
						<p
							class="tw-m-0 tw-flex tw-min-h-[88px] tw-items-center tw-text-sm tw-opacity-50">
							Select a branch to choose a CI Set.
						</p>
					}
				}
				<!-- Page filters (batch selector, multi-set selector) stay with the Set they refine. -->
				<div
					class="tw-flex tw-w-full tw-flex-col tw-gap-2 empty:tw-hidden">
					<ng-content></ng-content>
				</div>
			</div>
		}
		<!-- Actions sit at the top-right of the controls row. -->
		<div
			role="group"
			aria-label="Dashboard actions"
			class="tw-ml-auto tw-flex tw-flex-row tw-flex-wrap tw-items-center tw-justify-end tw-gap-2 tw-self-start">
			<ng-content select="[ciDashboardAction]"></ng-content>
			@if (actionButton() && branchIdValid() && branchType()) {
				<div class="tw-min-w-[210px]">
					<osee-current-action-drop-down />
				</div>
			}
			<osee-overflow-menu />
		</div>
	</section>`,
})
export class CiDashboardControlsComponent {
	branchPicker = input(true);
	ciSetSelector = input(true);
	showSetSection = input(true);
	actionButton = input(false);
	private route = inject(ActivatedRoute);
	private uiService = inject(CiDashboardUiService);
	// Syncs ?set= to ciSetId at page level so page data does not wait for the Set dropdown.
	private readonly _setRoute = inject(CiSetRoutedUiService);
	private _queryParamMap = toSignal(this.route.queryParamMap);
	private _paramEffect = effect(() => {
		const params = this._queryParamMap();
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
