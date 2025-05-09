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
import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { ColumnPreferencesDialogComponent } from '@osee/messaging/shared/dialogs/preferences';
import {
	CurrentMessagesService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import type { settingsDialogData } from '@osee/messaging/shared/types';
import { combineLatest } from 'rxjs';
import {
	map,
	share,
	shareReplay,
	switchMap,
	take,
	takeUntil,
} from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styles: [],
	imports: [MatMenuItem, MatIcon],
})
export class UsermenuComponent {
	private messageService = inject(CurrentMessagesService);
	dialog = inject(MatDialog);
	private preferencesService = inject(PreferencesUIService);

	preferences = this.messageService.preferences.pipe(
		takeUntil(this.messageService.done)
	);
	inEditMode = this.preferences.pipe(
		map((r) => r.inEditMode),
		share(),
		shareReplay(1),
		takeUntil(this.messageService.done)
	);

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
