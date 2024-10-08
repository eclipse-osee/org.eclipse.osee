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
import { difference } from '@osee/shared/types/change-report';
import {
	ExtendedNameValuePair,
	ExtendedNameValuePairWithChanges,
} from '../base-types/ExtendedNameValuePair';

export type feature = {
	name: string;
	description: string;
	valueType: string;
	valueStr?: string;
	defaultValue: string;
	productAppStr?: string;
	values: string[];
	productApplicabilities: string[];
	multiValued: boolean;
	setValueStr(): void;
	setProductAppStr(): void;
};
export type trackableFeature = {
	id: string;
	idIntValue?: number;
	idString?: string;
	type: null | undefined;
} & feature;
export type extendedFeature = {
	configurations: (
		| ExtendedNameValuePair
		| ExtendedNameValuePairWithChanges
	)[];
} & trackableFeature;
export type extendedFeatureWithChanges = {
	added: boolean;
	deleted: boolean;
	changes: {
		name?: difference;
		description?: difference;
		defaultValue?: difference;
		multiValued?: difference;
		productApplicabilities?: difference[];
		valueType?: difference;
		values?: difference[];
		configurations?: {
			name: difference;
			value: difference;
			values: difference[];
		}[];
	};
} & extendedFeature;
