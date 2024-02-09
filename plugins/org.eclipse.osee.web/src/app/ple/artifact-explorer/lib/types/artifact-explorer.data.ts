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
export const DEFAULT_HIERARCHY_ARTIFACT_ID: `${number}` = '197818';

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
	value: AttributeValue;
	typeId: string;
	id: string;
	storeType: string;
	multiplicityId: string;
}

export type AttributeValue = string | Date;

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

export class artifactToCreate {
	public name = '';
	public artifactTypeId = '0';
	public parentArtifactId = '0';
	public attributes: attribute[] = [];

	constructor(parentArtifactId: `${number}`) {
		this.parentArtifactId = parentArtifactId;
	}
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
