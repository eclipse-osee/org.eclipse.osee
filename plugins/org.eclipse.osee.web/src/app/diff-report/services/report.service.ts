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
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, combineLatest, Observable, of, Subject } from 'rxjs';
import {
	debounceTime,
	distinctUntilChanged,
	map,
	share,
	shareReplay,
	switchMap,
} from 'rxjs/operators';
import { Artifact } from '../model/artifact';
import { Build, Program, SearchOptions } from '../model/types';
import { DiffEndPoint } from '../model/types';
import { Config } from '../model/types';
import { apiURL } from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class ReportService {
	private _selectedProgram$: BehaviorSubject<Program | undefined> =
		new BehaviorSubject<Program | undefined>(undefined);
	private _selectedBuild$ = new BehaviorSubject<Build | undefined>(undefined);

	private _searchOptions$: BehaviorSubject<SearchOptions> =
		new BehaviorSubject<SearchOptions>({
			workflowNum: '',
			workflowDesc: '',
			program: '',
			build: '',
			displaySearch: false,
		});

	private _currentPage$ = new BehaviorSubject<number>(0);
	private _currentPageSize$ = new BehaviorSubject<number>(10);
	private _displayTable$ = new BehaviorSubject<boolean>(false);

	constructor(private http: HttpClient) {}

	diffEndpoint = this.http
		.get<DiffEndPoint>(apiURL + '/ats/teamwf/diff')
		.pipe(map((x) => x.endpointUrl));

	searchOptions(endpointUrl: string): Observable<Config> {
		return this.http.get<Config>(apiURL + endpointUrl + '/config');
	}

	set SelectedProgram(program: Program | undefined) {
		this._selectedProgram$.next(program);
	}

	get selectedProgram() {
		return this._selectedProgram$.asObservable();
	}

	set setSearchOptions(searchOptions: SearchOptions) {
		this._searchOptions$.next(searchOptions);
	}

	get selectedBuild() {
		return this._selectedBuild$.asObservable();
	}

	set SelectedBuild(build: Build | undefined) {
		this._selectedBuild$.next(build);
	}

	get getSearchOptions() {
		return this._searchOptions$.asObservable();
	}

	get currentPage() {
		return this._currentPage$;
	}

	set page(page: number) {
		this._currentPage$.next(page);
	}

	get currentPageSize() {
		return this._currentPageSize$;
	}

	set pageSize(pageSize: number) {
		this._currentPageSize$.next(pageSize);
	}

	get displayTable() {
		return this._displayTable$.asObservable();
	}

	set DisplayTable(display: boolean) {
		this._displayTable$.next(display);
	}

	querySearchCount = combineLatest([
		this.diffEndpoint,
		this._searchOptions$,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([url, options]) =>
			this.getCount(
				options.workflowNum,
				options.workflowDesc,
				options.program,
				options.build,
				url
			)
		),
		shareReplay({ bufferSize: 1, refCount: true }),
		map((result) => Number(result))
	);

	getCount(
		workflowNum: string,
		workflowDesc: string,
		program: string,
		build: string,
		endPointUrl: string
	): Observable<String> {
		if (program && build) {
			return this.http.get<String>(apiURL + endPointUrl + '/count?', {
				params: {
					workflowNum: workflowNum,
					workflowDesc: workflowDesc,
					build: build,
					program: program,
				},
			});
		} else return of();
	}

	artifact = combineLatest([
		this.diffEndpoint,
		this.currentPage,
		this.currentPageSize,
		this._searchOptions$,
	]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([url, page, pageSize, options]) =>
			this.findArtifacts(
				options.workflowNum,
				options.workflowDesc,
				options.program,
				options.build,
				page + 1,
				pageSize,
				url
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	findArtifacts(
		workflowNum: string,
		workflowDesc: string,
		program: string,
		build: string,
		pageNum: number,
		pageSize: number,
		endPointUrl: string
	): Observable<Artifact> {
		if (program && build) {
			return this.http.get<Artifact>(
				apiURL + endPointUrl + '/searchTrace?',
				{
					params: {
						pageNumber: pageNum.toString(),
						pageCount: pageSize.toString(),
						workflowNum: workflowNum,
						workflowDesc: workflowDesc,
						build: build,
						program: program,
					},
				}
			);
		} else return of();
	}

	downloadAllDatatoCsv(url: string): Observable<String> {
		var program = this._searchOptions$.value.program;
		var build = this._searchOptions$.value.build;
		if (program && build) {
			return this.http.get<String>(url + '/downloadAllDataToCsv?', {
				params: {
					build: build,
					program: program,
				},
			});
		} else return of();
	}
}
