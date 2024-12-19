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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
} from '@angular/core';
import {
	MatDialog,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { PeerReviewBranchSelectorComponent } from '../peer-review-branch-selector/peer-review-branch-selector.component';
import { MatButton } from '@angular/material/button';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { CreatePeerReviewButtonComponent } from '../create-peer-review-button/create-peer-review-button.component';
import { PeerReviewUiService } from '../../services/peer-review-ui.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { branchSelected } from '../../types/peer-review';
import { iif, of, switchMap, take, tap } from 'rxjs';
import { MatTooltip } from '@angular/material/tooltip';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { ManageActionButtonComponent } from '@osee/configuration-management/components';
import { BranchInfoService, UiService } from '@osee/shared/services';
import { ConfirmDialogComponent } from '@osee/shared/dialogs';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'osee-peer-review-dialog',
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatFormField,
		MatLabel,
		MatInput,
		MatButton,
		MatIcon,
		MatSelectionList,
		MatListOption,
		MatTooltip,
		RouterLink,
		CreatePeerReviewButtonComponent,
		PeerReviewBranchSelectorComponent,
		ManageActionButtonComponent,
	],
	templateUrl: './peer-review-dialog.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewDialogComponent {
	private prUIService = inject(PeerReviewUiService);
	private branchInfoService = inject(BranchInfoService);
	private uiService = inject(UiService);
	private dialog = inject(MatDialog);
	private dialogRef =
		inject<MatDialogRef<PeerReviewDialogComponent>>(MatDialogRef);

	prBranchId = toSignal(this.prUIService.prBranchId);

	filter = this.prUIService.workingBranchFilter;

	private _allWorkingBranches = toSignal(this.prUIService.workingBranches, {
		initialValue: [],
	});

	toAdd = this.prUIService.branchesToAdd;
	toRemove = this.prUIService.branchesToRemove;

	isCommitting = computed(
		() =>
			this._allWorkingBranches().filter(
				(branch) => branch.committedToBaseline
			).length > 0
	);
	completedCommitting = computed(
		() =>
			this._allWorkingBranches().length > 0 &&
			this._allWorkingBranches().filter(
				(branch) => branch.selected && !branch.committedToBaseline
			).length === 0
	);

	workingBranches = computed(() => {
		if (this.isCommitting()) {
			return this._allWorkingBranches().filter(
				(branch) => branch.selected
			);
		}
		return this._allWorkingBranches();
	});

	handleChange(branchSelection: branchSelected, selected: boolean) {
		this.prUIService.handleBranchSelection(branchSelection, selected);
	}

	isBranchSelected(branchSelection: branchSelected) {
		const isAdded =
			this.toAdd().filter((b) => b.id === branchSelection.branch.id)
				.length > 0;
		const isRemoved =
			this.toRemove().filter((b) => b.id === branchSelection.branch.id)
				.length > 0;
		return (
			(branchSelection.selected && !isRemoved) ||
			(!branchSelection.selected && isAdded)
		);
	}

	compareFn(o1: branchSelected, o2: branchSelected) {
		if (!o1 || !o2) {
			return false;
		}
		return o1.branch.id === o2.branch.id;
	}

	applySelected() {
		this.prUIService.applyWorkingBranches().pipe(take(1)).subscribe();
	}

	resetSelections() {
		this.prUIService.resetBranchSelections();
	}

	onNoClick() {
		this.dialogRef.close();
	}

	closePeerReview() {
		const branchId = this.prBranchId();
		if (branchId && branchId !== '-1') {
			this.dialog
				.open(ConfirmDialogComponent, {
					data: {
						text: `Are you sure you want to close this Peer Review branch?`,
					},
				})
				.afterClosed()
				.pipe(
					take(1),
					switchMap((confirm) =>
						iif(
							() => confirm === false,
							of(),
							this.branchInfoService.archiveBranch(branchId).pipe(
								take(1),
								tap((resp) => {
									if (resp.ok) {
										this.prUIService.PRBranchId = '-1';
										this.uiService.updated = true;
									}
								})
							)
						)
					)
				)
				.subscribe();
		}
	}
}
