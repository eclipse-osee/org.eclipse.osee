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
import { combineLatest, from, iif, of } from 'rxjs';
import {
	filter,
	map,
	mergeMap,
	reduce,
	share,
	shareReplay,
	switchMap,
	take,
} from 'rxjs/operators';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	CurrentStructureService,
	HeaderService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import type {
	element,
	settingsDialogData,
	structure,
} from '@osee/messaging/shared/types';
import { ColumnPreferencesDialogComponent } from '@osee/messaging/shared/dialogs/preferences';
import {
	defaultEditElementProfile,
	defaultViewElementProfile,
} from '@osee/messaging/shared/constants';

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styles: [],
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
	preferences = this.structureService.preferences;
	isEditing = this.preferences.pipe(
		map((x) => x.inEditMode),
		share(),
		shareReplay(1)
	);
	currentElementHeaders = combineLatest([
		this.headerService.AllElementHeaders,
		this.preferences,
	]).pipe(
		switchMap(([allHeaders, response]) =>
			of(response.columnPreferences).pipe(
				mergeMap((r) =>
					from(r).pipe(
						filter(
							(column) =>
								allHeaders.includes(
									column.name as Extract<
										keyof element,
										string
									>
								) && column.enabled
						),
						map(
							(header) =>
								header.name as Extract<keyof element, string>
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as (keyof element)[]
						)
					)
				)
			)
		),
		mergeMap((headers) =>
			iif(
				() => headers.length !== 0,
				of(headers),
				this.isEditing.pipe(
					switchMap((_isEditing) =>
						_isEditing
							? of(defaultEditElementProfile)
							: of(defaultViewElementProfile)
					)
				)
			)
		),
		share(),
		shareReplay(1)
	);
	currentStructureHeaders = combineLatest([
		this.headerService.AllStructureHeaders,
		this.preferences,
	]).pipe(
		switchMap(([structureHeaders, response]) =>
			of(response.columnPreferences).pipe(
				mergeMap((r) =>
					from(r).pipe(
						filter(
							(column) =>
								structureHeaders.includes(
									column.name as Extract<
										keyof structure,
										string
									>
								) && column.enabled
						),
						map(
							(header) =>
								header.name as Extract<keyof structure, string>
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as Extract<keyof structure, string>[]
						)
					)
				)
			)
		),
		mergeMap((headers) =>
			iif(
				() => headers.length !== 0,
				of(headers),
				of([
					'name',
					'description',
					'interfaceMinSimultaneity',
					'interfaceMaxSimultaneity',
					'interfaceTaskFileType',
					'interfaceStructureCategory',
				])
			)
		),
		switchMap((finalHeaders) => of([' ', ...finalHeaders])),
		share(),
		shareReplay(1)
	);
	settingsDialog = combineLatest([
		this.structureService.BranchId,
		this.isEditing,
		this.preferencesService.globalPrefs,
		this.currentElementHeaders,
		this.currentStructureHeaders,
		this.headerService.AllElementHeaders,
		this.headerService.AllStructureHeaders,
	]).pipe(
		take(1),
		switchMap(
			([
				branch,
				edit,
				globalPrefs,
				elements,
				structures,
				allElementHeaders,
				allStructureHeaders,
			]) =>
				this.dialog
					.open(ColumnPreferencesDialogComponent, {
						data: {
							branchId: branch,
							allowedHeaders1: structures,
							allHeaders1: allStructureHeaders,
							allHeaders2: allElementHeaders,
							allowedHeaders2: elements,
							editable: edit,
							headers1Label: 'Structure Headers',
							headers2Label: 'Element Headers',
							headersTableActive: true,
							wordWrap: globalPrefs.wordWrap,
						} satisfies settingsDialogData,
					})
					.afterClosed()
					.pipe(
						take(1),
						switchMap((result) =>
							this.structureService
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
	);
	constructor(
		public dialog: MatDialog,
		private structureService: CurrentStructureService,
		private headerService: HeaderService,
		private preferencesService: PreferencesUIService
	) {}
	openSettingsDialog() {
		this.settingsDialog.subscribe();
	}
}
export default UsermenuComponent;
