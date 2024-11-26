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
import {
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { SelectionModel } from '@angular/cdk/collections';
import { AsyncPipe } from '@angular/common';
import { Component, OnInit, effect, viewChild, inject } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatPrefix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { apiURL } from '@osee/environments';
import { of } from 'rxjs';
import { map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { Artifact, test, workflow } from '../model/artifact';
import { NewRow } from '../model/row';
import { ReportService } from '../services/report.service';

@Component({
	selector: 'osee-diff-table',
	imports: [
		AsyncPipe,
		FormsModule,
		ReactiveFormsModule,
		MatFormField,
		MatButton,
		MatInput,
		MatTable,
		MatSort,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatSortHeader,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatPaginator,
		MatIcon,
		MatPrefix,
	],
	templateUrl: './diff-table.component.html',
	animations: [
		trigger('detailExpand', [
			state('collapsed, void', style({ height: '0px', minHeight: '0' })),
			state('expanded', style({ height: '100%' })),
			transition(
				'expanded <=> collapsed',
				animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
			transition(
				'expanded <=> void',
				animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
		]),
	],
})
export class DiffTableComponent implements OnInit {
	private reportService = inject(ReportService);

	loading = false;
	private paginator = viewChild.required(MatPaginator);
	private sort = viewChild.required(MatSort);
	dataSource: MatTableDataSource<workflow> =
		new MatTableDataSource<workflow>();

	private _updateDataSourcePaginator = effect(() => {
		this.dataSource.paginator = this.paginator();
	});

	private _updateDataSourceSort = effect(() => {
		this.dataSource.sort = this.sort();
	});
	selection = new SelectionModel<Artifact>(true, []);
	allRowsExpanded = false;
	isExpansionDetailRow = (_i: number, row: object) =>
		Object.prototype.hasOwnProperty.call(row, 'detailRow');

	newRow(workflow: workflow): NewRow {
		return {
			actionId: workflow.actionId,
			workflowID: workflow.workflowID,
			program: workflow.program,
			build: workflow.build,
			state: workflow.state,
			title: workflow.title,
			requirement: '',
			test: '',
		};
	}

	workflowFilter = new FormControl('');
	changeExportFilter = new FormControl('');

	endPointUrl = this.reportService.diffEndpoint.pipe(
		take(1),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	link = this.endPointUrl.pipe(map((endUrl) => apiURL + endUrl));

	constructor() {
		this.dataSource.filterPredicate = this.filterFunction;
	}

	filterValues = {
		workflowID: '',
		webExported: '',
	};

	ngOnInit() {
		this.dataSource.data = [];
		this.workflowFilter.valueChanges.subscribe((workflowID) => {
			if (workflowID != null) this.filterValues.workflowID = workflowID;
			this.dataSource.filter = JSON.stringify(this.filterValues);
		});

		this.changeExportFilter.valueChanges.subscribe((webExported) => {
			if (webExported != null)
				this.filterValues.webExported = webExported;
			this.dataSource.filter = JSON.stringify(this.filterValues);
		});
	}

	displayedColumns = [
		'actionId',
		'workflowID',
		'program',
		'build',
		'state',
		'title',
		'webExported',
		'actions',
	];

	applyFilter(event: Event) {
		let filterValue = (event.target as HTMLInputElement).value;
		filterValue = filterValue.trim();
		filterValue = filterValue.toLowerCase();
		this.dataSource.filter = filterValue;
	}

	onArtifactToggled(artifact: Artifact) {
		this.selection.toggle(artifact);
	}

	artifact = this.reportService.artifact;

	artifactData = this.artifact.pipe(
		take(1),
		switchMap((artifact) => of(artifact.workflows)),
		tap((data) => (this.dataSource.data = data))
	);

	getTestTd(test: test) {
		const max = 50;
		let trimmed = test.name.slice(test.name.lastIndexOf('.') + 1);
		if (trimmed.length > max) {
			trimmed = '...' + trimmed.slice(-max);
		}
		return trimmed;
	}

	filterFunction(data: workflow, filter: string): boolean {
		const searchTerms = JSON.parse(filter);
		return (
			data.workflowID.toLowerCase().indexOf(searchTerms.workflowID) !==
				-1 &&
			data.webExported.toLowerCase().indexOf(searchTerms.webExported) !==
				-1
		);
	}

	toggleRow(element: { expanded: boolean }) {
		if (!this.allRowsExpanded) {
			element.expanded = !element.expanded;
		}
	}

	expandAllRows() {
		this.allRowsExpanded = true;
	}

	collapseAllRows() {
		this.allRowsExpanded = false;
		this.dataSource.data.forEach((element) => {
			element.expanded = false;
		});
	}

	getChangeReport(report: string) {
		let data: string;
		this.reportService.getReport(report).subscribe((resp) => {
			//@ts-expect-error Murshed can you make this make sense? are you just looking for resp.text()?
			data = resp;
			this.downloadAsXmlFile(
				data,
				report.substring(report.lastIndexOf('/') + 1)
			);
		});
	}

	updateRowsforCSV(results: workflow[]) {
		const rows = [];
		//TODO update to make this make sense?
		//eslint-disable-next-line
		for (let i = 0; i < results.length; i++) {
			const workflow = results[i];
			let row = this.newRow(workflow);
			rows.push(row);
			if (workflow.requirements.length) {
				for (let k = 0; k < workflow.requirements.length; k++) {
					if (k) {
						row = this.newRow(workflow);
						rows.push(row);
					}
					const req = workflow.requirements[k];
					row.requirement = req.name;
					if (req.tests.length) {
						row.test = this.getTestTd(req.tests[0]);
						for (let j = 1; j < req.tests.length; j++) {
							row = this.newRow(workflow);
							rows.push(row);
							row.requirement = req.name;
							row.test = this.getTestTd(req.tests[j]);
						}
					}
				}
			}
		}
		return rows;
	}

	convertRowsToStringForCSV(rows: NewRow[], headerList: string[]) {
		const array = typeof rows != 'object' ? JSON.parse(rows) : rows;
		let str = '';
		let column = 'S.No,';
		const newHeaders = [
			'Action ID',
			'WorkFlow ID',
			'Program',
			'Build',
			'State',
			'Title',
			'Requirement',
			'Test',
		];

		for (const index in newHeaders) {
			column += newHeaders[index] + ',';
		}
		column = column.slice(0, -1);
		str += column + '\r\n';
		for (let i = 0; i < array.length; i++) {
			let line = i + 1 + '';
			for (const index in headerList) {
				const head = headerList[index];

				line += ',' + this.stripData(array[i][head]);
			}
			str += line + '\r\n';
		}
		return str;
	}

	stripData(data: unknown) {
		if (typeof data == 'string') {
			let newData = data.replace(/,/g, ' ');
			newData = newData.replace(/(\r\n|\n|\r)/gm, ' ');
			return newData;
		} else if (typeof data == 'undefined') {
			return '-';
		} else if (typeof data == 'number') {
			return data.toString();
		} else {
			return data;
		}
	}

	downloadAsXmlFile(data: string, filename: string) {
		const blob = new Blob([data], {
			type: 'text/xml;',
		});
		const dwldLink = document.createElement('a');
		const url = URL.createObjectURL(blob);
		dwldLink.setAttribute('href', url);
		dwldLink.setAttribute('download', filename);
		dwldLink.style.visibility = 'hidden';
		document.body.appendChild(dwldLink);
		dwldLink.click();
		document.body.removeChild(dwldLink);
	}

	downloadAsCsvFile(csvData: string, filename: string) {
		const blob = new Blob(['\ufeff' + csvData], {
			type: 'text/csv;charset=utf-8;',
		});
		const dwldLink = document.createElement('a');
		const url = URL.createObjectURL(blob);
		dwldLink.setAttribute('href', url);
		dwldLink.setAttribute('download', filename);
		dwldLink.style.visibility = 'hidden';
		document.body.appendChild(dwldLink);
		dwldLink.click();
		document.body.removeChild(dwldLink);
	}

	exportDataAsCsv() {
		const arrHeader = [
			'actionId',
			'workflowID',
			'program',
			'build',
			'state',
			'title',
			'requirement',
			'test',
		];
		const updatedRows = this.updateRowsforCSV(this.dataSource.data);
		const csvData = this.convertRowsToStringForCSV(updatedRows, arrHeader);
		this.downloadAsCsvFile(csvData, 'TraceReport.csv');
	}

	exportAllDataAsCsv(url: string) {
		let csvData: string;
		this.reportService.downloadAllDatatoCsv(url).subscribe((resp) => {
			csvData = resp;
			this.downloadAsCsvFile(csvData, 'CompleteTraceReport.csv');
		});
	}

	downloadChangeReports(url: string) {
		this.reportService
			.downloadChangeReports(url)
			.pipe(tap(() => console.log('Completed file download.')))
			.subscribe((data: BlobPart) => this.getZipFile(data));
	}

	getZipFile(data: BlobPart) {
		const a = document.createElement('a');
		document.body.appendChild(a);
		a.setAttribute('style', 'display: none');
		const blob = new Blob([data], { type: 'application/zip' });
		const url = window.URL.createObjectURL(blob);
		a.href = url;
		a.download = 'changeReports.zip';
		a.click();
		window.URL.revokeObjectURL(url);
	}
}
