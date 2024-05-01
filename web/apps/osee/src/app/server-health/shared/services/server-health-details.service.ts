/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {
	remoteHealthDetails,
	defaultRemoteHealthDetails,
} from '../types/server-health-types';

@Injectable({
	providedIn: 'root',
})
export class ServerHealthDetailsService {
	constructor() {}

	private _remoteDetails = new BehaviorSubject<remoteHealthDetails>(
		defaultRemoteHealthDetails
	);
	private _remoteServerName = new BehaviorSubject<string>('');

	setRemoteDetails(details: remoteHealthDetails, name: string) {
		this._remoteDetails.next(details);
		this._remoteServerName.next(name);
	}
	getRemoteDetails() {
		return this._remoteDetails.asObservable();
	}
	getRemoteServerName() {
		return this._remoteServerName.asObservable();
	}
}
