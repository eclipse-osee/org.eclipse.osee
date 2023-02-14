import type { CrossReference } from './crossReference.d ';
import type { messageToken } from './messages';
import type { subMessage } from './sub-messages';
import type { elementImportToken } from './element';
import type { enumeration, enumSet } from './enum';
import type { nodeToken } from './node';
import type { platformTypeImportToken } from './platformType';
import type { structure } from './structure';

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
	connectionId: string;
	primaryNode: nodeToken;
	secondaryNode: nodeToken;
	messages: messageToken[];
	subMessages: subMessage[];
	structures: structure[];
	elements: elementImportToken[];
	platformTypes: platformTypeImportToken[];
	enumSets: enumSet[];
	enums: enumeration[];
	crossReferences: CrossReference[];
	messageSubmessageRelations: importRelationMap;
	subMessageStructureRelations: importRelationMap;
	structureElementRelations: importRelationMap;
	elementPlatformTypeRelations: importRelationMap;
	platformTypeEnumSetRelations: importRelationMap;
	enumSetEnumRelations: importRelationMap;
	connectionCrossReferenceRelations: importRelationMap;
}

export interface importRelationMap {
	[key: string]: string[];
}

export interface ImportOption {
	id: string;
	name: string;
	url: string;
	transportType: string;
	connectionRequired: boolean;
}

export interface ImportEnumSet extends enumSet {
	enums: string[];
}
