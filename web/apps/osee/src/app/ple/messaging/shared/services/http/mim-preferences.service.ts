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
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { user } from '@osee/shared/types/auth';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import type { element } from '../../types/element';
import type { message } from '../../types/messages';
import type {
	MimPreferences,
	MimUserGlobalPreferences,
} from '../../types/mim.preferences';
import type { displayableStructureFields } from '../../types/structure';
import type { subMessage } from '../../types/sub-messages';

import { TransactionService } from '@osee/transactions/services';
import {
	legacyAttributeType,
	legacyTransaction,
} from '@osee/transactions/types';
@Injectable({
	providedIn: 'root',
})
export class MimPreferencesService {
	private http = inject(HttpClient);

	private txService = inject(TransactionService);

	getUserPrefs(branchId: string, _user: user) {
		return this.http.get<
			MimPreferences<
				displayableStructureFields & message & subMessage & element
			>
		>(apiURL + '/mim/user/' + branchId);
	}

	getBranchPrefs(_user: user) {
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
					typeId: ARTIFACTTYPEIDENUM.GLOBALUSERPREFERENCES,
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
		} as legacyTransaction;

		return this.txService.performMutation(tx);
	}

	updateGlobalUserPrefs(
		current: MimUserGlobalPreferences,
		updated: MimUserGlobalPreferences
	) {
		const setAttributes: legacyAttributeType[] = [];
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
		} as legacyTransaction;

		return this.txService.performMutation(tx);
	}

	performMutation(transaction: legacyTransaction) {
		return this.txService.performMutation(transaction);
	}
}
