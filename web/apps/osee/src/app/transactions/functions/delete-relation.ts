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

export const deleteRelation = (
	tx: Required<transaction>,
	relation: modifyRelation
) => {
	tx.deleteRelations.push(relation);
	return tx;
};
export const deleteRelations = (
	tx: Required<transaction>,
	relations: modifyRelation[]
) => {
	relations.forEach((rel) => {
		tx.deleteRelations.push(rel);
	});
	return tx;
};
