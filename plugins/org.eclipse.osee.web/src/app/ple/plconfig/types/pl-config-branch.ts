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
import { branch, branchHeader, branchInfo } from '../../../types/branches/branch';
import { NameValuePair } from "./base-types/NameValuePair";

export interface PlConfigApplicUIBranch extends branchInfo {
};
export interface PlConfigBranchListingBranch extends branch {
}
export class PlConfigBranchListingBranchImpl implements PlConfigBranchListingBranch{
    associatedArtifact ='-1';
    baselineTx ='';
    parentTx ='';
    parentBranch = {
        id:'',
        viewId:''
    };
    branchState = '-1';
    branchType = '-1';
    inheritAccessControl =false;
    archived =false;
    shortName ='';
    idIntValue =-1;
    name ='';
    id ='-1';
    viewId = "-1";
    
}

export interface cfgGroup extends NameValuePair {
}