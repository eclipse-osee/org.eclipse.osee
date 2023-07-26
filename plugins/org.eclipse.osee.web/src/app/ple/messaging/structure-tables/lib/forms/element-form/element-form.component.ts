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
	Component,
	Inject,
	Input,
	OnChanges,
	OnInit,
	Output,
	SimpleChanges,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import {
	andNameQuery,
	andQuery,
	MimQuery,
	PlatformTypeQuery,
} from '@osee/messaging/shared/query';
import {
	element,
	ElementDialog,
	enumeration,
	newPlatformTypeDialogReturnData,
	PlatformType,
} from '@osee/messaging/shared/types';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	switchMap,
	iif,
	distinctUntilChanged,
	map,
	of,
	shareReplay,
	Subject,
	skip,
	tap,
	concatMap,
	filter,
	from,
	take,
} from 'rxjs';
import {
	CurrentStructureService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	MatSlideToggleChange,
	MatSlideToggleModule,
} from '@angular/material/slide-toggle';
import { MatSelectModule } from '@angular/material/select';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { applic } from '@osee/shared/types/applicability';
import { UiService } from '@osee/shared/services';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { PlatformTypeQueryComponent } from '../../dialogs/platform-type-query/platform-type-query.component';
import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { DefaultAddElementDialog } from '../../dialogs/add-element-dialog/add-element-dialog.default';
import { RemoveArrayElementsDialogComponent } from '../../dialogs/remove-array-elements-dialog/remove-array-elements-dialog.component';

const _platformTypeStates = ['SELECT', 'QUERY', 'CREATE'] as const;
type platformTypeStates =
	(typeof _platformTypeStates)[keyof typeof _platformTypeStates];

@Component({
	selector: 'osee-element-form',
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatSlideToggleModule,
		MatTooltipModule,
		MatDividerModule,
		MatSelectModule,
		MatIconModule,
		MatButtonModule,
		MatTooltipModule,
		MatProgressSpinnerModule,
		ApplicabilitySelectorComponent,
		PlatformTypeQueryComponent,
		NewTypeFormComponent,
	],
	templateUrl: './element-form.component.html',
	styleUrls: ['./element-form.component.sass'],
})
export class ElementFormComponent implements OnInit, OnChanges {
	@Input() data: ElementDialog = {
		id: '',
		name: '',
		element: {},
		type: new PlatformTypeSentinel(),
		mode: 'add',
		allowArray: true,
	};
	//do not reference
	@Input() reset!: number | null;
	private _dataChange = new Subject<ElementDialog>();
	private _internalData: ElementDialog = {
		id: '',
		name: '',
		element: {},
		type: new PlatformTypeSentinel(),
		mode: 'add',
		allowArray: true,
	};
	@Output() dataChange = this._dataChange.pipe(skip(1));
	private queryMode = new BehaviorSubject<boolean>(false);
	private query = new BehaviorSubject<MimQuery<PlatformType> | undefined>(
		undefined
	);

