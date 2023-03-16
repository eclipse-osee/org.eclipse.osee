/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { combineLatest } from 'rxjs';
import {
	takeUntil,
	map,
	share,
	shareReplay,
	take,
	switchMap,
} from 'rxjs/operators';
import {
	CurrentMessagesService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import type { settingsDialogData } from '@osee/messaging/shared/types';
import { ColumnPreferencesDialogComponent } from '@osee/messaging/shared/dialogs/preferences';

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styleUrls: ['./usermenu.component.sass'],
	standalone: true,
	imports: [MatMenuModule, MatIconModule, MatDialogModule],
})
export class UsermenuComponent {
	preferences = this.messageService.preferences.pipe(
		takeUntil(this.messageService.done)
	);
	inEditMode = this.preferences.pipe(
		map((r) => r.inEditMode),
		share(),
		shareReplay(1),
		takeUntil(this.messageService.done)
	);
	constructor(
		private messageService: CurrentMessagesService,
		public dialog: MatDialog,
		private preferencesService: PreferencesUIService
	) {}

	openSettingsDialog() {
		combineLatest([
			this.inEditMode,
			this.preferencesService.globalPrefs,
			this.messageService.BranchId,
		])
			.pipe(
				take(1),
				switchMap(([edit, globalPrefs, branch]) =>
					this.dialog
						.open(ColumnPreferencesDialogComponent, {
							data: {
								branchId: branch,
								allHeaders2: [],
								allowedHeaders2: [],
								allHeaders1: [],
								allowedHeaders1: [],
								editable: edit,
								headers1Label: 'Structure Headers',
								headers2Label: 'Element Headers',
								headersTableActive: false,
								wordWrap: globalPrefs.wordWrap,
							} as settingsDialogData,
						})
						.afterClosed()
						.pipe(
							take(1),
							switchMap((result) =>
								this.messageService
									.updatePreferences(result)
									.pipe(
										switchMap((_) =>
											this.preferencesService.createOrUpdateGlobalUserPrefs(
												result
											)
										)
									)
							)
						)
				)
			)
			.subscribe();
	}
}

export default UsermenuComponent;
