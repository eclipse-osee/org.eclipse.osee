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
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CurrentBranchTransactionService } from '../internal/services/current-branch-transaction.service';

@Component({
	selector: 'osee-undo-button-branch',
	templateUrl: './undo-button-branch.component.html',
	styleUrls: ['./undo-button-branch.component.sass'],
	standalone: true,
	imports: [MatButtonModule, MatTooltipModule, MatIconModule],
})
export class UndoButtonBranchComponent {
	private _undoLatest = this._undoService.undoLatest;
	constructor(private _undoService: CurrentBranchTransactionService) {}

	undo() {
		return this._undoLatest.subscribe();
	}
}
