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
import { applicWithGamma } from '@osee/applicability/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { hasChanges } from '@osee/shared/types/change-report';
import { ARTIFACTTYPEID } from '@osee/shared/types/constants';

export type plConfigTable = {
	table: plconfigTableEntry[];
	headers: configurationValue[];
	headerLengths: number[];
};
export type plconfigTableEntry<T = unknown> = Required<_plconfigTableEntry<T>> &
	_plconfigTableEntryChanges;
type _plconfigTableEntry<T = unknown> = {
	id: `${number}`;
	name: string;
	configurationValues: configurationValue[];
	attributes: PlConfigAttribute<T, ATTRIBUTETYPEID>[];
};

type _plconfigTableEntryChanges = {
	deleted?: boolean;
	added?: boolean;
	changes?: __plconfigTableEntryChanges;
};
type __plconfigTableEntryChanges = {} & hasChanges<{
	name: string;
	description: string;
	productApplicabilities: string[];
	multiValued: boolean;
	valueType: string;
	defaultValue: string;
}>;

export type configurationValue = Required<_configurationValue> &
	_configurationValueChanges;
type _configurationValue = {
	id: `${number}`;
	name: string;
	gammaId: string;
	applicability: applicWithGamma;
	typeId: ARTIFACTTYPEID;
};
type _configurationValueChanges = {
	deleted?: boolean;
	added?: boolean;
	changes?: __configurationValueChanges;
};
type __configurationValueChanges = hasChanges<{
	name: string;
	description: string;
	applicability: applicWithGamma;
}>;

// TODO remove when the full gamma id solution arrives and replace...
// this set of attribute types should be 1:1 with that solution
//eslint-disable-next-line @typescript-eslint/no-unused-vars
const newId = '-1' as const;
type _newId = typeof newId;

type _id = `${number}`;
export type validPLConfigAttribute<T, U extends ATTRIBUTETYPEID> = {
	readonly id: _id; //used to be string also Omit
	value: T;
	readonly attributeType: U;
	readonly gammaId: _id;
};

export type newPLConfigAttribute<T, U extends ATTRIBUTETYPEID> = {
	readonly id: _newId;
	value: T;
	readonly attributeType: U;
	readonly gammaId: _newId;
};

export type invalidPLConfigAttributeCondition = {
	readonly id: '';
};
export type PlConfigAttribute<T, U extends ATTRIBUTETYPEID> =
	| validPLConfigAttribute<T, U>
	| newPLConfigAttribute<T, U>
	| invalidPLConfigAttributeCondition;

export function isValidPLConfigAttr<T, U extends ATTRIBUTETYPEID>(
	attr: PlConfigAttribute<T, U>
): attr is validPLConfigAttribute<T, U> {
	return attr.id !== '' && !isNewPLConfigAttr(attr);
}
export function isNewPLConfigAttr<T, U extends ATTRIBUTETYPEID>(
	attr: PlConfigAttribute<T, U>
): attr is newPLConfigAttribute<T, U> {
	return attr.id === '-1';
}

export function isInvalidPLConfigAttr<T, U extends ATTRIBUTETYPEID>(
	attr: PlConfigAttribute<T, U>
): attr is invalidPLConfigAttributeCondition {
	return attr.id === '';
}
