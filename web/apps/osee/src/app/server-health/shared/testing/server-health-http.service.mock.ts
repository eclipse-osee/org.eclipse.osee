/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse  License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { Observable, of } from 'rxjs';
import { ServerHealthHttpService } from '../services/server-health-http.service';
import {
	healthActiveMq,
	healthBalancers,
	healthSql,
	healthSqlSize,
	healthStatus,
	healthTablespace,
	healthUsage,
	remoteHealthDetails,
	remoteHealthJava,
	remoteHealthLog,
	remoteHealthTop,
	sql,
	unknownJson,
} from '../types/server-health-types';

export const ServerHealthHttpServiceMock: Partial<ServerHealthHttpService> = {
	get Status(): Observable<healthStatus> {
		return of(healthStatusMock);
	},
	getRemoteDetails(
		remoteServerName: string
	): Observable<remoteHealthDetails> {
		return of(remoteHealthDetailsMock);
	},
	get RemoteLog(): Observable<remoteHealthLog> {
		return of(remoteHealthLogMock);
	},
	get RemoteJava(): Observable<remoteHealthJava> {
		return of(remoteHealthJavaMock);
	},
	get RemoteTop(): Observable<remoteHealthTop> {
		return of(remoteHealthTopMock);
	},
	get Balancers(): Observable<healthBalancers> {
		return of(healthBalancersMock);
	},
	get PrometheusUrl(): Observable<string> {
		return of('');
	},
	get HttpHeaders(): Observable<unknownJson> {
		return of(unknownJsonMock);
	},
	get ActiveMq(): Observable<healthActiveMq> {
		return of(healthActiveMqMock);
	},
	get Usage(): Observable<healthUsage> {
		return of(healthUsageMock);
	},
	getSql(
		pageNum: number,
		pageSize: number,
		orderByName: string,
		orderByDirection: string
	) {
		return of(healthSqlMock);
	},
	get SqlSize() {
		return of(healthSqlSizeMock);
	},
	getTablespace(orderByName: string, orderByDirection: string) {
		return of(healthTablespaceMock);
	},
};

export const healthStatusMock: healthStatus = {
	servers: [
		{
			name: 'osee.com:1111',
			serverAlive: true,
			dbAlive: true,
			errorMsg: '',
		},
		{
			name: 'osee.com:2222',
			serverAlive: true,
			dbAlive: true,
			errorMsg: '',
		},
		{
			name: 'osee.com:3333',
			serverAlive: true,
			dbAlive: true,
			errorMsg: '',
		},
		{
			name: 'osee.com:4444',
			serverAlive: false,
			dbAlive: false,
			errorMsg: '',
		},
		{
			name: 'osee.com:5555',
			serverAlive: false,
			dbAlive: false,
			errorMsg: '',
		},
	],
};

export const remoteHealthDetailsMock: remoteHealthDetails = {
	healthDetails: {
		uri: '',
		startTime: '',
		upTime: '',
		authScheme: '',
		authSchemeSupported: [''],
		heapMemAlloc: '',
		heapMemMax: '',
		heapMemUsed: '',
		nonHeapMemAlloc: '',
		nonHeapMemMax: '',
		nonHeapMemUsed: '',
		codeLocation: '',
		systemLoad: '',
		supportedVersions: [''],
		serverId: '',
		binaryDataPath: '',
		threadStats: [''],
		garbageCollectorStats: [''],
		serverWithHealthInfo: '',
	},
	errorMsg: '',
};

export const remoteHealthLogMock: remoteHealthLog = {
	healthLog: {
		log: '',
	},
};

export const remoteHealthJavaMock: remoteHealthJava = {
	healthJava: {
		vmName: '',
		vmVendor: '',
		vmVersion: '',
		vmSpecVersion: '',
		classPath: '',
		libraryPath: '',
		osName: '',
		osVersion: '',
		osArch: '',
		processArgs: [''],
		processes: [''],
	},
	errorMsg: '',
};

export const remoteHealthTopMock: remoteHealthTop = {
	healthTop: {
		top: '',
	},
	errorMsg: '',
};

export const healthBalancersMock: healthBalancers = {
	balancers: [
		{
			name: '',
			alive: true,
			errorMsg: '',
		},
		{
			name: '',
			alive: false,
			errorMsg: '',
		},
	],
};

export const healthActiveMqMock: healthActiveMq = {
	activeMqUrl: '',
	active: true,
	errorMsg: '',
};

export const healthUsageMock: healthUsage = {
	allUsers: [
		{
			name: '',
			email: '',
			userId: '',
			accountId: 1,
		},
	],
	allSessions: [
		{
			user: {
				name: '',
				email: '',
				userId: '',
				accountId: 1,
			},
			date: '',
			version: '',
			sessionId: '',
			clientAddress: '',
			clientMachineName: '',
			port: '',
		},
	],
	versionTypeMap: {
		versionType1: [
			{
				name: '',
				email: '',
				userId: '',
				accountId: 1,
			},
			{
				name: '',
				email: '',
				userId: '',
				accountId: 2,
			},
		],
		versionType2: [
			{
				name: '',
				email: '',
				userId: '',
				accountId: 3,
			},
			{
				name: '',
				email: '',
				userId: '',
				accountId: 4,
			},
		],
	},
	versionNameMap: {
		versionName1: [
			{
				name: '',
				email: '',
				userId: '',
				accountId: 1,
			},
			{
				name: '',
				email: '',
				userId: '',
				accountId: 2,
			},
		],
		versionName2: [
			{
				name: '',
				email: '',
				userId: '',
				accountId: 3,
			},
			{
				name: '',
				email: '',
				userId: '',
				accountId: 4,
			},
		],
	},
	errorMsg: '',
};

export const unknownJsonMock: unknownJson = {
	headerInfo1: '',
	headerInfo2: 2,
};

export const healthSqlMock: healthSql = {
	errorMsg: '',
	sqls: [
		{
			sqlText: '',
			elapsedTime: '',
			executions: '',
			elapsedTimeAverage: '',
			percent: '',
		},
	],
};

export const healthSqlSizeMock: healthSqlSize = {
	errorMsg: '',
	size: 7,
};

export const healthTablespaceMock: healthTablespace = {
	errorMsg: '',
	tablespaces: [
		{
			tablespaceName: '',
			maxTsPctUsed: '',
			autoExtend: '',
			tsPctUsed: '',
			tsPctFree: '',
			usedTsSize: '',
			freeTsSize: '',
			currTsSize: '',
			maxTxSize: '',
		},
	],
};
