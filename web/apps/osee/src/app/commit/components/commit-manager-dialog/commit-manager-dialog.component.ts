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
import { CdkTrapFocus } from '@angular/cdk/a11y';
import { Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import {} from '@osee/commit/types';
import { ActionService } from '@osee/configuration-management/services';
import { UiService } from '@osee/shared/services';
import { teamWorkflowDetails } from '@osee/shared/types/configuration-management';
import { filter, repeat, switchMap } from 'rxjs';
import { CommitBranchButtonComponent } from '../commit-branch-button/commit-branch-button.component';
import {
	COMMITSTATUS,
	branchCommitStatus,
	commitStatus,
} from '@osee/configuration-management/types';

@Component({
	selector: 'osee-commit-manager-dialog',
	imports: [
		CommitBranchButtonComponent,
		MatDialogContent,
		MatDialogTitle,
		MatDialogActions,
		MatDialogClose,
		MatButton,
		CdkTrapFocus,
		MatTable,
		MatColumnDef,
		MatRowDef,
		MatHeaderRowDef,
		MatCellDef,
		MatHeaderCellDef,
		MatRow,
		MatHeaderRow,
		MatCell,
		MatHeaderCell,
	],
	templateUrl: './commit-manager-dialog.component.html',
})
export class CommitManagerDialogComponent {
	teamWorkflow = signal(inject<teamWorkflowDetails>(MAT_DIALOG_DATA));

	private actionService = inject(ActionService);
	private uiService = inject(UiService);

	branches = toSignal(
		toObservable(this.teamWorkflow).pipe(
			filter((teamWf) => teamWf.id > 0),
			switchMap((teamWf) =>
				this.actionService.getBranchCommitStatus(teamWf.id).pipe(
					repeat({
						delay: () =>
							this.uiService.updateArtifact.pipe(
								filter((id) => id === teamWf.id.toString())
							),
					})
				)
			)
		)
	);

	headers = ['Branch Name', 'Commit Status', ' '];

	getCommitStatusText(status: commitStatus) {
		return COMMITSTATUS[status];
	}

	isCommitButtonDisabled(status: branchCommitStatus) {
		return !(
			status.commitStatus === 'Commit_Needed' ||
			status.commitStatus === 'Merge_In_Progress'
		);
	}
}
