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
import type { difference } from '@osee/shared/types/change-report';
import type { applic } from '@osee/shared/types/applicability';
import type { Node } from '@swimlane/ngx-graph';

export interface nodeData {
	id: string;
	name: string;
	description?: string;
	interfaceNodeNumber: string;
	interfaceNodeGroupId: string;
	interfaceNodeBackgroundColor: string;
	interfaceNodeAddress: string;
	applicability?: applic;
	interfaceNodeBuildCodeGen: boolean;
	interfaceNodeCodeGen: boolean;
	interfaceNodeCodeGenName: string;
	nameAbbrev: string;
	interfaceNodeToolUse: boolean;
	interfaceNodeType: string;
	notes: string;
}

export interface nodeDataWithChanges extends nodeData {
	deleted: boolean;
	changes: nodeChanges;
}

export interface nodeChanges {
	name?: difference;
	description?: difference;
	interfaceNodeNumber?: string;
	interfaceNodeGroupId?: string;
	interfaceNodeBackgroundColor?: difference;
	interfaceNodeAddress?: difference;
	applicability?: difference;
	interfaceNodeBuildCodeGen?: difference;
	interfaceNodeCodeGen?: difference;
	interfaceNodeCodeGenName?: difference;
	nameAbbrev?: difference;
	interfaceNodeToolUse?: difference;
	interfaceNodeType?: difference;
	notes?: difference;
}

export interface node {
	id?: string;
	name: string;
	description?: string;
	applicability?: applic;
}

export interface OseeNode<T> extends Omit<Node, 'data'> {
	data: T;
}
