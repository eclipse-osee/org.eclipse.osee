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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { share } from 'rxjs/operators';
import { ActionService } from '../../../../ple-services/http/action.service';
import { PlConfigUserService } from '../../../../ple/plconfig/services/pl-config-user.service';
import {
	actionableItem,
	PLConfigCreateAction,
	PRIORITY,
	targetedVersion,
} from '../../../../ple/plconfig/types/pl-config-actions';
import { ActionStateButtonService } from '../../../services/action-state-button.service';
/**
 * Dialog for creating a new action with the correct workType and category.
 */
@Component({
	selector: 'osee-create-action-dialog',
	templateUrl: './create-action-dialog.component.html',
	styleUrls: ['./create-action-dialog.component.sass'],
})
export class CreateActionDialogComponent {
	users = this.userService.usersSorted;
	actionableItems: Observable<actionableItem[]> =
		this.actionService.actionableItems.pipe(share());
	targetedVersions!: Observable<targetedVersion[]>;
	changeTypes!: Observable<targetedVersion[]>;
	private _priorityKeys = Object.keys(PRIORITY);
	private _priorityValues = Object.values(PRIORITY);
	priorities = this._priorityKeys.map((item, row) => {
		return {
			name: item.split(/(?=[A-Z])/).join(' '),
			value: this._priorityValues[row],
		};
	});
	constructor(
		public dialogRef: MatDialogRef<CreateActionDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: PLConfigCreateAction,
		public actionService: ActionStateButtonService,
		public userService: PlConfigUserService
	) {}

	onNoClick(): void {
		this.dialogRef.close();
	}
	selectActionableItem() {
		this.targetedVersions = this.actionService.getVersions(
			this.data.actionableItem.id
		);
		this.changeTypes = this.actionService.getChangeTypes(
			this.data.actionableItem.id
		);
	}
}
