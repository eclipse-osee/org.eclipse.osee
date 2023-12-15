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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, combineLatest, filter, switchMap, tap } from 'rxjs';
import { apiURL } from '@osee/environments';
import {
	healthActiveMq,
	healthBalancers,
	healthSql,
	healthSqlSize,
	healthStatus,
	healthUsage,
	healthTablespace,
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

	public get Status(): Observable<healthStatus> {
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
	public get RemoteLog(): Observable<remoteHealthLog> {
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
	public get RemoteJava(): Observable<remoteHealthJava> {
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
	public get RemoteTop(): Observable<remoteHealthTop> {
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

	public get Balancers(): Observable<healthBalancers> {
		return this.http.get<healthBalancers>(apiURL + '/health/balancers');
	}

	public get PrometheusUrl(): Observable<string> {
		return this.http.get(apiURL + '/health/prometheus', {
			responseType: 'text',
		});
	}

	public get HttpHeaders(): Observable<unknownJson> {
		return this.http.get<unknownJson>(apiURL + '/health/http/headers');
	}

	public get ActiveMq(): Observable<healthActiveMq> {
		return this.http.get<healthActiveMq>(apiURL + '/health/activemq');
	}

	public get Usage(): Observable<healthUsage> {
		return this.http.get<healthUsage>(apiURL + '/health/usage');
	}

	public getSql(
		pageNum: number,
		pageSize: number,
		orderByName: string,
		orderByDirection: string
	): Observable<healthSql> {
		return this.http.get<healthSql>(apiURL + '/health/db/sql', {
			params: {
				pageNum: pageNum,
				pageSize: pageSize,
				orderByName: orderByName,
				orderByDirection: orderByDirection,
			},
		});
	}

	public get SqlSize(): Observable<healthSqlSize> {
		return this.http.get<healthSqlSize>(apiURL + '/health/db/sql/size');
	}

	public getTablespace(
		orderByName: string,
		orderByDirection: string
	): Observable<healthTablespace> {
		return this.http.get<healthTablespace>(
			apiURL + '/health/db/tablespace',
			{
				params: {
					orderByName: orderByName,
					orderByDirection: orderByDirection,
				},
			}
		);
	}
}
