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
import { AsyncPipe } from '@angular/common';
import {
	Component,
	OnDestroy,
	OnInit,
	effect,
	inject,
	signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MatFormField,
	MatLabel,
	MatOption,
	MatSelect,
	MatSelectChange,
} from '@angular/material/select';
import { ActivatedRoute } from '@angular/router';
import { ImportService, ImportTableComponent } from '@osee/messaging/import';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import {
	connectionHeaderDetails,
	crossReferenceHeaderDetails,
	importElementHeaderDetails,
	importEnumSetHeaderDetails,
	importMessageHeaderDetails,
	importPlatformTypeHeaderDetails,
	nodeHeaderDetails,
	structureHeaderDetails,
	subMessageHeaderDetails,
} from '@osee/messaging/shared/table-headers';
import {
	transportType,
	type CrossReference,
	type ImportEnumSet,
	type ImportSummary,
	type connection,
	type elementImportToken,
	type enumeration,
	type messageToken,
	type nodeData,
	type platformTypeImportToken,
	type subMessage,
	getTransportTypeSentinel,
} from '@osee/messaging/shared/types';
import { TransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown';
import { UiService } from '@osee/shared/services';
import { BehaviorSubject, OperatorFunction, from, iif, of } from 'rxjs';
import { concatMap, filter, map, reduce, switchMap, tap } from 'rxjs/operators';

@Component({
	selector: 'osee-import',
	templateUrl: './import.component.html',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatButton,
		ImportTableComponent,
		MessagingControlsComponent,
		TransportTypeDropdownComponent,
	],
})
export class ImportComponent implements OnInit, OnDestroy {
	private route = inject(ActivatedRoute);
	private routerState = inject(UiService);
	private importService = inject(ImportService);

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.routerState.idValue = params.get('branchId') || '';
			this.routerState.typeValue =
				(params.get('branchType') as 'working' | 'baseline' | '') || '';
		});
	}

	ngOnDestroy(): void {
		this.importService.reset();
	}

	showSummary = false;
	selectedImportOption = toSignal(this.importService.selectedImportOption, {
		initialValue: {
			id: '-1',
			name: '',
			url: '',
			connectionRequired: false,
			transportTypeRequired: false,
		},
	});
	branchId = this.importService.branchId;
	branchType = this.importService.branchType;
	importSummary = this.importService.importSummary.pipe(
		tap(() => (this.showSummary = true))
	);
	importOptions = this.importService.importOptions;
	importSuccess = toSignal(this.importService.importSuccess);
	selectedImportFileName = this.importService.importFile.pipe(
		switchMap((file) =>
			iif(() => file === undefined, of(''), of(file?.name))
		)
	);
	transportType = signal<transportType>(getTransportTypeSentinel());

	private _transportTypeEffect = effect(
		() => (this.importService.TransportType = this.transportType())
	);

	_selectedConnection = new BehaviorSubject<connection | undefined>(
		undefined
	);

	connections = this.importService.connections;

	importOptionSelectionText = this.importOptions.pipe(
		map((options) =>
			options.length > 0
				? 'Select an import type'
				: 'No import types available'
		)
	);

	connectionSelectionText = this.connections.pipe(
		map((connections) =>
			connections.length > 0
				? 'Select a Connection'
				: 'No connections available'
		)
	);

	selectImportOption(event: MatSelectChange) {
		this.importService.reset();
		this.SelectedConnection = undefined;
		this.importService.SelectedImportOption = event.value;
		this.showSummary = false;
	}

	selectFile(event: Event) {
		const target = event.target as HTMLInputElement;
		if (target.files && target.files.length > 0) {
			const file: File = target.files[0];
			this.importService.ImportFile = file;
			this.importService.ImportSuccess = undefined;
			this.showSummary = false;
			target.value = '';
		}
	}

	startImportSummary() {
		this.importService.ImportInProgress = true;
	}

	performImport() {
		this.importService.performImport();
	}

	nodes = this.importSummary.pipe(
		filter((v) => v !== undefined) as OperatorFunction<
			ImportSummary | undefined,
			ImportSummary
		>,
		switchMap((summary) => {
			return of(summary.nodes);
		})
	);

	enumSets = this.importSummary.pipe(
		filter((v) => v !== undefined) as OperatorFunction<
			ImportSummary | undefined,
			ImportSummary
		>,
		switchMap((summary) =>
			of(summary?.enumSetEnumRelations).pipe(
				switchMap((relations) =>
					of(summary?.enums).pipe(
						concatMap((enums) =>
							from(summary?.enumSets).pipe(
								concatMap((enumSet) =>
									of(enumSet.id).pipe(
										filter(
											(enumSetId) =>
												enumSetId !== undefined
										) as OperatorFunction<
											string | undefined,
											string
										>,
										concatMap((enumSetId) =>
											of(relations[enumSetId]).pipe(
												filter(
													(v) => v !== undefined
												) as OperatorFunction<
													string[] | undefined,
													string[]
												>,
												concatMap((enumRels) =>
													from(enumRels).pipe(
														map((rel) =>
															enums.find(
																(e) =>
																	e.id === rel
															)
														),
														filter(
															(v) =>
																v !== undefined
														) as OperatorFunction<
															| enumeration
															| undefined,
															enumeration
														>,
														map(
															(enumeration) =>
																enumeration.ordinal +
																' = ' +
																enumeration.name
														)
													)
												),
												reduce(
													(acc, curr) => [
														...acc,
														curr,
													],
													[] as string[]
												),
												concatMap((enumerations) =>
													of({
														name: enumSet.name,
														enums: enumerations,
														applicability:
															enumSet.applicability,
													} as ImportEnumSet)
												)
											)
										)
									)
								)
							)
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as ImportEnumSet[]
						)
					)
				)
			)
		)
	);

	nodeHeaderDetails = nodeHeaderDetails;
	nodeHeaders: (keyof nodeData)[] = [
		'name',
		'nameAbbrev',
		'interfaceNodeCodeGenName',
		'interfaceNodeType',
		'interfaceNodeNumber',
		'interfaceNodeGroupId',
		'description',
		'interfaceNodeToolUse',
		'interfaceNodeCodeGen',
		'interfaceNodeBuildCodeGen',
		'notes',
		'applicability',
	];

	connectionHeaderDetails = connectionHeaderDetails;
	connectionHeaders: (keyof connection)[] = [
		'name',
		'description',
		'applicability',
	];

	messageHeaderDetails = importMessageHeaderDetails;
	messageHeaders: (keyof messageToken)[] = [
		'name',
		'description',
		'interfaceMessageRate',
		'interfaceMessagePeriodicity',
		'interfaceMessageWriteAccess',
		'interfaceMessageType',
		'interfaceMessageNumber',
		'applicability',
	];

	submessageHeaderDetails = subMessageHeaderDetails;
	submessageHeaders: (keyof subMessage)[] = [
		'name',
		'description',
		'interfaceSubMessageNumber',
		'applicability',
	];

	structureHeaderDetails = structureHeaderDetails;
	structureHeaders: string[] = [
		'name',
		'nameAbbrev',
		'description',
		'interfaceMaxSimultaneity',
		'interfaceMinSimultaneity',
		'interfaceTaskFileType',
		'interfaceStructureCategory',
		'applicability',
	];

	elementHeaderDetails = importElementHeaderDetails;
	elementHeaders: (keyof elementImportToken)[] = [
		'name',
		'description',
		'interfaceElementAlterable',
		'interfaceElementIndexStart',
		'interfaceElementIndexEnd',
		'notes',
		'enumLiteral',
		'interfaceDefaultValue',
	];

	platformTypeHeaderDetails = importPlatformTypeHeaderDetails;
	platformTypeHeaders: (keyof platformTypeImportToken)[] = [
		'name',
		'description',
		'interfaceLogicalType',
		'interfacePlatformTypeBitSize',
		'interfacePlatformTypeMinval',
		'interfacePlatformTypeMaxval',
		'interfacePlatformTypeValidRangeDescription',
		'interfacePlatformTypeUnits',
		'interfaceDefaultValue',
	];

	enumSetHeaderDetails = importEnumSetHeaderDetails;
	enumSetHeaders: (keyof ImportEnumSet)[] = [
		'name',
		'enums',
		'applicability',
	];

	crossReferenceHeaderDetails = crossReferenceHeaderDetails;
	crossRefHeaders: (keyof CrossReference)[] = [
		'name',
		'crossReferenceValue',
		'crossReferenceArrayValues',
	];

	setSelectedConnection(connection: connection) {
		this.SelectedConnection = connection;
	}

	get selectedConnection() {
		return this._selectedConnection;
	}

	set SelectedConnection(connection: connection | undefined) {
		this._selectedConnection.next(connection);
		this.SelectedConnectionId = connection?.id || '';
	}

	get selectedConnectionId() {
		return this.importService.selectedConnectionId;
	}

	set SelectedConnectionId(id: string) {
		this.importService.SelectedConnectionId = id;
	}

	get selectedFile() {
		return this.importService.importFile;
	}
}

export default ImportComponent;
