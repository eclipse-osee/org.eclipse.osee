export interface MimPreferences {
    id: string,
    name: string,
    columnPreferences: MimColumnPreference[],
    inEditMode: boolean,
    hasBranchPref:boolean
}

export interface MimColumnPreference{
    enabled: boolean,
    name:string
}