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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { ColumnPreferencesDialogComponent } from '@osee/messaging/shared/dialogs/preferences';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import type { settingsDialogData } from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { combineLatest, iif, of } from 'rxjs';
import {
	debounceTime,
	map,
	share,
	shareReplay,
	switchMap,
	take,
} from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styles: [],
	standalone: true,
	imports: [AsyncPipe, MatMenuItem, MatIcon, MatButton],
})
export class UsermenuComponent {
	private routeState = inject(UiService);
	dialog = inject(MatDialog);
	private preferencesService = inject(PreferencesUIService);

	settingsCapable = this.routeState.id.pipe(
		debounceTime(0),
		switchMap((val) =>
			iif(
				() => val !== '' && val !== '-1' && val !== '0',
				of('true'),
				of('false')
			)
		)
	);
	inEditMode = this.preferencesService.preferences.pipe(
		map((r) => r.inEditMode),
		share(),
		shareReplay(1)
	);

	openSettingsDialog() {
		combineLatest([
			this.inEditMode,
			this.preferencesService.globalPrefs,
			this.routeState.id,
		])
			.pipe(
				take(1),
				switchMap(([edit, globalPrefs, id]) =>
					this.dialog
						.open(ColumnPreferencesDialogComponent, {
							data: {
								branchId: id,
								allHeaders2: [],
								allowedHeaders2: [],
								allHeaders1: [],
								allowedHeaders1: [],
								editable: edit,
								headers1Label: '',
								headers2Label: '',
								headersTableActive: false,
								wordWrap: globalPrefs.wordWrap,
							} as settingsDialogData,
						})
						.afterClosed()
						.pipe(
							take(1),
							switchMap((result) =>
								this.preferencesService
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
