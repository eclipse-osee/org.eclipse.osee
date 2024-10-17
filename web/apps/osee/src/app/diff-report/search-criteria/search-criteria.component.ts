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
import { AsyncPipe } from '@angular/common';
import { Component, Output, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { BehaviorSubject, combineLatest, from, iif, of } from 'rxjs';
import {
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	reduce,
	share,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs/operators';
import { Build, Program, SearchOptions } from '../model/types';
import { ReportService } from '../services/report.service';

@Component({
	selector: 'osee-search-criteria',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatSelect,
		MatOption,
		MatButton,
	],
	templateUrl: './search-criteria.component.html',
})
export class SearchCriteriaComponent {
	private reportService = inject(ReportService);

	workflowNum = '';
	workflowDesc = '';
	endPointUrl = this.reportService.diffEndpoint;
	programSelection = this.reportService.selectedProgram;
	selectedBuild = this.reportService.selectedBuild;
	private startReport = new BehaviorSubject<boolean>(false);

	@Output() generateReport = combineLatest([
		this.startReport,
		this.programSelection,
		this.selectedBuild,
	]).pipe(
		filter(
			([startReport, programSelection, selectedBuild]) =>
				startReport === true &&
				programSelection !== undefined &&
				selectedBuild !== undefined
		),
		map(
			([_, programSelection, selectedBuild]) =>
				({
					workflowNum: this.workflowNum,
					workflowDesc: this.workflowDesc,
					program: programSelection!.guid,
					build: selectedBuild!.guid,
					displaySearch: false,
				}) as SearchOptions
		),
		tap((searchOptions) => {
			if (searchOptions.program && searchOptions.build) {
				searchOptions.displaySearch = true;
			} else {
				searchOptions.displaySearch = false;
			}
			this.reportService.setSearchOptions = searchOptions;
			this.startReport.next(false);
		})
	);

	programToBuilds = combineLatest([this.endPointUrl]).pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([url]) => this.reportService.searchOptions(url)),
		shareReplay({ bufferSize: 1, refCount: true }),
		map((config) => config.programsToBuilds)
	);

	programs = this.programToBuilds.pipe(
		switchMap((programToBuilds) =>
			from(programToBuilds).pipe(
				map((programToBuild) => programToBuild.program)
			)
		),
		reduce((acc, curr) => [...acc, curr], [] as Program[])
	);

	programSelectionText = this.programs.pipe(
		switchMap((programs) =>
			iif(
				() => programs.length > 0,
				of('Select a Program'),
				of('No Program available')
			)
		)
	);

	selectProgram(event: MatSelectChange) {
		this.reportService.SelectedProgram = event.value;
	}

	programSelectionName = this.programSelection.pipe(
		map((program) => program?.name)
	);

	builds = combineLatest([this.programToBuilds, this.programSelectionName])
		.pipe(
			filter(
				([_programToBuilds, programSelectionName]) =>
					programSelectionName !== undefined
			),
			switchMap(([programToBuilds, programSelectionName]) =>
				from(programToBuilds).pipe(
					filter(
						(programToBuild) =>
							programToBuild.program.name ===
							programSelectionName!
					)
				)
			)
		)
		.pipe(map((z) => z.builds));

	buildSelectionText = this.builds.pipe(
		switchMap((builds) =>
			iif(
				() => builds.length > 0,
				of('Select a Build'),
				of('No Build available')
			)
		)
	);

	setSelectedBuild(build: Build) {
		this.reportService.SelectedBuild = build;
	}

	clicked = signal(false);

	reset() {
		this.workflowNum = '';
		this.workflowDesc = '';
		const searchOptions = {
			workflowNum: '',
			workflowDesc: '',
			program: '',
			build: '',
			displaySearch: false,
		} as SearchOptions;
		this.reportService.SelectedProgram = {} as Program;
		this.reportService.SelectedBuild = {} as Build;
		this.reportService.setSearchOptions = searchOptions;
		this.reportService.DisplayTable = false;
		this.clicked.set(false);
	}

	startGenerateReport() {
		this.startReport.next(true);
		this.clicked.set(true);
	}
}
