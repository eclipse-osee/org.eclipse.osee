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
	input,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { CommitManagerDialogComponent } from '@osee/commit/components';
import { MatDialog } from '@angular/material/dialog';
import { teamWorkflowDetails } from '@osee/shared/types/configuration-management';

@Component({
	selector: 'osee-commit-manager-button',
	imports: [MatButton, MatTooltip],
	template: `<button
		mat-flat-button
		[class]="
			allBranchesCommitted()
				? 'tw-whitespace-nowrap tw-bg-success tw-text-background-background'
				: 'primary-button tw-whitespace-nowrap'
		"
		[matTooltip]="commitTooltip()"
		(click)="openCommitManager()">
		Open Commit Manager
	</button>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommitManagerButtonComponent {
	/**
	 * The workflow to commit. Provided by the container (which already owns it); this presentational
	 * button does not fetch, so it never duplicates the container's `/ats/teamwf/details` GET.
	 */
	teamWorkflow = input.required<teamWorkflowDetails>();

	protected readonly allBranchesCommitted = computed(
		() => this.teamWorkflow().branchesToCommitTo.length === 0
	);

	protected readonly commitTooltip = computed(() =>
		this.allBranchesCommitted()
			? 'All commits are complete.'
			: `There are ${this.teamWorkflow().branchesToCommitTo.length} branches left to commit to.`
	);

	private readonly dialog = inject(MatDialog);

	protected openCommitManager() {
		this.dialog.open(CommitManagerDialogComponent, {
			data: this.teamWorkflow(),
			minWidth: '60%',
			width: '60%',
		});
	}
}
