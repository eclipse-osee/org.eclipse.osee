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
import { NameValuePair } from "./base-types/NameValuePair";

export interface PlConfigApplicUIBranch extends branchListing {
    idIntValue: number,
    name: string,
};
export interface PlConfigBranchListingBranch extends PlConfigApplicUIBranch {
    associatedArtifact: string,
    baselineTx: string,
    parentTx: string,
    parentBranch: branchListing,
    branchState: string,
    branchType: string,
    inheritAccessControl: boolean,
    archived: boolean,
    shortName: string,
}
export class PlConfigBranchListingBranchImpl implements PlConfigBranchListingBranch{
    associatedArtifact: string ='-1';
    baselineTx: string ='';
    parentTx: string ='';
    parentBranch: branchListing = {
        id:'',
        viewId:''
    };
    branchState: string = '-1';
    branchType: string = '-1';
    inheritAccessControl: boolean =false;
    archived: boolean =false;
    shortName: string ='';
    idIntValue: number =-1;
    name: string ='';
    id: string ='-1';
    viewId: string = "-1";
    
}
interface branchListing {
    id: string,
    viewId: string,
}
export interface cfgGroup extends NameValuePair {
}