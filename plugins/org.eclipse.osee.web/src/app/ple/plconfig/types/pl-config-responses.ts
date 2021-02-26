export interface response {
    empty: boolean,
    errorCount: number,
    errors: boolean,
    failed: boolean,
    ids: [],
    infoCount: number,
    numErrors: number,
    numErrorsViaSearch: number,
    numWarnings: number,
    numWarningsViaSearch: number,
    results: Array<string>,
    success: boolean,
    tables: [],
    title: string | null,
    txId: string,
    warningCount: number,
}
export interface commitResponse {
    tx: transaction,
    results: response,
    success: boolean,
    failed:boolean,
    
}
export interface transitionResponse {
    cancelled: boolean,
    workItemIds: [],
    results: Array<string>,
    transitionWorkItems: [],
    transaction: transaction,
    empty:true
}
interface transaction {
    branchId: string,
    id:string,
}