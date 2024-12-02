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
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { OperatorFunction, filter, take } from 'rxjs';
import { AddRolesDialogComponent } from '../../dialogs/add-roles-dialog/add-roles-dialog.component';
import {
	DefaultTrainingRoleRecord,
	TrainingRoleRecord,
} from '../../types/training-role';

@Component({
	selector: 'osee-edit-roles-dropdown',
	templateUrl: './edit-roles-dropdown.component.html',
	styles: [],
	imports: [MatButton, MatMenuTrigger, MatIcon, MatMenu, MatMenuItem],
})
export class EditRolesDropdownComponent {
	dialog = inject(MatDialog);

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
