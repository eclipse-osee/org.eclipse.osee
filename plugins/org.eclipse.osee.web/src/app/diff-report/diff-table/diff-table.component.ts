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
import { CommonModule } from '@angular/common';
import {
	MatPaginator,
	MatPaginatorModule,
	PageEvent,
} from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ReportService } from '../services/report.service';
import { Artifact, workflow, test } from '../model/artifact';
import { NewRow } from '../model/row';
import { SelectionModel } from '@angular/cdk/collections';
import { Component, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { map, switchMap, tap } from 'rxjs/operators';
import { of } from 'rxjs/internal/observable/of';
import { state, style, trigger } from '@angular/animations';
import { MatSortModule } from '@angular/material/sort';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { apiURL } from '@osee/environments';
import { MatIconModule } from '@angular/material/icon';

@Component({
	selector: 'osee-diff-table',
	standalone: true,
	imports: [
		CommonModule,
		MatPaginatorModule,
		MatTableModule,
		MatSortModule,
		FormsModule,
		ReactiveFormsModule,
		MatButtonModule,
		MatInputModule,
		MatTableModule,
		MatSelectModule,
		MatIconModule,
	],
	templateUrl: './diff-table.component.html',
	styleUrls: ['./diff-table.component.sass'],
	animations: [
		trigger('detailExpand', [
			state(
				'collapsed',
				style({ height: '0px', minHeight: '0', visibility: 'hidden' })
			),
			state('expanded', style({ height: '*', visibility: 'visible' })),
		]),
	],
})
export class DiffTableComponent implements OnInit, AfterViewInit {
	loading = false;
	@ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
	@ViewChild(MatSort) sort!: MatSort;
	dataSource: MatTableDataSource<workflow> =
		new MatTableDataSource<workflow>();
	selection = new SelectionModel<Artifact>(true, []);
	isExpansionDetailRow = (i: number, row: Object) =>
		row.hasOwnProperty('detailRow');

	newRow(workflow: workflow): NewRow {
		return {
			workflowID: workflow.workflowID,
			program: workflow.program,
			build: workflow.build,
			subsystem: workflow.subsystem,
			changeType: workflow.changeType,
			state: workflow.state,
			enhancement: workflow.enhancement,
			description: workflow.description,
			requirement: '',
			test: '',
		};
	}

	workflowFilter = new FormControl('');
	endPointUrl = this.reportService.diffEndpoint;
	link = this.endPointUrl.pipe(map((endUrl) => apiURL + endUrl));

	constructor(private reportService: ReportService) {
		this.dataSource.paginator = this.paginator;
		this.dataSource.sort = this.sort;
		this.dataSource.filterPredicate = this.filterFunction;
	}

	filterValues = {
		workflowID: '',
	};

	ngOnInit() {
		this.workflowFilter.valueChanges.subscribe((workflowID) => {
			if (workflowID != null) this.filterValues.workflowID = workflowID;
			this.dataSource.filter = JSON.stringify(this.filterValues);
		});
	}

	displayedColumns = [
		'workflowID',
		'program',
		'build',
		'subsystem',
		'type',
		'state',
		'enhancement',
		'description',
		'actions',
	];

	applyFilter(event: any) {
		var filterValue = event.target.value;
		filterValue = filterValue.trim();
		filterValue = filterValue.toLowerCase();
		this.dataSource.filter = filterValue;
	}

	queryCount = this.reportService.querySearchCount;

	onArtifactToggled(artifact: Artifact) {
		this.selection.toggle(artifact);
	}

	ngAfterViewInit() {
		this.dataSource.paginator = this.paginator;
		this.dataSource.sort = this.sort;
	}

	artifact = this.reportService.artifact;

	artifactData = this.artifact.pipe(
		switchMap((artifact) => of(artifact.workflows)),
		tap((data) => (this.dataSource.data = data))
	);

	setPaginatorState(event: PageEvent) {
		this.page = event.pageIndex;
		this.pageSize = event.pageSize;
	}

	set page(page: number) {
		this.reportService.page = page;
	}

	set pageSize(pageSize: number) {
		this.reportService.pageSize = pageSize;
	}

	getTestTd(test: test) {
		var max = 50;
		var trimmed = test.name.slice(test.name.lastIndexOf('.') + 1);
		if (trimmed.length > max) {
			trimmed = '...' + trimmed.slice(-max);
		}
		return trimmed;
	}

	filterFunction(data: workflow, filter: string): boolean {
		let searchTerms = JSON.parse(filter);
		return (
			data.workflowID.toLowerCase().indexOf(searchTerms.workflowID) !== -1
		);
	}

	allRowsExpanded: boolean = false;

	toggleRow(element: { expanded: boolean }) {
		element.expanded = !element.expanded;
	}

	manageAllRows(flag: boolean) {
		this.allRowsExpanded = flag;
	}

	updateRowsforCSV(results: Array<workflow>) {
		var rows = [];
		for (var i = 0; i < results.length; i++) {
			var workflow = results[i];
			var row = this.newRow(workflow);
			rows.push(row);
			if (workflow.requirements.length) {
				for (var k = 0; k < workflow.requirements.length; k++) {
					if (k) {
						row = this.newRow(workflow);
						rows.push(row);
					}
					var req = workflow.requirements[k];
					row.requirement = req.name;
					if (req.tests.length) {
						row.test = this.getTestTd(req.tests[0]);
						for (var j = 1; j < req.tests.length; j++) {
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
		let array = typeof rows != 'object' ? JSON.parse(rows) : rows;
		let str = '';
		let column = 'S.No,';
		let newHeaders = [
			'WorkFlow ID',
			'Program',
			'Build',
			'Subsystem',
			'State',
			'Type',
			'Enhancement',
			'Description',
			'Requirement',
			'Test',
		];

		for (let index in newHeaders) {
			column += newHeaders[index] + ',';
		}
		column = column.slice(0, -1);
		str += column + '\r\n';
		for (let i = 0; i < array.length; i++) {
			let line = i + 1 + '';
			for (let index in headerList) {
				let head = headerList[index];

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

	downloadAsCsvFile(csvData: string) {
		let blob = new Blob(['\ufeff' + csvData], {
			type: 'text/csv;charset=utf-8;',
		});
		let dwldLink = document.createElement('a');
		let url = URL.createObjectURL(blob);
		dwldLink.setAttribute('href', url);
		dwldLink.setAttribute('download', 'TraceReport.csv');
		dwldLink.style.visibility = 'hidden';
		document.body.appendChild(dwldLink);
		dwldLink.click();
		document.body.removeChild(dwldLink);
	}

	exportDataAsCsv() {
		let arrHeader = [
			'workflowID',
			'program',
			'build',
			'subsystem',
			'state',
			'changeType',
			'enhancement',
			'description',
			'requirement',
			'test',
		];
		var updatedRows = this.updateRowsforCSV(this.dataSource.data);
		let csvData = this.convertRowsToStringForCSV(updatedRows, arrHeader);
		this.downloadAsCsvFile(csvData);
	}
}
