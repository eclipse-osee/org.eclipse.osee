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
/**
 * Response the OSEE API should return when doing a POST,PUT,PATCH, or DELETE to indicate success/status of API
 */
 export interface OSEEWriteApiResponse {
    empty: boolean,
    errorCount: number,
    errors: boolean,
    failed: boolean,
    ids: string[],
    infoCount: number,
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