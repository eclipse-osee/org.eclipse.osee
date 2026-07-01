/*********************************************************************
 * Copyright (c) 2025 Boeing
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
	inject,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CurrentActionService } from '@osee/configuration-management/services';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { teamWorkflowTokenSentinel } from '@osee/shared/types/configuration-management';
import { map } from 'rxjs';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { CreateBranchButtonComponent } from '../../../../../../shared/components/create-branch-button/create-branch-button.component';
import {
	CommitBranchButtonComponent,
	UpdateFromParentButtonComponent,
} from '@osee/commit/components';
import { branchSentinel } from '@osee/shared/types';
import { ChangeReportButtonComponent } from '../change-report-button/change-report-button.component';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { CreateActionButtonComponent } from '@osee/configuration-management/components';

@Component({
	selector: 'osee-branch-management-panel',
	imports: [
		MatButton,
		MatIcon,
		CreateBranchButtonComponent,
		CommitBranchButtonComponent,
		UpdateFromParentButtonComponent,
		ChangeReportButtonComponent,
		MatTooltip,
		RouterLink,
		CreateActionButtonComponent,
	],
	template: `
		<div class="tw-flex tw-flex-wrap tw-items-center tw-gap-1 tw-p-4">
			@if (branchId() !== '' && branchId() !== '570') {
				@if (displayWorkflowButtons()) {
					@if (branchType() === 'working') {
						<a
							routerLink="/actra/workflow"
							[queryParams]="{
								id: branchWorkflowToken().id,
							}"
							target="_blank">
							<button
								mat-raised-button
								class="tw-flex tw-justify-center tw-bg-primary tw-text-background-background [&_*]:tw-m-0"
								matTooltip="Open Team Workflow in tab">
								<mat-icon class="material-icons-outlined"
									>assignment</mat-icon
								>
							</button>
						</a>
					} @else {
						<osee-create-action-button [opensDialog]="false" />
					}
				}
				@if (showCreateBranch()) {
					<osee-create-branch-button />
					<osee-commit-branch-button
						[sourceBranchId]="currBranchId()"
						[destBranchId]="currBranchParentId()"
						[disabled]="branchCommitButtonIsDisabled()"
						disabledMessage="Only working branches can be committed." />
					<osee-update-from-parent-button
						[workingBranch]="currBranch()"
						[disabled]="isBaseline()"
						disabledMessage="Baseline branches cannot be updated." />
					<osee-change-report-button />
				}
			} @else {
				<div class="tw-text-sm tw-opacity-50">
					Select a branch to see management options.
				</div>
			}
		</div>
	`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BranchManagementPanelComponent {
	private currBranchInfoService = inject(CurrentBranchInfoService);
	private currentActionService = inject(CurrentActionService);
	private uiService = inject(UiService);

	protected branchType = toSignal(this.uiService.type, { initialValue: '' });
	protected branchId = toSignal(this.uiService.id, { initialValue: '' });

	protected branchWorkflowToken = toSignal(
		this.currentActionService.branchWorkflowToken,
		{ initialValue: teamWorkflowTokenSentinel }
	);

	branchCategories = this.currBranchInfoService.currentBranch.pipe(
		map((currBranch) => currBranch.categories)
	);
	private _branchCategories$ = toSignal(this.branchCategories, {
		initialValue: [],
	});
	categoryNames = computed(() => {
		return this._branchCategories$().map((category) => category.name);
	});
	hasAtsCategory = computed(() => {
		return this.categoryNames().some((name) => name == 'ATS');
	});
	hasPleCategory = computed(() => {
		return this.categoryNames().some((name) => name == 'PLE');
	});
	isBaseline = computed(() => this.branchType() === 'baseline');
	workflowTokenValid = computed(
		() =>
			this.branchWorkflowToken().id !== undefined &&
			this.branchWorkflowToken().id !== '-1'
	);

	displayWorkflowButtons = computed(
		() =>
			this.workflowTokenValid() ||
			(this.hasAtsCategory() && this.isBaseline())
	);

	showCreateBranch = computed(
		() => !this.displayWorkflowButtons() && !this.hasAtsCategory()
	);

	currBranch = toSignal(this.currBranchInfoService.currentBranch, {
		initialValue: branchSentinel,
	});
	currBranchId = computed(() => this.currBranch().id);

	currBranchParentId = toSignal(this.currBranchInfoService.parentBranch, {
		initialValue: branchSentinel.id,
	});

	// Do not allow commit if the branch type is not working (0)
	branchCommitButtonIsDisabled = computed(
		() => this.currBranch().branchType !== '0'
	);
}
