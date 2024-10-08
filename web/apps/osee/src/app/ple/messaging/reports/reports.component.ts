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
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatAnchor, MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import {
	ConnectionService,
	ReportsService,
	ValidationUiService,
} from '@osee/messaging/shared/services';
import {
	connectionValidationResultSentinel,
	type MimReport,
	type connection,
} from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { applic } from '@osee/applicability/types';
import { Subject, combineLatest, iif, of } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { ConnectionValidationResultsComponent } from './lib/connection-validation-results/connection-validation-results.component';
import { ShowErrorsCheckboxComponent } from './lib/show-errors-checkbox/show-errors-checkbox.component';
import { ViewSelectorComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-messaging-reports',
	templateUrl: './reports.component.html',
	standalone: true,
	imports: [
		AsyncPipe,
		RouterLink,
		NgTemplateOutlet,
		FormsModule,
		MessagingControlsComponent,
		MatAnchor,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatInput,
		MatCheckbox,
		MatButton,
		MatIcon,
		ViewSelectorComponent,
		ConnectionValidationResultsComponent,
		ShowErrorsCheckboxComponent,
	],
})
export class ReportsComponent implements OnInit {
	private route = inject(ActivatedRoute);
	private routerState = inject(UiService);
	private reportsService = inject(ReportsService);
	private validationService = inject(ValidationUiService);
	private connectionService = inject(ConnectionService);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		// When the branch changes, need to reset the validation results
		this.reportsService.branchId
			.pipe(
				tap(() => this.startConnectionValidation.next(false)),
				takeUntilDestroyed()
			)
			.subscribe();
	}

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.routerState.idValue = params.get('branchId') || '';
			this.routerState.typeValue =
				(params.get('branchType') as 'working' | 'baseline' | '') || '';
		});
	}

	bypassValidation = signal(false);

	selectedReport: MimReport | undefined = undefined;

	selectedApplic = signal<applic>({ id: '-1', name: 'None' });
	private _applicEffect = effect(
		() => {
			// Reset validation results when the selected view changes
			this.selectedApplic();
			this.resetValidation();
		},
		{ allowSignalWrites: true }
	);

	branchType = this.reportsService.branchType;
	reports = this.reportsService.getReports();

	startConnectionValidation = new Subject<boolean>();
	connectionValidationResults = toSignal(
		this.startConnectionValidation.pipe(
			switchMap((start) =>
				iif(
					() => start === true,
					this.validationService.validateConnection(
						this.selectedConnection.id || '-1',
						this.selectedApplic().id || '-1'
					),
					of(connectionValidationResultSentinel)
				)
			)
		),
		{
			initialValue: connectionValidationResultSentinel,
		}
	);

	webReportRoutes = combineLatest([
		this.reportsService.diffReportRoute,
		this.reportsService.nodeTraceReportRoute,
		this.reportsService.unreferencedReportRoute,
		this.reportsService.impactedConnectionsRoute,
	]).pipe(
		map(
			([
				diffRoute,
				nodeTraceRoute,
				unreferencedRoute,
				impactedConnectionsRoute,
			]) => ({
				diffRoute: diffRoute,
				nodeTraceRoute: nodeTraceRoute,
				unreferencedRoute: unreferencedRoute,
				impactedConnectionsRoute: impactedConnectionsRoute,
			})
		)
	);

	connections = this.branchId.pipe(
		filter((v) => v !== ''),
		switchMap((branchId) => this.connectionService.getConnections(branchId))
	);

	setShowErrorColoring(value: boolean) {
		this.reportsService.ShowErrorColoring = value;
	}

	selectReport(event: MatSelectChange) {
		this.selectedReport = event.value;
	}

	getSelectedReport() {
		this.reportsService
			.downloadReport(this.selectedReport, this.selectedApplic().id)
			.subscribe();
	}

	validateConnection() {
		this.startConnectionValidation.next(true);
	}

	resetValidation() {
		this.startConnectionValidation.next(false);
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

	get branchId() {
		return this.reportsService.branchId;
	}

	set BranchId(id: string) {
		this.reportsService.BranchId = id;
	}
}

export default ReportsComponent;
