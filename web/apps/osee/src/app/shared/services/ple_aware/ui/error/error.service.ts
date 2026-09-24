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

	/**
	 * Timestamp (ms) until which HTTP error popups are suppressed. Set during the SSE
	 * reconnect/resync window, where refetches against a not-yet-ready server can transiently fail
	 * (e.g. 500 during warmup). Those failures are expected, retried locally, and already conveyed
	 * by the connection indicator, so surfacing a modal "Request failed" popup would be noise. The
	 * errors themselves still propagate to callers; only the global popup is muted.
	 */
	private _suppressHttpErrorsUntil = 0;

	/**
	 * Suppresses global HTTP error popups for the given duration (ms). Used by the SSE layer around
	 * reconnect so transient warmup failures don't raise error modals.
	 */
	suppressHttpErrorsFor(durationMs: number) {
		this._suppressHttpErrorsUntil = Date.now() + durationMs;
	}

	setHttpError(error: HttpErrorResponse) {
		if (Date.now() < this._suppressHttpErrorsUntil) {
			return;
		}
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
}
