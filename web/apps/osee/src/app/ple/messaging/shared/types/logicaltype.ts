import type { MIMATTRIBUTETYPEID } from '@osee/messaging/shared/attr';
import type { PlatformTypeAttr } from './platformType';

/*********************************************************************
 * Copyright (c) 2021 Boeing
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
export type logicalType = {
	id: `${number}`;
	name: string;
	idString: string;
	idIntValue: number;
};

export type logicalTypeFormDetail<
	T extends Extract<keyof PlatformTypeAttr, string>,
> = {
	fields: logicalTypeFieldInfo<T>[];
} & logicalType;
export type logicalTypeFieldInfo<
	T extends Extract<keyof PlatformTypeAttr, string>,
> = {
	attributeType: Capitalize<T>; //note: this isn't actually valid typing, but it shuts up typescript about string|undefined !== undefined due to the types of enumSet and 2s complement not being string
	attributeTypeId: MIMATTRIBUTETYPEID;
	editable: boolean;
	name: string;
	required: boolean;
	defaultValue: PlatformTypeAttr[T];
	value?: PlatformTypeAttr[T];
	jsonPropertyName: T;
};
