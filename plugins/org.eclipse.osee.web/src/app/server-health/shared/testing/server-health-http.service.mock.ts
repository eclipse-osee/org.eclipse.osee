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
	healthStatus,
	healthUsage,
	remoteHealthDetails,
	remoteHealthJava,
	remoteHealthLog,
	remoteHealthTop,
	unknownJson,
} from '../types/server-health-types';

export const ServerHealthHttpServiceMock: Partial<ServerHealthHttpService> = {
	getStatus(): Observable<healthStatus> {
		return of(healthStatusMock);
	},
	getRemoteDetails(
		remoteServerName: string
	): Observable<remoteHealthDetails> {
		return of(remoteHealthDetailsMock);
	},
	getRemoteLog(): Observable<remoteHealthLog> {
		return of(remoteHealthLogMock);
	},
	getRemoteJava(): Observable<remoteHealthJava> {
		return of(remoteHealthJavaMock);
	},
	getRemoteTop(): Observable<remoteHealthTop> {
		return of(remoteHealthTopMock);
	},
	getBalancers(): Observable<healthBalancers> {
		return of(healthBalancersMock);
	},
	getPrometheusUrl(): Observable<string> {
		return of('');
	},
	getHttpHeaders(): Observable<unknownJson> {
		return of(unknownJsonMock);
	},
	getActiveMq(): Observable<healthActiveMq> {
		return of(healthActiveMqMock);
	},
	getUsage(): Observable<healthUsage> {
		return of(healthUsageMock);
	},
};

export const healthStatusMock: healthStatus = {
	servers: [
		{
			name: 'osee.com:1111',
			serverAlive: true,
			dbAlive: true,
		},
		{
			name: 'osee.com:2222',
			serverAlive: true,
			dbAlive: true,
		},
		{
			name: 'osee.com:3333',
			serverAlive: true,
			dbAlive: true,
		},
		{
			name: 'osee.com:4444',
			serverAlive: false,
			dbAlive: false,
		},
		{
			name: 'osee.com:5555',
			serverAlive: false,
			dbAlive: false,
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
		},
		{
			name: '',
			alive: false,
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
