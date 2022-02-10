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
import { applic } from "../applicability/applic";
import { ARTIFACTTYPEID } from "../constants/ArtifactTypeId.enum";
import { ATTRIBUTETYPEID } from "../constants/AttributeTypeId.enum";
import { RelationTypeId } from "../constants/RelationTypeId.enum";
import { TupleTypeId } from "../constants/TupleTypeId";
import { transactionToken } from "./transaction-token";

/**
 * Contains information on changes that have occurred on a branch, appears in an array on /orcs/branches/branch1/diff/branch2
 */
export class changeInstance<T=string | number | boolean | null | undefined,U=string | number | boolean | null | undefined,V=string | number | boolean | null | undefined,W=string | number | boolean | null | undefined,X=string | number | boolean | null | undefined>{
    ignoreType: ignoreType;
    changeType: changeType;
    artId: string;
    itemId: string;
    itemTypeId: ARTIFACTTYPEID|ATTRIBUTETYPEID|RelationTypeId|TupleTypeId|itemTypeIdRelation|string;// update to enum union later, should be artifact type id, attribute type id, relation type id
    baselineVersion: version<T>; //information available at beginning of branch
    firstNonCurrentChange: version<U>; // doesn't seem useful for web apps as of yet
    currentVersion: version<V>; //latest instance of information on branch
    destinationVersion: version<W>; //information currently available on parent branch
    netChange: version<X>; //total change
    synthetic: boolean; //only needed on client so far
    artIdB: string; //only needed on client so far
    deleted: boolean;
    applicabilityCopy: boolean; //only needed on client so far
    
}
export const enum ignoreType{
    NONE = "NONE",
    DELETED_AND_DNE_ON_DESTINATION="DELETED_AND_DNE_ON_DESTINATION"
}

/**
 * Enum containing the types of modification that occurred with a particular change
 */
export const enum ModificationType{
    NONE = "-1",
    NEW = "1",
    MODIFIED = "2",
    DELETED = "3",
    MERGED = "4",
    ARTIFACT_DELETED = "5",
    INTRODUCED = "6",
    UNDELETED = "7",
    REPLACED_WITH_VERSION = "8",
    DELETED_ON_DESTINATION = "9",
    APPLICABILITY="10"
}

/**
 * Contains information regarding the type of change occurring, i.e. whether it is an attribute change, artifact change, relation change,  etc.
 */
export class changeType{
    id: changeTypeNumber; //really a number
    name: changeTypeEnum; //actual change type
    typeId: number; //2834799904 data at top of ChangeType.java no idea if any use
    notRelationChange: boolean; //change is not a relation change
    notAttributeChange: boolean; //change is not an attribute change
    idIntValue: number; //redundant
    idString: string; //redundant
}
export const enum changeTypeEnum {
    ATTRIBUTE_CHANGE = "AttributeChange",
    ARTIFACT_CHANGE = "ArtifactChange",
    RELATION_CHANGE = "RelationChange",
    TUPLE_CHANGE= "TupleChange"
}

export const enum changeTypeNumber{
    ARTIFACT_CHANGE="111",
    ATTRIBUTE_CHANGE = "222",
    RELATION_CHANGE = "333",
    TUPLE_CHANGE = "444"
}
/**
 * Useful info includes the {@link transactionToken transaction}, value, uri,whether or not the state is valid, and the {@link applic applicability token}
 */
export class version<T=string | number | boolean | null | undefined> {
    transactionToken: transactionToken;
    gammaId: string|null;
    modType: ModificationType;
    value: T;
    uri: string;
    valid: boolean;
    applicabilityToken:applic|null
}
export interface difference<T=string | number | boolean | applic | null | undefined>{
    user?: string,
    timeOfChange?: string,
    previousValue: T,
    currentValue: T,
    transactionToken:transactionToken
}
export class itemTypeIdRelation{
    id: RelationTypeId;
    name: string;
    order: string;
    ordered: boolean;
    multiplicity: string;
    idString: string;
    idIntValue: number;
}