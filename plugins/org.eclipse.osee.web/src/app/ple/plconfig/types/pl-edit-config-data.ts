import { view } from "./pl-config-applicui-branch-mapping";

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
    group = '';
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
    group: string
    productApplicabilities: string[];
}