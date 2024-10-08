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
import { Component, inject } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { CurrentBranchTransactionService } from '../internal/services/current-branch-transaction.service';

@Component({
	selector: 'osee-undo-button-branch',
	styles: [],
	standalone: true,
	imports: [MatButton, MatIconButton, MatIcon, MatTooltip],
	template: `<button
		mat-icon-button
		(click)="undo()"
		matTooltip="Undo the latest transaction via purging."
		data-cy="undo-btn">
		<mat-icon>undo</mat-icon>
	</button>`,
})
export class UndoButtonBranchComponent {
	private _undoService = inject(CurrentBranchTransactionService);

	private _undoLatest = this._undoService.undoLatest;

	undo() {
		return this._undoLatest.subscribe();
	}
}
