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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, combineLatest, iif, of } from 'rxjs';
import {
	debounceTime,
	distinct,
	filter,
	map,
	shareReplay,
	switchMap,
	take,
} from 'rxjs/operators';
import { FilesService } from '@osee/shared/services';
import { apiURL } from '@osee/environments';
import type { connection, MimReport } from '@osee/messaging/shared/types';
import type { NodeTraceReportItem } from '../../types/NodeTraceReport';
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class ReportsService {
	constructor(
		private ui: UiService,
		private http: HttpClient,
		private fileService: FilesService
	) {}

	private _connection = new BehaviorSubject<
		Partial<connection> | Required<connection>
	>({ id: '-1' });
	private _requestBody: BehaviorSubject<string> = new BehaviorSubject('');
	private _requestBodyFile: BehaviorSubject<File | undefined> =
		new BehaviorSubject<File | undefined>(undefined);
	private _includeDiff: BehaviorSubject<boolean> =
		new BehaviorSubject<boolean>(false);

	private _allCurrentPage$ = new BehaviorSubject<number>(0);

	private _allCurrentPageSize$ = new BehaviorSubject<number>(200);

	private _noCurrentPage$ = new BehaviorSubject<number>(0);

	private _noCurrentPageSize$ = new BehaviorSubject<number>(200);

	getReports() {
		return this.http
			.get<MimReport[]>(apiURL + '/mim/reports')
			.pipe(shareReplay(1));
	}

	downloadReport(report: MimReport | undefined, viewId: string) {
		return combineLatest([this.branchId, this._connection]).pipe(
			take(1),
			filter(
				(val): val is [string, Required<connection>] =>
					val[1].name !== undefined &&
					val[1].transportType !== undefined
			),
			switchMap(([branchId, connection]) =>
				iif(
					() => report !== undefined,
					this.getReport(
						report as MimReport,
						branchId,
						viewId,
						connection
					).pipe(
						map((res) => {
							if (res.size !== 0) {
								const blob = new Blob([res], {
									type: report?.producesMediaType,
								});
								const url = URL.createObjectURL(blob);
								const link = document.createElement('a');
								link.href = url;
								link.setAttribute(
									'download',
									report?.fileNamePrefix +
										'_' +
										connection?.name +
										'.' +
										report?.fileExtension
								);
								document.body.appendChild(link);
								link.click();
								link.remove();
							}
						})
					),
					of()
				)
			)
		);
	}

	private getReport(
		report: MimReport,
		branchId: string,
		viewId: string,
		connection?: connection
	) {
		return combineLatest([
			this.requestBody,
			this.requestBodyFile,
			this.includeDiff,
		]).pipe(
			take(1),
			switchMap(([input, file, includeDiff]) =>
				iif(
					() =>
						report !== undefined &&
						report.url !== '' &&
						branchId !== '' &&
						connection !== undefined &&
						connection.id !== '-1',
					this.fileService.getFileAsBlob(
						report.httpMethod,
						report.url
							.replace('<branchId>', branchId)
							.replace('<connectionId>', connection?.id!)
							.replace('<diffAvailable>', includeDiff + '')
							.replace('<viewId>', viewId),
						file === undefined ? input : file
					),
					of(new Blob())
				)
			)
		);
	}

	private _nodeTraceReportRequirements = combineLatest([
		this.ui.id,
		this._allCurrentPage$,
		this._allCurrentPageSize$,
	]).pipe(
		filter(
			([v, page, size]) =>
				v !== undefined &&
				v !== '' &&
				v !== '-1' &&
				page >= 0 &&
				size >= 0
		),
		distinct(),
		debounceTime(50),
		switchMap(([id, page, size]) =>
			this.http.get<NodeTraceReportItem[]>(
				apiURL + '/mim/reports/' + id + '/allRequirementsToInterface',
				{
					params: {
						count: size,
						pageNum: page + 1,
					},
				}
			)
		)
	);

	private _nodeTraceReportRequirementsCount = this.ui.id.pipe(
		filter((id) => id !== undefined && id !== '' && id !== '-1'),
		switchMap((id) =>
			this.http.get<number>(
				apiURL +
					'/mim/reports/' +
					id +
					'/allRequirementsToInterface/count'
			)
		),
		take(1)
	);

	private _nodeTraceReportRequirementsNoMatch = combineLatest([
		this.ui.id,
		this._noCurrentPage$,
		this._noCurrentPageSize$,
	]).pipe(
		filter(
			([v, page, size]) =>
				v !== undefined &&
				v !== '' &&
				v !== '-1' &&
				page >= 0 &&
				size >= 0
		),
		distinct(),
		debounceTime(50),
		switchMap(([id, page, size]) =>
			this.http.get<NodeTraceReportItem[]>(
				apiURL + '/mim/reports/' + id + '/noRequirementsToInterface',
				{
					params: {
						count: size,
						pageNum: page + 1,
					},
				}
			)
		)
	);

	private _nodeTraceReportRequirementsNoMatchCount = this.ui.id.pipe(
		filter((id) => id !== undefined && id !== '' && id !== '-1'),
		switchMap((id) =>
			this.http.get<number>(
				apiURL +
					'/mim/reports/' +
					id +
					'/noRequirementsToInterface/count'
			)
		),
		take(1)
	);

	private _nodeTraceReportInterfaceArtifacts = combineLatest([
		this.ui.id,
		this._allCurrentPage$,
		this._allCurrentPageSize$,
	]).pipe(
		filter(
			([v, page, size]) =>
				v !== undefined &&
				v !== '' &&
				v !== '-1' &&
				page >= 0 &&
				size >= 0
		),
		distinct(),
		debounceTime(50),
		switchMap(([id, page, size]) =>
			this.http.get<NodeTraceReportItem[]>(
				apiURL + '/mim/reports/' + id + '/allInterfaceToRequirements',
				{
					params: {
						count: size,
						pageNum: page + 1,
					},
				}
			)
		)
	);

	private _nodeTraceReportInterfaceArtifactsCount = this.ui.id.pipe(
		filter((id) => id !== undefined && id !== '' && id !== '-1'),
		switchMap((id) =>
			this.http.get<number>(
				apiURL +
					'/mim/reports/' +
					id +
					'/allInterfaceToRequirements/count'
			)
		),
		take(1)
	);

	private _nodeTraceReportInterfaceArtifactsNoMatch = combineLatest([
		this.ui.id,
		this._noCurrentPage$,
		this._noCurrentPageSize$,
	]).pipe(
		filter(
			([v, page, size]) =>
				v !== undefined &&
				v !== '' &&
				v !== '-1' &&
				page >= 0 &&
				size >= 0
		),
		distinct(),
		debounceTime(50),
		switchMap(([id, page, size]) =>
			this.http.get<NodeTraceReportItem[]>(
				apiURL + '/mim/reports/' + id + '/noInterfaceToRequirements',
				{
					params: {
						count: size,
						pageNum: page + 1,
					},
				}
			)
		)
	);

	private _nodeTraceReportInterfaceArtifactsNoMatchCount = this.ui.id.pipe(
		filter((id) => id !== undefined && id !== '' && id !== '-1'),
		switchMap((id) =>
			this.http.get<number>(
				apiURL +
					'/mim/reports/' +
					id +
					'/noInterfaceToRequirements/count'
			)
		),
		take(1)
	);

	private _diffReportRoute = combineLatest([this.ui.id, this.ui.type]).pipe(
		switchMap(([branchId, branchType]) =>
			of(
				'/ple/messaging/reports/' +
					branchType +
					'/' +
					branchId +
					'/differences'
			)
		)
	);

	private _nodeTraceReportRoute = combineLatest([
		this.ui.id,
		this.ui.type,
	]).pipe(
		switchMap(([branchId, branchType]) =>
			of(
				'/ple/messaging/reports/' +
					branchType +
					'/' +
					branchId +
					'/traceReport'
			)
		)
	);

	get nodeTraceReportRequirements() {
		return this._nodeTraceReportRequirements;
	}

	get nodeTraceReportRequirementsCount() {
		return this._nodeTraceReportRequirementsCount;
	}

	get nodeTraceReportNoMatchingArtifacts() {
		return this._nodeTraceReportRequirementsNoMatch;
	}

	get nodeTraceReportNoMatchingArtifactsCount() {
		return this._nodeTraceReportRequirementsNoMatchCount;
	}

	get nodeTraceReportInterfaceArtifacts() {
		return this._nodeTraceReportInterfaceArtifacts;
	}

	get nodeTraceReportInterfaceArtifactsCount() {
		return this._nodeTraceReportInterfaceArtifactsCount;
	}

	get nodeTraceReportNoMatchingInterfaceArtifacts() {
		return this._nodeTraceReportInterfaceArtifactsNoMatch;
	}

	get nodeTraceReportNoMatchingInterfaceArtifactsCount() {
		return this._nodeTraceReportInterfaceArtifactsNoMatchCount;
	}

	get diffReportRoute() {
		return this._diffReportRoute;
	}

	get nodeTraceReportRoute() {
		return this._nodeTraceReportRoute;
	}

	get branchId() {
		return this.ui.id;
	}

	set BranchId(branchId: string) {
		this.ui.idValue = branchId;
	}

	get branchType() {
		return this.ui.type;
	}

	set BranchType(branchType: string) {
		this.ui.typeValue = branchType;
	}

	get connection() {
		return this._connection;
	}

	set Connection(connection: connection) {
		this._connection.next(connection);
	}

	get requestBody() {
		return this._requestBody;
	}

	set RequestBody(requestBody: string) {
		this.requestBody.next(requestBody);
	}

	get requestBodyFile() {
		return this._requestBodyFile;
	}

	set RequestBodyFile(requestBodyFile: File) {
		this.requestBodyFile.next(requestBodyFile);
	}

	get includeDiff() {
		return this._includeDiff;
	}

	set IncludeDiff(value: boolean) {
		this._includeDiff.next(value);
	}

	get currentPageSize(): Observable<number> {
		return this._allCurrentPageSize$;
	}

	set currentPageSize(value: number) {
		this._allCurrentPageSize$.next(value);
	}

	get currentPage(): Observable<number> {
		return this._allCurrentPage$;
	}

	set currentPage(value: number) {
		this._allCurrentPage$.next(value);
	}

	get missingPageSize(): Observable<number> {
		return this._noCurrentPageSize$;
	}

	set missingPageSize(value: number) {
		this._noCurrentPageSize$.next(value);
	}

	get missingPage(): Observable<number> {
		return this._noCurrentPage$;
	}

	set missingPage(value: number) {
		this._noCurrentPage$.next(value);
	}
}
