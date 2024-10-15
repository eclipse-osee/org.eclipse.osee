/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Injectable, inject } from '@angular/core';
import { ArtifactExplorerPreferencesHttpService } from './artifact-explorer-preferences-http.service';
import { UserDataAccountService } from '@osee/auth';
import { Subject, combineLatest, repeat, switchMap, tap } from 'rxjs';
import { artifactExplorerUserPreferences } from '../types/user-preferences';

@Injectable({
	providedIn: 'root',
})
export class ArtifactExplorerPreferencesService {
	private preferencesService = inject(ArtifactExplorerPreferencesHttpService);
	private userDataService = inject(UserDataAccountService);

	private _prefsUpdated = new Subject<boolean>();

	private _artifactExplorerPreferences = this.preferencesService
		.getArtifactExplorerPreferences()
		.pipe(repeat({ delay: () => this._prefsUpdated }));

	createOrUpdateArtifactExplorerPrefs(
		newPrefs: artifactExplorerUserPreferences
	) {
		return combineLatest([
			this.userDataService.user,
			this.artifactExplorerPreferences,
		]).pipe(
			switchMap(([user, currentPrefs]) => {
				if (currentPrefs.id === '-1') {
					return this.preferencesService.createArtifactExplorerPrefs(
						user,
						newPrefs
					);
				}
				return this.preferencesService.updateArtifactExplorerPrefs(
					currentPrefs,
					newPrefs
				);
			}),
			tap(() => this._prefsUpdated.next(true))
		);
	}

	get artifactExplorerPreferences() {
		return this._artifactExplorerPreferences;
	}
}
