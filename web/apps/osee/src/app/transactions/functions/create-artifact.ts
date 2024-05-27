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
import {
	ATTRIBUTETYPEID,
	ATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';
import {
	attribute,
	isInvalidAttr,
	isNewAttr,
	newAttribute,
} from '@osee/attributes/types';
import { ARTIFACTTYPEID } from '@osee/shared/types/constants';
import {
	createArtifact as _createArtifact,
	createAttributeType,
	relation,
	transaction,
} from '@osee/transactions/types';

export const createArtifact = (
	tx: Required<transaction>,
	artTypeId: ARTIFACTTYPEID,
	applicability: applic,
	relations: relation[],
	optionalKey?: string,
	...attributes: attribute<
		string | number | boolean | unknown[] | unknown,
		ATTRIBUTETYPEID
	>[]
) => {
	if (
		!attributes
			.filter(
				(
					v
				): v is newAttribute<
					string | number | boolean | unknown[] | unknown,
					ATTRIBUTETYPEID
				> => isNewAttr(v) && !isInvalidAttr(v)
			)
			.map((v) => v.typeId)
			.includes(ATTRIBUTETYPEIDENUM.NAME)
	) {
		throw new Error('Name Not Defined for Artifact');
	}
	const nameIndex = attributes
		.filter(
			(
				v
			): v is newAttribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			> => isNewAttr(v) && !isInvalidAttr(v)
		)
		.map((v) => v.typeId)
		.indexOf(ATTRIBUTETYPEIDENUM.NAME);
	//note: we are assuming that other users of this function are not overriding the typesafety of having a defined AttributeTypeId, otherwise, we would need to check that they are within the attribute type id list
	const _attributes: createAttributeType[] = attributes
		.filter(
			(
				v
			): v is newAttribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			> => isNewAttr(v) && !isInvalidAttr(v)
		)
		.filter((v) => v.typeId !== ATTRIBUTETYPEIDENUM.NAME)
		.map((v) => {
			return {
				typeId: v.typeId,
				value: v.value,
			};
		});

	_attributes.forEach((v) => {
		if (v.typeId === '-1' || v.typeId === '' || v.typeId === undefined) {
			throw new Error(`${v.value} has no attribute type id defined.`);
		}
	});
	const key = crypto.randomUUID();
	/**
	 * @TODO relations
	 */
	const _newArtifact: _createArtifact = {
		typeId: artTypeId,
		name: attributes.filter(
			(
				v
			): v is newAttribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			> => isNewAttr(v) && !isInvalidAttr(v)
		)[nameIndex].value as string,
		key: optionalKey ?? key,
		attributes: _attributes,
		applicabilityId: applicability?.id ?? '1',
		relations: relations,
	};
	tx.createArtifacts.push(_newArtifact);
	return { tx, _newArtifact };
};
