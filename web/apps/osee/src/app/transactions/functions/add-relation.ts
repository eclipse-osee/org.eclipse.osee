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

export const addRelation = (
	tx: Required<transaction>,
	relation: modifyRelation
) => {
	tx.addRelations.push(relation);
	return tx;
};
export const addRelations = (
	tx: Required<transaction>,
	relations: modifyRelation[]
) => {
	relations.forEach((rel) => {
		tx.addRelations.push(rel);
	});
	return tx;
};
