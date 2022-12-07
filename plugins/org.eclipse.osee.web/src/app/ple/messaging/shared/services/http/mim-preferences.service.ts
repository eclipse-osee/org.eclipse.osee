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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { user } from 'src/app/userdata/types/user-data-user';
import { apiURL } from 'src/environments/environment';
import { element } from '../../types/element';
import { structure } from '../../types/structure';
import { message } from '../../../message-interface/types/messages';
import { subMessage } from '../../../message-interface/types/sub-messages';
import {
	MimPreferences,
	MimUserGlobalPreferences,
} from '../../types/mim.preferences';
import { TransactionService } from 'src/app/transactions/transaction.service';
import { attributeType, transaction } from 'src/app/transactions/transaction.d';
import { ARTIFACTTYPEID } from 'src/app/types/constants/ArtifactTypeId.enum';

@Injectable({
	providedIn: 'root',
})
export class MimPreferencesService {
	constructor(
		private http: HttpClient,
		private txService: TransactionService
	) {}

	getUserPrefs(branchId: string, user: user) {
		return this.http.get<
			MimPreferences<structure & message & subMessage & element>
		>(apiURL + '/mim/user/' + branchId);
	}

	getBranchPrefs(user: user) {
		return this.http.get<string[]>(apiURL + '/mim/user/branches');
	}

	createGlobalUserPrefs(
		user: user,
		prefs: Partial<MimUserGlobalPreferences>
	) {
		const tx = {
			branch: '570',
			txComment: 'Create MIM User Global Preferences',
			createArtifacts: [
				{
					typeId: ARTIFACTTYPEID.GLOBALUSERPREFERENCES,
					name: 'MIM Global User Preferences',
					key: 'globalPrefs',
					attributes: [
						{
							typeName: 'MIM Word Wrap',
							value: prefs.wordWrap,
						},
					],
				},
			],
			addRelations: [
				{
					typeName: 'User to MIM User Global Preferences',
					aArtId: user.id,
					bArtId: 'globalPrefs',
				},
			],
		} as transaction;

		return this.txService.performMutation(tx);
	}

	updateGlobalUserPrefs(
		current: MimUserGlobalPreferences,
		updated: MimUserGlobalPreferences
	) {
		let setAttributes: attributeType[] = [];
		if (current.wordWrap !== updated.wordWrap) {
			setAttributes.push({
				typeName: 'MIM Word Wrap',
				value: updated.wordWrap,
			});
		}

		const tx = {
			branch: '570',
			txComment: 'Updating MIM User Global Preferences',
			modifyArtifacts: [{ id: updated.id, setAttributes: setAttributes }],
		} as transaction;

		return this.txService.performMutation(tx);
	}
}
