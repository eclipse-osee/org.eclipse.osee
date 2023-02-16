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
import { Component, OnInit } from '@angular/core';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { combineLatest, from, iif, of } from 'rxjs';
import { filter, map, scan, startWith, switchMap, tap } from 'rxjs/operators';
import { applic } from '@osee/shared/types/applicability';
import {
	ApplicabilityListService,
	connection,
	ConnectionService,
	MimReport,
	ReportsService,
} from '@osee/messaging/shared';
import { UiService } from '../../../ple-services/ui/ui.service';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { BranchPickerComponent } from '@osee/shared/components';
import { MatInputModule } from '@angular/material/input';

@Component({
	selector: 'osee-messaging-reports',
	templateUrl: './reports.component.html',
	styleUrls: ['./reports.component.scss'],
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		RouterLink,
		NgFor,
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatSelectModule,
		MatOptionModule,
		MatCheckboxModule,
		MatButtonModule,
		BranchPickerComponent,
	],
})
export class ReportsComponent implements OnInit {
	constructor(
		private route: ActivatedRoute,
		private routerState: UiService,
		private reportsService: ReportsService,
		private connectionService: ConnectionService,
		private applicService: ApplicabilityListService
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.routerState.idValue = params.get('branchId') || '';
			this.routerState.typeValue = params.get('branchType') || '';
		});
	}

	selectedReport: MimReport | undefined = undefined;
	selectedApplic: applic = { id: '-1', name: 'None' };

	branchId = this.reportsService.branchId;
	branchType = this.reportsService.branchType;
	reports = this.reportsService.getReports();

	webReportRoutes = combineLatest([
		this.reportsService.diffReportRoute,
		this.reportsService.nodeTraceReportRoute,
	]).pipe(
		map(([diffRoute, nodeTraceRoute]) => ({
			diffRoute: diffRoute,
			nodeTraceRoute: nodeTraceRoute,
		}))
	);

	reportSelectionText = this.reports.pipe(
		switchMap((reports) =>
			iif(
				() => reports.length > 0,
				of('Select a Report'),
				of('No reports available')
			)
		)
	);

	connections = this.branchId.pipe(
		filter((v) => v !== ''),
		switchMap((branchId) => this.connectionService.getConnections(branchId))
	);

	connectionSelectionText = this.connections.pipe(
		switchMap((connections) =>
			iif(
				() => connections.length > 0,
				of('Select a Connection'),
				of('No connections available')
			)
		)
	);

	applicViews = this.branchId.pipe(
		filter((v) => v !== ''),
		switchMap((branchId) =>
			this.applicService.getViews(branchId).pipe(
				switchMap((applics) =>
					from(applics).pipe(
						startWith({ id: '-1', name: 'None' } as applic),
						scan((acc, curr) => {
							acc.push(curr);
							return acc;
						}, [] as applic[])
					)
				)
			)
		)
	);

	selectReport(event: MatSelectChange) {
		this.selectedReport = event.value;
	}

	getSelectedReport() {
		this.reportsService
			.downloadReport(this.selectedReport, this.selectedApplic.id)
			.subscribe();
	}

	selectConnection(event: MatSelectChange) {
		this.selectedConnection = event.value;
	}

	selectFile(event: Event) {
		const target = event.target as HTMLInputElement;
		if (target.files && target.files.length > 0) {
			const file: File = target.files[0];
			this.reportsService.RequestBodyFile = file;
		}
	}

	get requestBody() {
		return this.reportsService.requestBody.getValue();
	}

	set requestBody(requestBody: string) {
		this.reportsService.RequestBody = requestBody;
	}

	get selectedConnection() {
		return this.reportsService.connection.getValue() as connection;
	}

	set selectedConnection(connection: connection) {
		this.reportsService.Connection = connection;
	}

	get includeDiff() {
		return this.reportsService.includeDiff.value;
	}

	set includeDiff(value: boolean) {
		this.reportsService.IncludeDiff = value;
	}
}

export default ReportsComponent;
