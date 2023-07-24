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
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class ErrorService {
	private _errorText = new BehaviorSubject<string>('');
	private _errorDetails = new BehaviorSubject<string>('');

	public get errorText() {
		return this._errorText;
	}

	public get errorDetails() {
		return this._errorDetails;
	}

	setHttpError(error: HttpErrorResponse) {
		this._errorText.next(
			'Request failed: ' + error.statusText + ' ' + error.status
		);
		this._errorDetails.next(error.message);
	}

	setError(errorText: string, errorDetails: string) {
		this._errorText.next(errorText);
		this._errorDetails.next(errorDetails);
	}

	clearError() {
		this._errorText.next('');
		this._errorDetails.next('');
	}

	constructor() {}
}
