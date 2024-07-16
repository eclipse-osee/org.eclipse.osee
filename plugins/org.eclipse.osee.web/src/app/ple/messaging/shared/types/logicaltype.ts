import type { MIMATTRIBUTETYPEID } from '@osee/messaging/shared/attr';
import type { PlatformType } from './platformType';

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
export interface logicalType {
	id: string;
	name: string;
	idString: string;
	idIntValue: number;
}

export interface logicalTypeFormDetail<T> extends logicalType {
	fields: logicalTypeFieldInfo<T>[];
}
export type logicalTypeFieldInfo<T> = {
	attributeType: Capitalize<
		Readonly<
			Exclude<
				Extract<keyof PlatformType, string>,
				'enumSet' | 'interfacePlatform2sComplement'
			>
		>
	>; //note: this isn't actually valid typing, but it shuts up typescript about string|undefined !== undefined due to the types of enumSet and 2s complement not being string
	attributeTypeId: MIMATTRIBUTETYPEID;
	editable: boolean;
	name: string;
	required: boolean;
	defaultValue: T extends keyof PlatformType
		? PlatformType[Exclude<T, 'enumSet' | 'interfacePlatform2sComplement'>]
		: never;
	value?: T extends keyof PlatformType
		? PlatformType[Exclude<T, 'enumSet' | 'interfacePlatform2sComplement'>]
		: never;
	jsonPropertyName: T extends keyof PlatformType ? T : never;
};
