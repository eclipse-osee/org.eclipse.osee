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
import { applicabilitySentinel, type applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import type { hasChanges } from '@osee/shared/types/change-report';
import type { Edge } from '@swimlane/ngx-graph';
import { nodeData } from './node';
import { TransportType, type transportType } from './transportType';

export type _newConnection = {
	applicability?: applic;
} & connectionAttributes &
	Partial<connectionRelations>;
export type connection = {
	id: `${number}`;
	gammaId: `${number}`;
	dashed?: boolean;
} & _connectionApplic &
	connectionAttributes &
	connectionRelations &
	_connectionChanges;
type _connectionApplic = {
	applicability: applic;
};
type connectionAttributes = {
	name: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>;
	description: attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>;
};

type connectionRelations = {
	transportType: transportType;
	nodes: nodeData[];
};

type _connectionChanges = {
	deleted?: boolean;
	added?: boolean;
	changes?: __connectionChanges;
};
export type __connectionChanges = {} & hasChanges<connectionAttributes> &
	hasChanges<_connectionApplic> &
	hasChanges<connectionRelations>;

export type OseeEdge<T> = {
	data: T;
} & Omit<Edge, 'data'>;

export const connectionSentinel: connection = {
	id: '-1',
	gammaId: '-1',
	name: {
		id: '-1',
		typeId: '1152921504606847088',
		gammaId: '-1',
		value: '',
	},
	description: {
		id: '-1',
		typeId: '1152921504606847090',
		gammaId: '-1',
		value: '',
	},
	nodes: [],
	transportType: { ...new TransportType(), directConnection: false },
	applicability: applicabilitySentinel,
};
