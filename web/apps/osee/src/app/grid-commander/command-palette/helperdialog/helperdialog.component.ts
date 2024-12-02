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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
	MatDialog,
	MatDialogContent,
	MatDialogRef,
} from '@angular/material/dialog';
import { CommandGroupOptionsService } from '../../services/data-services/commands/command-group-options.service';

@Component({
	selector: 'osee-helperdialog',
	templateUrl: './helperdialog.component.html',
	styles: [],
	imports: [AsyncPipe, MatDialogContent],
})
export class HelperdialogComponent {
	private commandGroupOptsService = inject(CommandGroupOptionsService);
	dialog = inject(MatDialog);
	dialogRef = inject<MatDialogRef<HelperdialogComponent>>(MatDialogRef);

	allCommands = this.commandGroupOptsService.allCommands;

	closeDialog() {
		this.commandGroupOptsService.stringToFilterCommandsBy = '';
	}
}