	protected _typeState = new BehaviorSubject<platformTypeStates>('SELECT');
	types = this.structures.types;
	availableTypes = combineLatest([this.queryMode, this.query]).pipe(
		debounceTime(100),
		switchMap(([mode, query]) =>
			iif(
				() => mode === true && query !== undefined,
				this.structures
					.query<PlatformType>(query as MimQuery<PlatformType>)
					.pipe(
						distinctUntilChanged(),
						map((result) => {
							if (result.length === 1) {
								this.data.type = result[0];
							}
							return result;
						})
					),
				of(undefined)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	platformTypeState = this.availableTypes.pipe(
		switchMap((types) =>
			iif(
				() => types !== undefined,
				iif(
					() => types !== undefined && types.length === 1,
					of(
						types !== undefined && types[0].name + ' selected.'
					).pipe(
						tap((v) => {
							this.data.element.enumLiteral =
								this.data.type.enumSet?.description || '';
						})
					),
					iif(
						() =>
							types !== undefined &&
							types.length !== 1 &&
							this.data.type.id !== '',
						of('No exact match found.'),
						iif(
							() =>
								types !== undefined &&
								types.length !== 1 &&
								this.data.type.id === '',
							of(''),
							of(this.data.type.name + ' selected.').pipe(
								tap((v) => {
									this.data.element.enumLiteral =
										this.data.type.enumSet?.description ||
										'';
								})
							)
						)
					)
				),
				of('')
			)
		)
	);
	ngOnInit(): void {
		this.query.next(new PlatformTypeQuery());
		this.queryMode.next(true);
	}
	ngOnChanges(changes: SimpleChanges) {
		if (
			changes.data !== undefined &&
			changes.data.previousValue !== changes.data.currentValue &&
			changes.data.currentValue !== undefined &&
			changes.data.currentValue.type !== undefined &&
			(changes.data.previousValue !== undefined &&
			changes.data.previousValue.type !== undefined
				? changes.data.currentValue.type !==
				  changes.data.previousValue.type
				: true) &&
			changes.data.currentValue.type.name !== ''
		) {
			const andQuery = new andNameQuery(
				changes.data.currentValue.type.name
			);
			this.query.next(new PlatformTypeQuery(undefined, [andQuery]));
			this.queryMode.next(true);
		}
		if (changes.reset) {
			this._typeState.next('SELECT');
		}
	}
	constructor(
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structures: CurrentStructureService,
		private dialog: MatDialog,
		private typeDialogService: TypesUIService,
		private _ui: UiService
	) {}
	updateData() {
		if (this._internalData !== this.data) {
			this._dataChange.next(this.data);
			this._internalData = this.data;
		}
	}

	setPlatformType(type: PlatformType) {
		if (this.data.element.id === '' || this.data.element.id === '-1') {
			this.data.element.platformType = type;
		}
		this.updateEnumLiteral();
	}

	updateEnumLiteral() {
		this.data.element.enumLiteral =
			this.data.type.enumSet?.description || '';
		this.updateData();
	}
	compareTypes(o1: PlatformType, o2: PlatformType) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}

	openSearch(event?: Event) {
		event?.stopPropagation();
		this._typeState.next('QUERY');
	}

	openPlatformTypeDialog(event?: Event) {
		event?.stopPropagation();
		this._typeState.next('CREATE');
	}

	receivePlatformTypeData(value: newPlatformTypeDialogReturnData) {
		this._typeState.next('SELECT');
		const {
			platformType: fields,
			createEnum,
			enumSet,
			...enumData
		} = value;
		this.createPlatformType(fields, createEnum, enumData)
			.pipe(
				concatMap((newElement) =>
					from(newElement.results.ids).pipe(
						concatMap((createdElement) =>
							this.structures.getType(createdElement).pipe(
								filter(
									(value) =>
										value.id !== '-1' && value.id !== ''
								),
								tap((v) => {
									this._ui.updated = true;
									this.data.type =
										v as Required<PlatformType>;
									if (
										(v as Required<PlatformType>) &&
										v.interfaceLogicalType === 'enumeration'
									) {
										this.data.element.enumLiteral =
											value.enumSetDescription;
									}
									const queries: andQuery[] = [];
									queries.push(new andNameQuery(v.name));
									const query = new PlatformTypeQuery(
										undefined,
										queries
									);
									this.queryMode.next(true);
									this.query.next(query);
								})
							)
						)
					)
				)
			)
			.subscribe();
	}
	createPlatformType(
		results: Partial<PlatformType>,
		newEnum: boolean,
		enumData: {
			enumSetId: string;
			enumSetName: string;
			enumSetDescription: string;
			enumSetApplicability: applic;
			enums: enumeration[];
		}
	) {
		return this.typeDialogService.createType(
			results,
			enumData.enumSetId !== '1' && enumData.enumSetId !== '',
			enumData
		);
	}

	receiveQuery(query: PlatformTypeQuery) {
		//close the dialog
		this._typeState.next('SELECT');
		//switch observable to query type
		this.queryMode.next(true);
		//set query to query
		this.query.next(query);
	}

	toggleArrayHeader(val: MatSlideToggleChange) {
		if (
			!val.checked &&
			this.data.element.arrayElements &&
			this.data.element.arrayElements.length > 0
		) {
			const dialogRef = this.dialog.open(
				RemoveArrayElementsDialogComponent
			);
			dialogRef
				.afterClosed()
				.pipe(
					take(1),
					tap((res) => {
						if (res === 'ok') {
							this.data.element.arrayElements = [];
							this.data.element.interfaceElementArrayHeader =
								false;
							this.data.element.platformType =
								new PlatformTypeSentinel();
							this.data.type = new PlatformTypeSentinel();
							this.receiveQuery(new PlatformTypeQuery());
						} else {
							this.data.element.interfaceElementArrayHeader =
								true;
						}
					})
				)
				.subscribe();
		}
	}

	addArrayElement() {
		const dialogData = new DefaultAddElementDialog(
			'',
			this.data.element.name + ' Array',
			undefined,
			'add',
			false
		);
		let dialogRef = this.dialog.open(AddElementDialogComponent, {
			data: dialogData,
			minWidth: '80%',
		});
		let createElement = dialogRef.afterClosed().pipe(
			take(1),
			filter(
				(val) =>
					(val !== undefined || val !== null) &&
					val?.element !== undefined
			),
			tap((value: ElementDialog) => {
				if (this.data.element.arrayElements) {
					this.data.element.arrayElements.push(
						value.element as element
					);
				}
			})
		);
		createElement.subscribe();
	}

	removeFromArray(element: element) {
		if (this.data.element.arrayElements) {
			const index = this.data.element.arrayElements.findIndex(
				(e) => e.name === element.name && e.id === element.id
			);
			this.data.element.arrayElements.splice(index, 1);
		}
	}

	async editArrayElement(element: element) {
		const dialogData: ElementDialog = {
			id: '',
			name: '',
			element: element,
			type: element.platformType,
			mode: 'edit',
			allowArray: false,
		};
		// Lazy loading the Edit Element Dialog to avoid a circular dependency
		const { EditElementDialogComponent } = await import(
			'@osee/messaging/structure-tables'
		);
		let dialogRef = this.dialog.open(EditElementDialogComponent, {
			data: dialogData,
		});
		dialogRef.afterClosed().pipe(take(1)).subscribe();
	}
}
