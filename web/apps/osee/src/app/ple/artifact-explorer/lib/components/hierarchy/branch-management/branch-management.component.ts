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
import { map, take, tap } from 'rxjs';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { MatButton } from '@angular/material/button';
import { CurrentActionDropDownComponent } from '../../../../../../configuration-management/components/current-action-drop-down/current-action-drop-down.component';
import { MatIcon } from '@angular/material/icon';
import { CreateBranchButtonComponent } from '../../../../../../shared/components/create-branch-button/create-branch-button.component';
import {
	CommitBranchButtonComponent,
	UpdateFromParentButtonComponent,
} from '@osee/commit/components';
import { branchSentinel } from '@osee/shared/types';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { ChangeReportButtonComponent } from '../change-report-button/change-report-button.component';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
	selector: 'osee-branch-management',
	imports: [
		MatButton,
		CurrentActionDropDownComponent,
		MatIcon,
		CreateBranchButtonComponent,
		CommitBranchButtonComponent,
		UpdateFromParentButtonComponent,
		ExpansionPanelComponent,
		ChangeReportButtonComponent,
		MatTooltip,
	],
	template: `
		<osee-expansion-panel
			title="Branch Management"
			[openDefault]="false">
			<div>
				@if (branchId() !== '' && branchId() !== '570') {
					@if (displayWorkflowButtons()) {
						<div class="tw-flex tw-items-center tw-gap-1">
							<osee-current-action-drop-down />
							@if (branchType() === 'working') {
								<button
									mat-raised-button
									class="tw-flex tw-justify-center tw-bg-osee-blue-7 tw-text-background-background dark:tw-bg-osee-blue-10 [&_*]:tw-m-0"
									(click)="openTeamWorkflowTab()"
									matTooltip="Open Team Workflow in tab">
									<mat-icon class="material-icons-outlined"
										>assignment</mat-icon
									>
								</button>
							}
						</div>
					}
					@if (showCreateBranch()) {
						<div class="tw-flex tw-items-center tw-gap-1">
							<osee-create-branch-button />
							<osee-commit-branch-button
								[sourceBranchId]="currBranchId()"
								[destBranchId]="currBranchParentId()"
								[disabled]="branchCommitButtonIsDisabled()" />
							<!-- Could add change detection to show this update from parent button only when there are changes on (committed to from another working branch) the parent branch that are not on this branch yet. Would make this button more responsive. -->
							<osee-update-from-parent-button
								[workingBranch]="currBranch()"
								[disabled]="isBaseline()" />
							<osee-change-report-button />
						</div>
					}
				}
			</div>
		</osee-expansion-panel>
	`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BranchManagementComponent {
	private tabService = inject(ArtifactExplorerTabService);
	private currBranchInfoService = inject(CurrentBranchInfoService);
	private currentActionService = inject(CurrentActionService);
	private uiService = inject(UiService);

	protected branchType = toSignal(this.uiService.type, { initialValue: '' });
	protected branchId = toSignal(this.uiService.id, { initialValue: '' });

	openTeamWorkflowTab() {
		this.currentActionService.branchWorkflowToken
			.pipe(
				take(1),
				tap((teamwf) => this.tabService.addTeamWorkflowTab(teamwf))
			)
			.subscribe();
	}

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
