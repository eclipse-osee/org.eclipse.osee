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
import type { Edge } from '@swimlane/ngx-graph';
import type { difference } from '@osee/shared/types/change-report';
import type { applic } from '@osee/shared/types/applicability';
import type { transportType } from './transportType';

export interface connection extends connectionAttributes, connectionRelations {
	id?: string;
	dashed?: boolean;
	applicability?: applic;
}

interface connectionAttributes {
	name: string;
	description: string;
}

interface connectionRelations {
	transportType: transportType;
}
export interface connectionWithChanges extends connection {
	deleted: boolean;
	added: boolean;
	changes: {
		name?: difference;
		description?: difference;
		transportType?: difference;
		applicability?: difference;
	};
}

export interface _newConnection
	extends connectionAttributes,
		Partial<connectionRelations> {
	applicability?: applic;
}
export interface newConnection {
	connection: _newConnection;
	nodeId: string;
}

export interface OseeEdge<T> extends Omit<Edge, 'data'> {
	data: T;
}
