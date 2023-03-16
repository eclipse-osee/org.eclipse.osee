/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Observable } from 'rxjs';
import { transactionResult } from '@osee/shared/types/change-report';
import { CurrentTextEditorService } from '../services/current-text-editor.service';

let bogusObservable$ = new Observable<string>();
let bogusTransactionResultObservable$ = new Observable<transactionResult>();

export const currentTextEditorServiceMock: Partial<CurrentTextEditorService> = {
	get mdContent() {
		return bogusObservable$;
	},

	get userInput() {
		return bogusObservable$;
	},

	set userInputValue(value: string) {
		// do nothing
	},

	get updateArtifact() {
		return bogusTransactionResultObservable$;
	},
};
