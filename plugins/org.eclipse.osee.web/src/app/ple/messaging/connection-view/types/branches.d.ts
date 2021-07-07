export interface messageBranch {
    idIntValue: number,
    name: string,
    associatedArtifact: string,
    baselineTx: string,
    parentTx: string,
    parentBranch: {
        id: string,
        viewId: string,
    },
    branchState: string,
    branchType: string,
    inheritAccessControl: boolean,
    archived: boolean,
    shortName: string,
    id: string,
    viewId: string,
}