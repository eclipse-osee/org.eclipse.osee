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
import { combineLatest, from, iif, Observable, of } from 'rxjs';
import {
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	mergeMap,
	reduce,
	repeatWhen,
	share,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';
import { TypesService } from '../../shared/services/http/types.service';
import { PlatformType } from '../../shared/types/platformType';
import { transaction } from 'src/app/transactions/transaction';
import { settingsDialogData } from '../../shared/types/settingsdialog';
import { applic } from '../../../../types/applicability/applic';
import { enumeration, enumerationSet } from '../../shared/types/enum.d';
import { ApplicabilityListUIService } from '../../shared/services/ui/applicability-list-ui.service';
import { EnumerationUIService } from '../../shared/services/ui/enumeration-ui.service';
import { PreferencesUIService } from '../../shared/services/ui/preferences-ui.service';
import { EnumsService } from '../../shared/services/http/enums.service';
import { TypesUIService } from '../../shared/services/ui/types-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentTypesService {
	private _typeData = combineLatest([
		this.uiService.filter,
		this.uiService.BranchId,
	]).pipe(
		share(),
		filter(([filter, id]) => id !== ''),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([filter, id]) =>
			this.typesService.getFilteredTypes(filter, id).pipe(
				repeatWhen((_) => this.uiService.typeUpdateRequired),
				share(),
				tap((y) => {
					//this.uiService.updateTypes = false;
					if (y.length <= this.uiService.columnCount.getValue()) {
						this.uiService.singleLineAdjustmentNumber = 30;
					} else {
						this.uiService.singleLineAdjustmentNumber = 0;
					}
				})
			)
		)
	);

	constructor(
		private typesService: TypesService,
		private uiService: PlMessagingTypesUIService,
		private preferenceService: PreferencesUIService,
		private applicabilityService: ApplicabilityListUIService,
		private enumSetService: EnumerationUIService,
		private constantEnumService: EnumsService,
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

	/**
	 * Creates a new platform type using the platform types POST API, current branch,but without the id,idIntValue, and idString present and includes enum values
	 * @todo fix this up later to be in enumeration-ui.service
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
