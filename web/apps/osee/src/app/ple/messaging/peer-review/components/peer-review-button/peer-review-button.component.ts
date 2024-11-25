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
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { PeerReviewDialogComponent } from '../peer-review-dialog/peer-review-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
	selector: 'osee-peer-review-button',
	imports: [MatButton, MatIcon],
	template: `<button
		mat-flat-button
		(click)="openPeerReviewDialog()">
		Peer Review
	</button>`,
})
export class PeerReviewButtonComponent {
	private dialog = inject(MatDialog);

	openPeerReviewDialog() {
		const dialogRef = this.dialog.open(PeerReviewDialogComponent, {
			minWidth: '60vw',
			minHeight: '80vh',
		});
		dialogRef.afterClosed().subscribe();
	}
}
