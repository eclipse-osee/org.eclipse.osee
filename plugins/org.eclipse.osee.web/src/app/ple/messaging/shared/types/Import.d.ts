import { messageToken } from './messages';
import { subMessage } from './sub-messages';
import { elementImportToken } from './element';
import { enumeration, enumSet } from './enum';
import { nodeToken } from './node';
import { platformTypeImportToken } from './platformType';
import { structure } from './structure';

/*********************************************************************
 * Copyright (c) 2022 Boeing
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
export interface ImportSummary {
	createPrimaryNode: boolean;
	createSecondaryNode: boolean;
	primaryNode: nodeToken;
	secondaryNode: nodeToken;
	messages: messageToken[];
	subMessages: subMessage[];
	structures: structure[];
	elements: elementImportToken[];
	platformTypes: platformTypeImportToken[];
	enumSets: enumSet[];
	enums: enumeration[];
	messageSubmessageRelations: importRelationMap;
	subMessageStructureRelations: importRelationMap;
	structureElementRelations: importRelationMap;
	elementPlatformTypeRelations: importRelationMap;
	platformTypeEnumSetRelations: importRelationMap;
	enumSetEnumRelations: importRelationMap;
}

export interface importRelationMap {
	[key: string]: string[];
}

export interface ImportOption {
	id: string;
	name: string;
	url: string;
	transportType: string;
}

export interface ImportEnumSet extends enumSet {
	enums: string[];
}
