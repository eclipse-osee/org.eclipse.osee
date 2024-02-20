import { iconVariant, twColor, twShade } from '@osee/shared/types';

/*********************************************************************
 * Copyright (c) 2023 Boeing
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
export interface artifactWithDirectRelations {
	artId: string;
	artName: string;
	artType: string;
	relations: relation[];
}

export interface relation {
	relationTypeToken: relationTypeToken;
	relationSides: relationSide[];
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

export interface relationSide {
	name: string;
	artifacts: artifact[];
	isSideA: boolean;
	isSideB: boolean;
}

export interface artifact {
	name: string;
	id: `${number}`;
	typeId: string;
	typeName: string;
	icon: artifactTypeIcon;
	attributes: attribute[];
	editable: boolean;
}

export interface attribute {
	name: string;
	value: string;
	typeId: string;
	id: string;
	baseType: string;
}

export type artifactTypeIcon = {
	icon: string;
	color: twColor;
	lightShade: twShade;
	darkShade: twShade;
	variant: iconVariant;
};

export interface tab {
	tabType: TabType;
	tabTitle: string;
	artifact: artifact;
	branchId: string;
	viewId: string;
}

export interface artifactHierarchyOptions {
	showRelations: boolean;
}

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

export const TABTYPES = ['Artifact', 'ChangeReport'] as const;

export type TabType = (typeof TABTYPES)[number];
