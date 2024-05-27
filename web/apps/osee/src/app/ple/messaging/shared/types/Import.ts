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
import type { connection } from '@osee/messaging/shared/types';
import type { CrossReference } from './crossReference';
import type { element } from './element';
import type { enumeration, enumerationSet } from './enum';
import type { message } from './messages';
import type { nodeData } from './node';
import type { PlatformType } from './platformType';
import type { structure } from './structure';
import type { subMessage } from './sub-messages';

export type ImportSummary = {
	nodes: nodeData[];
	connections: connection[];
	messages: message[];
	subMessages: subMessage[];
	structures: structure[];
	elements: element[];
	platformTypes: PlatformType[];
	enumSets: enumerationSet[];
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
};

export type importRelationMap = Record<string, string[]>;

export type ImportOption = {
	id: string;
	name: string;
	url: string;
	connectionRequired: boolean;
	transportTypeRequired: boolean;
};

export type ImportEnumSet = {
	enums: string[];
} & enumerationSet;
