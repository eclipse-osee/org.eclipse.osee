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
import { transaction } from '@osee/transactions/types';
import { pipe, Observable, map } from 'rxjs';
export const deleteArtifact = (artId: `${number}`) => {
	return pipe<
		Observable<Required<transaction>>,
		Observable<Required<transaction>>
	>(
		map((tx) => {
			tx.deleteArtifacts.push(artId);
			return tx;
		})
	);
};
