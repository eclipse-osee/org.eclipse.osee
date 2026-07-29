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
	isNewAttr,
	isValidAttr,
	newAttribute,
	validAttribute,
} from '@osee/attributes/types';
import {
	attrConfig,
	modifyArtifact as _modifyArtifact,
	transaction,
} from '@osee/transactions/types';

export const modifyArtifact = (
	tx: Required<transaction>,
	artId: `${number}`,
	applicability: applic,
	attrConfig: attrConfig
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
		deleteAttributes: attrConfig.delete
			?.filter((attr) => isValidAttr(attr))
			.map((attr) => ({
				id: (attr as validAttribute<unknown, ATTRIBUTETYPEID>).id,
			})),
	};
	tx.modifyArtifacts.push(_modify);
	return tx;
};
