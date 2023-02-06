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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Observable } from 'rxjs';
import { share } from 'rxjs/operators';
import { ActionUserService } from '../internal/services/action-user.service';
import { CreateAction } from '../../../../types/configuration-management/create-action';
import { targetedVersion } from '../../../../types/configuration-management/targeted-version';
import { actionableItem } from '../../../../types/configuration-management/actionable-item';
import { PRIORITY } from '../../../../types/configuration-management/priority.enum';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';
/**
 * Dialog for creating a new action with the correct workType and category.
 */
@Component({
	selector: 'osee-create-action-dialog',
	templateUrl: './create-action-dialog.component.html',
	styleUrls: ['./create-action-dialog.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		FormsModule,
		MatDialogModule,
		MatFormFieldModule,
		MatSelectModule,
		MatOptionModule,
		MatInputModule,
		MatButtonModule,
	],
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
		@Inject(MAT_DIALOG_DATA) public data: CreateAction,
		public actionService: ActionStateButtonService,
		public userService: ActionUserService
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
