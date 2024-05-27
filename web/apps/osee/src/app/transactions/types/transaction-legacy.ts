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
import { applic } from '@osee/applicability/types';

export type legacyTransaction = {
	branch: string;
	txComment: string;
	createArtifacts?: legacyCreateArtifact[];
	modifyArtifacts?: legacyModifyArtifact[];
	deleteArtifacts?: (string | number)[];
	deleteRelations?: legacyModifyRelation[];
	addRelations?: legacyModifyRelation[];
};

export type legacyCreateArtifact = {
	typeId: string;
	name: string;
	key?: string;
	applicabilityId?: string;
	attributes?: legacyAttributeType[];
	relations?: legacyRelation[];
};

export type legacyArtifact = {
	id: string;
	type?: string;
	name?: string;
	applicability?: applic;
	attributes?: legacyAttributeType[];
	relations?: Record<string, legacyRelationValue>;
};

export type legacyAttributeType = {
	typeName?: string;
	typeId?: string;
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	value: string | number | boolean | any[] | unknown;
};

export type legacyRelation = {
	typeId?: string;
	typeName?: string;
} & legacyRelationValue;
export type legacyRelationValue = {
	sideA?: string | string[];
	sideB?: string | string[];
	rationale?: string;
	afterArtifact?: string | 'end' | 'start';
};

export type legacyModifyArtifact = {
	id: string;
	applicabilityId?: string;
	setAttributes?: legacyAttributeType[];
	addAttributes?: legacyAttributeType[];
	deleteAttributes?: [{ typeName: string }];
};
export type legacyModifyRelation = {
	typeName?: string;
	typeId?: string;
	aArtId?: string;
	bArtId?: string;
	rationale?: string;
	afterArtifact?: string;
};
