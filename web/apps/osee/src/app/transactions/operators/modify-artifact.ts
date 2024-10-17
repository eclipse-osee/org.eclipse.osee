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
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import {
	attribute,
	isNewAttr,
	isValidAttr,
	newAttribute,
	validAttribute,
} from '@osee/attributes/types';
import { applic } from '@osee/applicability/types';
import {
	transaction,
	modifyArtifact as _modifyArtifact,
} from '@osee/transactions/types';
import { pipe, Observable, map } from 'rxjs';

type _attrConfig = {
	set?: attribute<
		string | number | boolean | unknown[] | unknown,
		ATTRIBUTETYPEID
	>[];
	add?: attribute<
		string | number | boolean | unknown[] | unknown,
		ATTRIBUTETYPEID
	>[];
	delete?: attribute<
		string | number | boolean | unknown[] | unknown,
		ATTRIBUTETYPEID
	>[];
};
export const modifyArtifact = (
	artId: `${number}`,
	applicability: applic,
	attrConfig: _attrConfig
) => {
	return pipe<
		Observable<Required<transaction>>,
		Observable<Required<transaction>>
	>(
		map((tx) => {
			const _modify: _modifyArtifact = {
				id: artId,
				applicabilityId: applicability.id,
				setAttributes: attrConfig.set
					?.filter((attr) => attr.id !== undefined)
					.filter(
						(
							attr
						): attr is validAttribute<
							string | number | boolean | unknown[] | unknown,
							ATTRIBUTETYPEID
						> => isValidAttr(attr)
					)
					.map((attr) => {
						return {
							id: attr.id,
							typeId: attr.typeId,
							gamma: attr.gammaId,
							value: attr.value,
						};
					}),
				addAttributes: attrConfig.add
					?.filter(
						(
							attr
						): attr is newAttribute<
							string | number | boolean | unknown[] | unknown,
							ATTRIBUTETYPEID
						> => isNewAttr(attr)
					)
					.map((attr) => ({
						typeId: attr.typeId,
						value: attr.value,
					})),
			};
			tx.modifyArtifacts.push(_modify);
			return tx;
		})
	);
};
