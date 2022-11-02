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
import { MessageApiResponse } from '../../types/ApiResponse';

export const messageResponseMock: MessageApiResponse = {
	empty: false,
	errorCount: 0,
	errors: false,
	failed: false,
	ids: [],
	infoCount: 0,
	numErrors: 0,
	numErrorsViaSearch: 0,
	numWarnings: 0,
	numWarningsViaSearch: 0,
	results: [],
	success: false,
	tables: [],
	title: '',
	txId: '',
	warningCount: 0,
};
