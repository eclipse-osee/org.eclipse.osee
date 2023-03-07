/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddRolesDialogComponent } from '../../dialogs/add-roles-dialog/add-roles-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { filter, OperatorFunction, take } from 'rxjs';
import {
	DefaultTrainingRoleRecord,
	TrainingRoleRecord,
} from './../../types/training-role';

@Component({
	standalone: true,
	selector: 'osee-edit-roles-dropdown',
	templateUrl: './edit-roles-dropdown.component.html',
	styleUrls: ['./edit-roles-dropdown.component.sass'],
	imports: [
		MatMenuModule,
		MatIconModule,
		MatButtonModule,
		MatNativeDateModule,
	],
})
export class EditRolesDropdownComponent {
	constructor(public dialog: MatDialog) {}

	addRoles() {
		this.dialog
			.open(AddRolesDialogComponent, {
				minWidth: '60%',
				data: new DefaultTrainingRoleRecord(),
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined) as OperatorFunction<
					TrainingRoleRecord | undefined,
					TrainingRoleRecord
				>
				//switchmap stuff here calling the training role service
			)
			.subscribe();
	}
}
