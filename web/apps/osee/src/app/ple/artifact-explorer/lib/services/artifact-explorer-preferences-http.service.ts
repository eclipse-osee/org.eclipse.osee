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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { TransactionService } from '@osee/transactions/services';
import {
	legacyAttributeType,
	legacyTransaction,
} from '@osee/transactions/types';
import { user } from '@osee/shared/types/auth';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { artifactExplorerUserPreferences } from '../types/user-preferences';

@Injectable({
	providedIn: 'root',
})
export class ArtifactExplorerPreferencesHttpService {
	private http = inject(HttpClient);

	private txService = inject(TransactionService);

	getArtifactExplorerPreferences() {
		return this.http.get<artifactExplorerUserPreferences>(
			apiURL + `/orcs/preferences/explorer`
		);
	}

	createArtifactExplorerPrefs(
		user: user,
		prefs: artifactExplorerUserPreferences
	) {
		const tx: legacyTransaction = {
			branch: '570',
			txComment: 'Create Artifact Explorer Preferences',
			createArtifacts: [
				{
					typeId: ARTIFACTTYPEIDENUM.ARTIFACTEXPLORERPREFERENCES,
					name: user.name + ' Artifact Explorer Preferences',
					key: 'artifactExplorerPrefs',
					attributes: [
						{
							typeName: 'Artifact Explorer Panel Location',
							value: prefs.artifactExplorerPanelLocation,
						},
					],
				},
			],
			addRelations: [
				{
					typeName: 'User to Artifact Explorer Preferences',
					aArtId: user.id,
					bArtId: 'artifactExplorerPrefs',
				},
			],
		};

		return this.txService.performMutation(tx);
	}

	updateArtifactExplorerPrefs(
		current: artifactExplorerUserPreferences,
		updated: artifactExplorerUserPreferences
	) {
		const setAttributes: legacyAttributeType[] = [];
		if (
			current.artifactExplorerPanelLocation !==
			updated.artifactExplorerPanelLocation
		) {
			setAttributes.push({
				typeName: 'Artifact Explorer Panel Location',
				value: updated.artifactExplorerPanelLocation,
			});
		}

		const tx = {
			branch: '570',
			txComment: 'Updating Artifact Explorer Preferences',
			modifyArtifacts: [{ id: updated.id, setAttributes: setAttributes }],
		} as legacyTransaction;

		return this.txService.performMutation(tx);
	}
}
