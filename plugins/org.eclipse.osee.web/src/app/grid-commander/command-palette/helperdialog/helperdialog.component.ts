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
import { Component } from '@angular/core';
import { CommandGroupOptionsService } from '../../services/data-services/commands/command-group-options.service';
import {
	MatDialog,
	MatDialogRef,
	MatDialogModule,
} from '@angular/material/dialog';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-helperdialog',
	templateUrl: './helperdialog.component.html',
	styleUrls: ['./helperdialog.component.sass'],
	standalone: true,
	imports: [MatDialogModule, NgIf, NgFor, AsyncPipe],
})
export class HelperdialogComponent {
	allCommands = this.commandGroupOptsService.allCommands;

	constructor(
		private commandGroupOptsService: CommandGroupOptionsService,
		public dialog: MatDialog,
		public dialogRef: MatDialogRef<HelperdialogComponent>
	) {}

	closeDialog() {
		this.commandGroupOptsService.stringToFilterCommandsBy = '';
	}
}
