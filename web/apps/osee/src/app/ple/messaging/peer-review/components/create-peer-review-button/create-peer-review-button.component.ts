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
import { Component, inject, input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { filter, switchMap, take, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { CreateAction } from '@osee/configuration-management/types';
import { CreateActionService } from '@osee/configuration-management/services';
import { UiService } from '@osee/shared/services';
import { CreateActionDialogComponent } from '@osee/configuration-management/components';
import { PeerReviewUiService } from '../../services/peer-review-ui.service';

@Component({
	selector: 'osee-create-peer-review-button',
	standalone: true,
	imports: [MatButton, MatIcon],
	template: `<button
		mat-raised-button
		(click)="addAction()"
		class="tw-bg-primary tw-text-background-background">
		<mat-icon>add</mat-icon>Create Peer Review
	</button>`,
})
export class CreatePeerReviewButtonComponent {
	workType = input('');

	private createActionService = inject(CreateActionService);
	private peerReviewUIService = inject(PeerReviewUiService);
	private uiService = inject(UiService);
	private dialog = inject(MatDialog);

	addAction() {
		this.createActionService.user
			.pipe(
				switchMap((thisUser) =>
					this.dialog
						.open(CreateActionDialogComponent, {
							data: new CreateAction(thisUser, this.workType()),
							minWidth: '60vw',
						})
						.afterClosed()
						.pipe(
							take(1),
							filter(
								(val): val is CreateAction => val !== undefined
							),
							switchMap((value) =>
								this.createActionService.createAction(
									value,
									'5'
								)
							),
							tap((resp) => {
								this.uiService.updated = true;
								if (resp.results.success) {
									this.peerReviewUIService.PRBranchId =
										resp.workingBranchId.id;
								}
							})
						)
				)
			)
			.subscribe();
	}
}
