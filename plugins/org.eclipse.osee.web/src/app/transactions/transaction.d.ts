import { applic } from '../ple/messaging/shared/types/NamedId.applic';

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
  value: string | number | boolean | string[];
}

export interface relation extends relationValue {
  typeId?: string;
  typeName?: string;
}
export interface relationValue {
  sideA?: string | string[];
  sideB?: string | string[];
  rationale?: string;
}

export interface modifyArtifact {
  id: string;
  applicabilityId?: string;
  setAttributes?: attributeType[];
  addAttributes?: {
    [x: string]: string | number | boolean | string[];
  };
  deleteAttributes?: {
    [x: string]: string | number | boolean | string[];
  };
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
