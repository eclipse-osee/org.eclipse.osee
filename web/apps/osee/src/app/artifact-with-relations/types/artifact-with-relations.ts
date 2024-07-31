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
import { attribute, iconVariant, twColor, twShade } from '@osee/shared/types';

export type artifactWithRelations = {
	name: string;
	id: `${number}`;
	typeId: string;
	typeName: string;
	icon: artifactTypeIcon;
	attributes: attribute[];
	relations: artifactRelation[];
	editable: boolean;
	operationTypes: operationType[];
};

export type artifactTypeIcon = {
	icon: string;
	color: twColor;
	lightShade: twShade;
	darkShade: twShade;
	variant: iconVariant;
};

export interface artifactRelation {
	relationTypeToken: relationTypeToken;
	relationSides: artifactRelationSide[];
}

export interface artifactRelationSide {
	name: string;
	artifacts: artifactWithRelations[];
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

export interface operationType {
	id: `${number}`;
	name: string;
	description: string;
	materialIcon: artifactTypeIcon;
}

export const operationTypeMock: operationType = {
	id: '1',
	name: '',
	description: '',
	materialIcon: {
		icon: '',
		color: '',
		lightShade: '',
		darkShade: '',
		variant: '',
	},
};

export type artifactTokenWithIcon = {
	id: `${number}`;
	name: string;
	icon: artifactTypeIcon;
};

export const artifactWithRelationsSentinel: artifactWithRelations = {
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
	relations: [],
	editable: false,
	operationTypes: [],
};
