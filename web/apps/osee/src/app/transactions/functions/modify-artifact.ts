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
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import {
	attribute,
	isNewAttr,
	isValidAttr,
	newAttribute,
	validAttribute,
} from '@osee/attributes/types';
import {
	modifyArtifact as _modifyArtifact,
	transaction,
} from '@osee/transactions/types';

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
	tx: Required<transaction>,
	artId: `${number}`,
	applicability: applic,
	attrConfig: _attrConfig
) => {
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
		addAttributes: attrConfig.add?.filter(
			(
				attr
			): attr is newAttribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			> => isNewAttr(attr)
		),
	};
	tx.modifyArtifacts.push(_modify);
	return tx;
};
