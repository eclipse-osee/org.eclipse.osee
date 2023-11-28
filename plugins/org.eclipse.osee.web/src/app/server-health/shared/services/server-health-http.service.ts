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
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
	BehaviorSubject,
	Observable,
	combineLatest,
	filter,
	switchMap,
	tap,
} from 'rxjs';
import { apiURL } from '@osee/environments';
import {
	healthActiveMq,
	healthBalancers,
	healthStatus,
	healthUsage,
	remoteHealthDetails,
	remoteHealthJava,
	remoteHealthLog,
	remoteHealthTop,
	unknownJson,
} from '../types/server-health-types';
import { ServerHealthDetailsService } from './server-health-details.service';

@Injectable({
	providedIn: 'root',
})
export class ServerHealthHttpService {
	constructor(
		private http: HttpClient,
		private healthDetailsService: ServerHealthDetailsService
	) {}

	public getStatus(): Observable<healthStatus> {
		return this.http.get<healthStatus>(apiURL + '/health/status');
	}

	public getRemoteDetails(
		remoteServerName: string
	): Observable<remoteHealthDetails> {
		return this.http
			.get<remoteHealthDetails>(apiURL + '/health/details/remote', {
				params: {
					remoteServerName: remoteServerName,
				},
			})
			.pipe(
				tap((data) =>
					this.healthDetailsService.setRemoteDetails(
						data,
						data.healthDetails.serverWithHealthInfo
					)
				)
			);
	}

	// Dependent on designated health server name returned by getRemoteDetails
	public getRemoteLog(): Observable<remoteHealthLog> {
		return combineLatest([
			this.healthDetailsService.getRemoteDetails(),
			this.healthDetailsService.getRemoteServerName(),
		]).pipe(
			filter(
				([remoteHealthDetails, name]) =>
					name !== '' &&
					remoteHealthDetails.healthDetails.codeLocation !== '' &&
					remoteHealthDetails.healthDetails.uri !== ''
			),
			switchMap(([remoteHealthDetails, name]) =>
				this.http.get<remoteHealthLog>(apiURL + '/health/log/remote', {
					params: {
						remoteServerName: name,
						appServerDir:
							remoteHealthDetails.healthDetails.codeLocation,
						serverUri: remoteHealthDetails.healthDetails.uri,
					},
				})
			)
		);
	}

	// Dependent on designated health server name returned by getRemoteDetails
	public getRemoteJava(): Observable<remoteHealthJava> {
		return combineLatest([
			this.healthDetailsService.getRemoteServerName(),
		]).pipe(
			filter(([name]) => name !== ''),
			switchMap(([name]) =>
				this.http.get<remoteHealthJava>(
					apiURL + '/health/java/remote',
					{
						params: {
							remoteServerName: name,
						},
					}
				)
			)
		);
	}

	// Dependent on designated health server name returned by getRemoteDetails
	public getRemoteTop(): Observable<remoteHealthTop> {
		return combineLatest([
			this.healthDetailsService.getRemoteServerName(),
		]).pipe(
			filter(([name]) => name !== ''),
			switchMap(([name]) =>
				this.http.get<remoteHealthTop>(apiURL + '/health/top/remote', {
					params: {
						remoteServerName: name,
					},
				})
			)
		);
	}

	public getBalancers(): Observable<healthBalancers> {
		return this.http.get<healthBalancers>(apiURL + '/health/balancers');
	}

	public getPrometheusUrl(): Observable<string> {
		return this.http.get(apiURL + '/health/prometheus', {
			responseType: 'text',
		});
	}

	public getHttpHeaders(): Observable<unknownJson> {
		return this.http.get<unknownJson>(apiURL + '/health/http/headers');
	}

	public getActiveMq(): Observable<healthActiveMq> {
		return this.http.get<healthActiveMq>(apiURL + '/health/activemq');
	}

	public getUsage(): Observable<healthUsage> {
		return this.http.get<healthUsage>(apiURL + '/health/usage');
	}
}
