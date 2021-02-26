
export interface configuration {
    name: string,
    copyFrom?: string,
    configurationGroup?: string,
    productApplicabilities?:string[]
}
export interface editConfiguration extends configuration {
    configurationGroup?: string
}
export interface configurationGroup {
    configurations:string[],
    id: string,
    name: string,
    hasFeatureApplicabilities: boolean
    productApplicabilities:string[]
}