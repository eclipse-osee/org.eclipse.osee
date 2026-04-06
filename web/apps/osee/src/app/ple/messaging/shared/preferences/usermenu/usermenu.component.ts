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
import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { ColumnPreferencesDialogComponent } from '@osee/messaging/shared/dialogs/preferences';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import type { settingsDialogData } from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { combineLatest } from 'rxjs';
import { map, share, shareReplay, switchMap, take } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styles: [],
	imports: [MatMenuItem, MatIcon],
})
export class UsermenuComponent {
	private routeState = inject(UiService);
	private id = toSignal(this.routeState.id, { initialValue: '' });
	dialog = inject(MatDialog);
	private preferencesService = inject(PreferencesUIService);

	settingsCapable = computed(() => {
		const id = this.id();
		return id !== '' && id !== '-1' && id !== '0';
	});
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
