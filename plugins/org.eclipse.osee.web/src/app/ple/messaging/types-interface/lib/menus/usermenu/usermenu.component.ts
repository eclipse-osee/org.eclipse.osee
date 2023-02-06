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
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	ColumnPreferencesDialogComponent,
	PreferencesUIService,
	settingsDialogData,
} from '@osee/messaging/shared';
import { combineLatest } from 'rxjs';
import { take, switchMap } from 'rxjs/operators';
import { CurrentTypesService } from '../../services/current-types.service';
import { PlMessagingTypesUIService } from '../../services/pl-messaging-types-ui.service';

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styleUrls: ['./usermenu.component.sass'],
	standalone: true,
	imports: [
		MatButtonModule,
		MatIconModule,
		MatMenuModule,
		MatDialogModule,
		MatTooltipModule,
	],
})
export class UsermenuComponent {
	constructor(
		private typesService: CurrentTypesService,
		private uiService: PlMessagingTypesUIService,
		public dialog: MatDialog,
		private preferencesService: PreferencesUIService
	) {}

	openSettingsDialog() {
		combineLatest([
			this.typesService.inEditMode,
			this.preferencesService.globalPrefs,
			this.uiService.BranchId,
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
								this.typesService
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
