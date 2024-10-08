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
import { modifyRelation, transaction } from '@osee/transactions/types';
import { pipe, Observable, map, concatMap, from, last } from 'rxjs';

export const addRelation = (relation: modifyRelation) => {
	return pipe<
		Observable<Required<transaction>>,
		Observable<Required<transaction>>
	>(
		map((tx) => {
			tx.addRelations.push(relation);
			return tx;
		})
	);
};
export const addRelations = (relations: modifyRelation[]) => {
	return pipe<
		Observable<Required<transaction>>,
		Observable<Required<transaction>>,
		Observable<Required<transaction>>
	>(
		concatMap((tx) =>
			from(relations).pipe(
				map((rel) => {
					tx.addRelations.push(rel);
					return tx;
				})
			)
		),
		last()
	);
};
