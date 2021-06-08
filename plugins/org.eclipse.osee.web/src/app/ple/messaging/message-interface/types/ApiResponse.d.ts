/**
 * Response the Platform Types API should return when doing a POST,PUT,PATCH, or DELETE to indicate success/status of API
 */
 export interface MessageApiResponse {
    empty: boolean,
    errorCount: number,
    errors: boolean,
    failed: boolean,
    ids: string[],
    infoCount: boolean,
    numErrors: number,
    numErrorsViaSearch: number,
    numWarnings: number,
    numWarningsViaSearch: number,
    results: string[],
    success: boolean,
    tables: string[],
    title: string,
    txId: string,
    warningCount: number
}