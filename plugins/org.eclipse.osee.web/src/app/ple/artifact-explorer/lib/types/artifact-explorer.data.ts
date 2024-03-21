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
import { attribute, iconVariant, twColor, twShade } from '@osee/shared/types';

export const DEFAULT_HIERARCHY_ROOT_ARTIFACT_ID: `${number}` = '197818';

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

export type artifactTokenWithIcon = {
	id: `${number}`;
	name: string;
	icon: artifactTypeIcon;
};

export interface artifactHierarchyOptions {
	showRelations: boolean;
}

export interface createChildArtifactDialogData {
	name: string;
	artifactTypeId: string;
	parentArtifactId: string;
	attributes: attribute[];
	option: artifactContextMenuOption;
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

export type TabType = 'Artifact' | 'ChangeReport';

export interface artifactContextMenuOption {
	name: string;
	icon: artifactTypeIcon;
	excludedArtifactTypes: `${number}`[];
}

export interface deleteArtifactDialogData {
	artifact: artifact;
	option: artifactContextMenuOption;
}
