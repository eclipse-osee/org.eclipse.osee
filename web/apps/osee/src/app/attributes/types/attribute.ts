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

const __newId = '-1' as const;
type _newId = typeof __newId;

type _id = `${number}`;
type _gammaId = _id;
//TODO: think about how to make readonly again?
export type validAttribute<T, U extends ATTRIBUTETYPEID> = {
	id: _id; //used to be string also Omit
	value: T;
	readonly typeId: U;
	readonly gammaId: _gammaId;
};

export type newAttribute<T, U extends ATTRIBUTETYPEID> = {
	id: _newId;
	value: T;
	readonly typeId: U;
	readonly gammaId: _newId;
};

export type invalidCondition<T, U extends ATTRIBUTETYPEID> = {
	id: '';
	//TODO think about signals binding here so that this can go away
	value: T;
	readonly typeId: U;
	readonly gammaId: _gammaId;
};
export type attribute<T, U extends ATTRIBUTETYPEID> =
	| validAttribute<T, U>
	| newAttribute<T, U>
	| invalidCondition<T, U>;

export function isValidAttr<T, U extends ATTRIBUTETYPEID>(
	attr: attribute<T, U> | undefined | null | Required<attribute<T, U>>
): attr is validAttribute<T, U> {
	return (
		attr !== undefined &&
		attr !== null &&
		attr.id !== '' &&
		!isNewAttr(attr)
	);
}
export function isNewAttr<T, U extends ATTRIBUTETYPEID>(
	attr: attribute<T, U>
): attr is newAttribute<T, U> {
	return attr.id === '-1';
}

export function isInvalidAttr<T, U extends ATTRIBUTETYPEID>(
	attr: attribute<T, U>
): attr is invalidCondition<T, U> {
	return attr.id === '';
}
