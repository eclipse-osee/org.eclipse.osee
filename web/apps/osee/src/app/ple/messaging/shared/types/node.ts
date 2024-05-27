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
import type { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import type { hasChanges } from '@osee/shared/types/change-report';
import type { Node } from '@swimlane/ngx-graph';

export type nodeData = {
	id: `${number}`;
	gammaId: `${number}`;
} & Required<_nodeAttributes> &
	Required<_nodeApplic> &
	Required<_nodeRelations> &
	_nodeChanges;
type _nodeApplic = {
	applicability: applic;
};

type _nodeAttributes = {
	name: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>;
	description: attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>;
	interfaceNodeNumber: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODENUMBER
	>;
	interfaceNodeGroupId: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODEGROUPID
	>;
	interfaceNodeBackgroundColor: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODEBACKGROUNDCOLOR
	>;
	interfaceNodeAddress: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODEADDRESS
	>;
	applicability: applic;
	interfaceNodeBuildCodeGen: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODEBUILDCODEGEN
	>;
	interfaceNodeCodeGen: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODECODEGEN
	>;
	interfaceNodeCodeGenName: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODECODEGENNAME
	>;
	nameAbbrev: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAMEABBREV>;
	interfaceNodeToolUse: attribute<
		boolean,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODETOOLUSE
	>;
	interfaceNodeType: attribute<
		string,
		typeof ATTRIBUTETYPEIDENUM.INTERFACENODETYPE
	>;
	notes: attribute<string, typeof ATTRIBUTETYPEIDENUM.NOTES>;
};
type _nodeRelations = object;

type _nodeChanges = {
	deleted?: boolean;
	added?: boolean;
	changes?: __nodeChanges;
};
type __nodeChanges = {} & hasChanges<_nodeAttributes> &
	hasChanges<_nodeApplic> &
	hasChanges<_nodeRelations>;
export type OseeNode<T> = {
	data: T;
} & Omit<Node, 'data'>;
