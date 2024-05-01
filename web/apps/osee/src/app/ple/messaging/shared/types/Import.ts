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
import type { CrossReference } from './crossReference';
import type { messageToken } from './messages';
import type { subMessage } from './sub-messages';
import type { elementImportToken } from './element';
import type { enumeration, enumSet } from './enum';
import type { nodeData } from './node';
import type { platformTypeImportToken } from './platformType';
import type { structure } from './structure';
import type { connection } from '@osee/messaging/shared/types';

export interface ImportSummary {
	nodes: nodeData[];
	connections: connection[];
	messages: messageToken[];
	subMessages: subMessage[];
	structures: structure[];
	elements: elementImportToken[];
	platformTypes: platformTypeImportToken[];
	enumSets: enumSet[];
	enums: enumeration[];
	crossReferences: CrossReference[];
	connectionNodeRelations: importRelationMap;
	connectionMessageRelations: importRelationMap;
	messagePublisherNodeRelations: importRelationMap;
	messageSubscriberNodeRelations: importRelationMap;
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
