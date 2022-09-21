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
import { applic } from '../types/applicability/applic';

export interface transaction {
  branch: string;
  txComment: string;
  createArtifacts?: createArtifact[];
  modifyArtifacts?: modifyArtifact[];
  deleteArtifacts?: Array<string | number>;
  deleteRelations?: modifyRelation[];
  addRelations?: modifyRelation[];
}

export interface createArtifact {
  typeId: string;
  name: string;
  key?: string;
  applicabilityId?: string;
  attributes?: attributeType[];
  relations?: relation[];
}

export interface artifact {
  id: string;
  type?: string;
  name?: string;
  applicability?: applic;
  attributes?: attributeType[];
  relations?: {
    [x: string]: relationValue;
  };
}

export interface attributeType {
  typeName: string;
  value: string | number | boolean | any[] | unknown;
}

export interface relation extends relationValue {
  typeId?: string;
  typeName?: string;
}
export interface relationValue {
  sideA?: string | string[];
  sideB?: string | string[];
  rationale?: string;
  afterArtifact?: string | "end" | "start";
}

export interface modifyArtifact {
  id: string;
  applicabilityId?: string;
  setAttributes?: attributeType[];
  addAttributes?: 
    attributeType[];
  deleteAttributes?: [{typeName:string}]
}

export interface transactionToken {
  id: string;
  branchId: string;
}

export interface modifyRelation {
  typeName?: string;
  typeId?: string;
  aArtId?: string;
  bArtId?: string;
  rationale?: string;
}
