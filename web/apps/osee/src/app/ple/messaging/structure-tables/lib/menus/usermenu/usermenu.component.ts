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
import {
	defaultEditElementProfile,
	defaultViewElementProfile,
} from '@osee/messaging/shared/constants';
import { ColumnPreferencesDialogComponent } from '@osee/messaging/shared/dialogs/preferences';
import {
	CurrentStructureService,
	HeaderService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import type {
	DisplayableElementProps,
	displayableStructureFields,
	settingsDialogData,
	structure,
} from '@osee/messaging/shared/types';
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

@Component({
	selector: 'osee-messaging-usermenu',
	templateUrl: './usermenu.component.html',
	styles: [],
	imports: [MatMenuItem, MatIcon],
})
export class UsermenuComponent {
	dialog = inject(MatDialog);
	private structureService = inject(CurrentStructureService);
	private headerService = inject(HeaderService);
	private preferencesService = inject(PreferencesUIService);

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
										keyof DisplayableElementProps,
										string
									>
								) && column.enabled
						),
						map(
							(header) =>
								header.name as Extract<
									keyof DisplayableElementProps,
									string
								>
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as (keyof DisplayableElementProps)[]
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
									column.name as
										| keyof displayableStructureFields
										| 'txRate'
										| 'publisher'
										| 'subscriber'
										| 'messageNumber'
										| 'messagePeriodicity'
										| ' '
								) && column.enabled
						),
						map(
							(header) =>
								header.name as
									| keyof structure
									| 'txRate'
									| 'publisher'
									| 'subscriber'
									| 'messageNumber'
									| 'messagePeriodicity'
									| ' '
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as (
								| keyof structure
								| 'txRate'
								| 'publisher'
								| 'subscriber'
								| 'messageNumber'
								| 'messagePeriodicity'
								| ' '
							)[]
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
					'name' as const,
					'description' as const,
					'interfaceMinSimultaneity' as const,
					'interfaceMaxSimultaneity' as const,
					'interfaceTaskFileType' as const,
					'interfaceStructureCategory' as const,
				])
			)
		),
		switchMap((finalHeaders) => of([...finalHeaders])),
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

	openSettingsDialog() {
		this.settingsDialog.subscribe();
	}
}
export default UsermenuComponent;
