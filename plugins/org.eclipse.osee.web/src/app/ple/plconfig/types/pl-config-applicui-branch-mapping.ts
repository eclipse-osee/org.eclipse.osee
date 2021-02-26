import { NameValuePair } from "./base-types/NameValuePair";
import { showable } from "./base-types/showable";
import { extendedFeature } from "./features/base";
import { PlConfigApplicUIBranch } from "./pl-config-branch";
import { configurationGroup } from "./pl-config-configurations";

export interface PlConfigApplicUIBranchMapping {
    associatedArtifactId: string,
    branch: PlConfigApplicUIBranch,
    editable: boolean,
    features: extendedFeature[],
    groups: configurationGroup[],
    parentBranch: PlConfigApplicUIBranch,
    views: view[],
};
export class PlConfigApplicUIBranchMappingImpl implements PlConfigApplicUIBranchMapping{
    associatedArtifactId: string ='-1';
    branch: PlConfigApplicUIBranch = {
        idIntValue: 0,
        name: '',
        id: '0',
        viewId:"-1"
    };
    editable: boolean =false;
    features: extendedFeature[]=[];
    groups: configurationGroup[]=[];
    parentBranch: PlConfigApplicUIBranch= {
        idIntValue: 0,
        name: '',
        id: '0',
        viewId:"-1"
    };
    views: view[]=[];
    
}

export interface ConfigGroup extends NameValuePair, showable {
}
export interface view extends NameValuePair, showable {
    hasFeatureApplicabilities: boolean,
    productApplicabilities?:string[],
}