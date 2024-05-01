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
import { Injectable } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import {
	PreferencesUIService,
	TypesService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import type {
	PlatformType,
	enumeration,
	settingsDialogData,
} from '@osee/messaging/shared/types';
import { transaction } from '@osee/shared/types';
import { applic } from '@osee/shared/types/applicability';
import { BehaviorSubject, combineLatest, iif, of } from 'rxjs';
import {
	debounceTime,
	distinctUntilChanged,
	repeatWhen,
	share,
	shareReplay,
	startWith,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentTypesService {
	private _currentPage$ = new BehaviorSubject<number>(0);
	private _currentPageSize$ = new BehaviorSubject<number>(10);

	private _filterObs = toObservable(this.uiService.filter);
	private _typeData = combineLatest([
		this._filterObs,
		this.uiService.BranchId,
		this.currentPage,
		this.currentPageSize,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([filter, id, page, pageSize]) =>
			iif(
				() => id !== '' && id !== '0' && id !== '-1',
				this.typesService
					.getFilteredTypes(filter, id, page + 1, pageSize)
					.pipe(
						repeatWhen((_) => this.uiService.typeUpdateRequired),
						share(),
						tap((y) => {
							//this.uiService.updateTypes = false;
							if (
								y.length <=
								this.uiService.columnCount.getValue()
							) {
								this.uiService.singleLineAdjustmentNumber = 30;
							} else {
								this.uiService.singleLineAdjustmentNumber = 0;
							}
						})
					),
				of([] as PlatformType[])
			)
		),
		shareReplay({ refCount: true, bufferSize: 1 }),
		startWith([] as PlatformType[])
	);

	private _typeDataCount = combineLatest([
		this._filterObs,
		this.uiService.BranchId,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([filter, id]) =>
			iif(
				() => id !== '' && id !== '0' && id !== '-1',
				this.typesService.getFilteredTypesCount(filter, id).pipe(
					repeatWhen((_) => this.uiService.typeUpdateRequired),
					share()
				),
				of(0)
			)
		),
		shareReplay({ refCount: true, bufferSize: 1 })
	);
	constructor(
		private typesService: TypesService,
		private uiService: PlMessagingTypesUIService,
		private preferenceService: PreferencesUIService,
		private sharedTypeService: TypesUIService
	) {}

	/**
	 * Returns a list of platform types based on current branch and filter conditions(debounced).
	 * Sets the "single line adjustment" which is used to offset platform type cards in the grid when there is only one line of platform types
	 * Also updates when insertions are done via API.
	 * @returns @type {Observable<PlatformType[]>} list of platform types
	 */
	get typeData() {
		return this._typeData;
	}

	get typeDataCount() {
		return this._typeDataCount;
	}
	/**
	 * Creates a new platform type using the platform types POST API, current branch,but without the id,idIntValue, and idString present and includes enum values
	 * @todo fix this up later to be in enumeration-ui.service
	 * @TODO replace enumSetData with actual enumerationSet
	 * @param body @type {PlatformType} platform type to create
	 * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
	 */
	createType(
		body: PlatformType | Partial<PlatformType>,
		isNewEnumSet: boolean,
		enumSetData: {
			enumSetId: string;
			enumSetName: string;
			enumSetDescription: string;
			enumSetApplicability: applic;
			enums: enumeration[];
		}
	) {
		return this.sharedTypeService.createType(
			body,
			isNewEnumSet,
			enumSetData
		);
	}

	get currentPage() {
		return this._currentPage$;
	}

	set page(page: number) {
		this._currentPage$.next(page);
	}

	get currentPageSize() {
		return this._currentPageSize$;
	}
	set pageSize(page: number) {
		this._currentPageSize$.next(page);
	}

	public get preferences() {
		return this.preferenceService.preferences;
	}
	public get inEditMode() {
		return this.preferenceService.inEditMode;
	}
	public get BranchPrefs() {
		return this.preferenceService.BranchPrefs;
	}

	updatePreferences(preferences: settingsDialogData) {
		return this.createUserPreferenceBranchTransaction(
			preferences.editable
		).pipe(
			take(1),
			switchMap((transaction) =>
				this.typesService.performMutation(transaction).pipe(
					take(1),
					tap(() => {
						this.uiService.updateTypes = true;
					})
				)
			)
		);
	}

	private createUserPreferenceBranchTransaction(editMode: boolean) {
		return combineLatest(
			this.preferences,
			this.uiService.BranchId,
			this.BranchPrefs
		).pipe(
			take(1),
			switchMap(([prefs, branch, branchPrefs]) =>
				iif(
					() => prefs.hasBranchPref,
					of<transaction>({
						branch: '570',
						txComment: 'Updating MIM User Preferences',
						modifyArtifacts: [
							{
								id: prefs.id,
								setAttributes: [
									{
										typeName: 'MIM Branch Preferences',
										value: [
											...branchPrefs,
											`${branch}:${editMode}`,
										],
									},
								],
							},
						],
					}),
					of<transaction>({
						branch: '570',
						txComment: 'Updating MIM User Preferences',
						modifyArtifacts: [
							{
								id: prefs.id,
								addAttributes: [
									{
										typeName: 'MIM Branch Preferences',
										value: `${branch}:${editMode}`,
									},
								],
							},
						],
					})
				)
			)
		);
	}
}
