/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { RELATIONTYPEID } from '@osee/shared/types/constants';

export type transaction = {
	branch: string;
	txComment: string;
	createArtifacts?: createArtifact[];
	modifyArtifacts?: modifyArtifact[];
	deleteArtifacts?: (string | number)[];
	deleteRelations?: modifyRelation[];
	addRelations?: modifyRelation[];
};

export type createArtifact = {
	typeId: string;
	name: string;
	key: string;
	applicabilityId?: string;
	attributes?: createAttributeType[];
	relations?: relation[];
};

export type artifact = {
	id: string;
	type?: string;
	name?: string;
	applicability?: applic;
	attributes?: createAttributeType[];
	relations?: Record<string, relationValue>;
};

export type createAttributeType = {
	typeName?: string;
	typeId?: string;
	value: string | number | boolean | unknown[] | unknown;
};

export type relation = relationValue & (relationById | relationByName);
type relationById = {
	typeId: RELATIONTYPEID;
};

type relationByName = {
	typeName: string;
};

export type relationValue = (
	| relationValueA
	| relationValueB
	| (relationValueA & relationValueB)
) & {
	rationale?: string;
	afterArtifact?: string | 'end' | 'start';
};

type relationValueA = {
	sideA: string | string[];
};

type relationValueB = {
	sideB: string | string[];
};

export type fullRelation = Extract<
	relation,
	{ sideA: string | string[]; sideB: string | string[] }
>;

export type modifyArtifact = {
	id: string;
	applicabilityId?: string;
	setAttributes?: createAttributeType[];
	addAttributes?: createAttributeType[];
	deleteAttributes?: [{ typeName: string }];
};

export type modifyRelation = (relationById | relationByName) &
	relationArtIds & {
		rationale?: string;
		afterArtifact?: string;
	};
type relationaArtId = {
	aArtId: string;
};
type relationbArtId = {
	bArtId: string;
};
type relationArtIds = relationaArtId & relationbArtId;
