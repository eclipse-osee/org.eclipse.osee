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
import { attribute } from '../attribute';
import { iconVariant, twColor, twShade } from '../tw-colors';

export type artifact = {
	name: string;
	id: `${number}`;
	typeId: string;
	typeName: string;
	icon: artifactTypeIcon;
	attributes: attribute[];
	editable: boolean;
};

export type artifactTypeIcon = {
	icon: string;
	color: twColor;
	lightShade: twShade;
	darkShade: twShade;
	variant: iconVariant;
};

export interface relation {
	relationTypeToken: relationTypeToken;
	relationSides: relationSide[];
}

export interface relationSide {
	name: string;
	artifacts: artifact[];
	isSideA: boolean;
	isSideB: boolean;
}

export interface relationTypeToken {
	id: `${number}`;
	idIntValue: number;
	idString: string;
	multiplicity: string;
	name: string;
	newRelationTable: boolean;
	order: string;
	ordered: boolean;
	relationArtifactType: string;
}

export type artifactTokenWithIcon = {
	id: `${number}`;
	name: string;
	icon: artifactTypeIcon;
};

export const artifactSentinel: artifact = {
	id: '-1',
	name: '',
	typeId: '',
	typeName: '',
	icon: {
		icon: '',
		color: '',
		lightShade: '',
		darkShade: '',
		variant: '',
	},
	attributes: [],
	editable: false,
};
