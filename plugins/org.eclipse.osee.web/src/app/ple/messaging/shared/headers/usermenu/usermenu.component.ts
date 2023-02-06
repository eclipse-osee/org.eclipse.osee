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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import {
	ColumnPreferencesDialogComponent,
	PreferencesUIService,
	settingsDialogData,
} from '@osee/messaging/shared';
import { combineLatest, iif, of } from 'rxjs';
import { map, share, shareReplay, take, switchMap } from 'rxjs/operators';
import { RouteStateService } from '../../../connection-view/lib/services/route-state-service.service';

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styleUrls: ['./usermenu.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		MatMenuModule,
		MatButtonModule,
		MatIconModule,
		MatDialogModule,
	],
})
export class UsermenuComponent {
	settingsCapable = this.routeState.id.pipe(
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
	constructor(
		private routeState: RouteStateService,
		public dialog: MatDialog,
		private preferencesService: PreferencesUIService
	) {}
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
