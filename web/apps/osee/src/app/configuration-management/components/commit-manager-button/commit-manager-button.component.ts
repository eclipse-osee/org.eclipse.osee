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
import { NgClass } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltip } from '@angular/material/tooltip';
import { CommitManagerDialogComponent } from '@osee/commit/components';
import { ActionService } from '@osee/configuration-management/services';
import { UiService } from '@osee/shared/services';
import { teamWorkflowDetailsImpl } from '@osee/shared/types/configuration-management';
import { switchMap, repeat, filter, take } from 'rxjs';

@Component({
	selector: 'osee-commit-manager-button',
	imports: [MatButton, MatTooltip, NgClass],
	template: `<button
		mat-raised-button
		class="tw-text-background-background"
		[ngClass]="allBranchesCommitted() ? 'tw-bg-success' : 'tw-bg-primary'"
		[matTooltip]="
			allBranchesCommitted()
				? 'All commits are complete'
				: 'There are ' +
					teamWorkflow().branchesToCommitTo.length +
					' branches left to commit to'
		"
		(click)="openCommitManager()">
		Open Commit Manager
	</button>`,
})
export class CommitManagerButtonComponent {
	actionService = inject(ActionService);
	uiService = inject(UiService);

	teamWorkflowId = input.required<`${number}`>();
	teamWorkflowId$ = toObservable(this.teamWorkflowId);

	teamWorkflow = toSignal(
		this.teamWorkflowId$.pipe(
			switchMap((id) =>
				this.actionService.getTeamWorkflowDetails(id).pipe(
					repeat({
						delay: () =>
							this.uiService.updateArtifact.pipe(
								filter(
									(updatedId) => updatedId === id.toString()
								)
							),
					})
				)
			)
		),
		{ initialValue: new teamWorkflowDetailsImpl() }
	);

	teamWorkflow$ = toObservable(this.teamWorkflow);

	allBranchesCommitted = computed(
		() => this.teamWorkflow().branchesToCommitTo.length === 0
	);

	dialog = inject(MatDialog);

	openCommitManager() {
		this.teamWorkflow$
			.pipe(
				take(1),
				switchMap((teamWf) =>
					this.dialog
						.open(CommitManagerDialogComponent, {
							data: teamWf,
							minWidth: '60%',
							width: '60%',
						})
						.afterClosed()
				)
			)
			.subscribe();
	}
}
