import { view } from "./pl-config-applicui-branch-mapping";

export interface addCfgGroup{
    title:string
}

export interface CfgGroupDialog{
    configGroup: {
        name: string,
        id:string,
        views:view[]
    },
    editable:boolean,
}
export interface ConfigurationGroupDefinition {
    id?: string,
    name: string,
    configurations?:string[]
}