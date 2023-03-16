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
import { UserDataAccountService } from '@osee/auth';
import { transaction } from '@osee/shared/types';
import { combineLatest, from, iif, Observable, of } from 'rxjs';
import type { settingsDialogData } from '@osee/messaging/shared/types';
import {
	share,
	filter,
	switchMap,
	repeatWhen,
	shareReplay,
	map,
	reduce,
	take,
	tap,
} from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { transactionResult } from '@osee/shared/types/change-report';
import { MimPreferencesService } from '../http/mim-preferences.service';

@Injectable({
	providedIn: 'root',
})
export class PreferencesUIService {
	private _preferences = combineLatest([
		this.ui.id,
		this.userService.user,
	]).pipe(
		share(),
		filter(([id, user]) => id !== '' && id !== '-1'),
		switchMap(([id, user]) =>
			this.preferenceService.getUserPrefs(id, user).pipe(
				repeatWhen((_) => this.ui.update),
				share(),
				shareReplay({ bufferSize: 1, refCount: true })
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _inEditMode = this.preferences.pipe(map((x) => x.inEditMode));
	private _globalPrefs = this.preferences.pipe(
		map((prefs) => prefs.globalPrefs)
	);

	private _branchPrefs = combineLatest([
		this.ui.id,
		this.userService.user,
	]).pipe(
		share(),
		switchMap(([branch, user]) =>
			this.preferenceService.getBranchPrefs(user).pipe(
				repeatWhen((_) => this.ui.update),
				share(),
				switchMap((branchPrefs) =>
					from(branchPrefs).pipe(
						filter((pref) => !pref.includes(branch + ':')),
						reduce((acc, curr) => [...acc, curr], [] as string[])
					)
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	createOrUpdateGlobalUserPrefs(
		result: settingsDialogData
	): Observable<transactionResult> {
		return combineLatest([this.userService.user, this._preferences]).pipe(
			take(1),
			switchMap(([user, prefs]) =>
				of(prefs.globalPrefs).pipe(
					switchMap((globalPrefs) =>
						globalPrefs.id === '-1'
							? this.preferenceService.createGlobalUserPrefs(
									user,
									{ wordWrap: result.wordWrap }
							  )
							: this.preferenceService.updateGlobalUserPrefs(
									globalPrefs,
									{
										id: globalPrefs.id,
										name: globalPrefs.name,
										wordWrap: result.wordWrap,
									}
							  )
					)
				)
			)
		);
	}

	updatePreferences(preferences: settingsDialogData) {
		return this.createUserPreferenceBranchTransaction(
			preferences.editable
		).pipe(
			take(1),
			switchMap((transaction) =>
				this.preferenceService.performMutation(transaction).pipe(
					take(1),
					tap(() => {
						this.ui.updated = true;
					})
				)
			)
		);
	}

	private createUserPreferenceBranchTransaction(editMode: boolean) {
		return combineLatest([
			this.preferences,
			this.ui.id,
			this.BranchPrefs,
		]).pipe(
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

	constructor(
		private ui: UiService,
		private userService: UserDataAccountService,
		private preferenceService: MimPreferencesService
	) {}

	public get branchId() {
		return this.ui.id;
	}

	public set BranchId(id: string) {
		this.ui.idValue = id;
	}

	public get preferences() {
		return this._preferences;
	}
	public get inEditMode() {
		return this._inEditMode;
	}
	public get globalPrefs() {
		return this._globalPrefs;
	}
	public get BranchPrefs() {
		return this._branchPrefs;
	}
}
