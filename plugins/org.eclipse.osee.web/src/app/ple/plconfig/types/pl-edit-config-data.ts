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
import { ConfigGroup, view } from "./pl-config-applicui-branch-mapping";

export class PLEditConfigData implements ConfigData {
    constructor(branch?: string,currentConfig?:view,ConfigurationToCopyFrom?:view, productApplicabilities?:string[], editable?:boolean) {
        if (branch) {
            this.currentBranch = branch;
        }
        if (currentConfig) {
            this.currentConfig = currentConfig;
        }
        if (ConfigurationToCopyFrom) {
            this.copyFrom=ConfigurationToCopyFrom;
        }
        if (productApplicabilities) {
            this.productApplicabilities = productApplicabilities;
        }
        if (editable) {
            this.editable = editable;
        }
    }
    productApplicabilities: string[]=[];
    currentBranch = '';
    currentConfig = { id: '', name: '' };
    copyFrom = { id: '', name: '', hasFeatureApplicabilities:false };
    group = { id: '', name: '' };
    editable: boolean = false;
}
export interface copyFrom {
    copyFrom: number,
}
export interface PLAddConfigData extends ConfigData {
    title: string,
}
interface ConfigData {
    currentBranch: string | undefined,
    copyFrom: view
    group: ConfigGroup
    productApplicabilities: string[];
}