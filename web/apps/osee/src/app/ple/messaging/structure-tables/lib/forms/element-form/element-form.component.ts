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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
	Component,
	Input,
	OnChanges,
	OnInit,
	Output,
	SimpleChanges,
	WritableSignal,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import {
	MatFormField,
	MatHint,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import {
	MatSlideToggle,
	MatSlideToggleChange,
} from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import {
	MimQuery,
	PlatformTypeQuery,
	andNameQuery,
	andQuery,
} from '@osee/messaging/shared/query';
import {
	CurrentStructureService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import {
	ElementDialog,
	PlatformType,
	element,
	enumeration,
	newPlatformTypeDialogReturnData,
} from '@osee/messaging/shared/types';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { applic } from '@osee/shared/types/applicability';
import {
	BehaviorSubject,
	Subject,
	combineLatest,
	concatMap,
	debounceTime,
	distinctUntilChanged,
	filter,
	from,
	iif,
	map,
	of,
	shareReplay,
	skip,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { DefaultAddElementDialog } from '../../dialogs/add-element-dialog/add-element-dialog.default';
import { PlatformTypeQueryComponent } from '../../dialogs/platform-type-query/platform-type-query.component';
import { RemoveArrayElementsDialogComponent } from '../../dialogs/remove-array-elements-dialog/remove-array-elements-dialog.component';

const _platformTypeStates = ['SELECT', 'QUERY', 'CREATE'] as const;
type platformTypeStates =
	(typeof _platformTypeStates)[keyof typeof _platformTypeStates];

@Component({
	selector: 'osee-element-form',
	standalone: true,
	imports: [
		AsyncPipe,
		NgTemplateOutlet,
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatHint,
		CdkTextareaAutosize,
		MatSlideToggle,
		MatButton,
		MatTooltip,
		MatIcon,
		MatSelect,
		MatOption,
		MatSuffix,
		MatIconButton,
		MatDivider,
		MatProgressSpinner,
		ApplicabilitySelectorComponent,
		PlatformTypeQueryComponent,
		NewTypeFormComponent,
	],
	templateUrl: './element-form.component.html',
	styles: [],
})
export class ElementFormComponent implements OnInit, OnChanges {
	@Input() data: ElementDialog = {
		id: '',
		name: '',
		element: {},
		type: new PlatformTypeSentinel(),
		mode: 'add',
		allowArray: true,
		arrayChild: false,
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
		arrayChild: false,
	};
	@Output() dataChange = this._dataChange.pipe(skip(1));
	private queryMode = new BehaviorSubject<boolean>(false);
	private query = new BehaviorSubject<MimQuery<PlatformType> | undefined>(
		undefined
	);

	elementExample: WritableSignal<string[]> = signal([]);
	arrayExample: WritableSignal<string[]> = signal([]);

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
	arrayIndexOrders = this.structures.arrayIndexOrders;
	ngOnInit(): void {
		this.query.next(new PlatformTypeQuery());
		this.queryMode.next(true);
		this.updateArrayExample();
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
					value.element.platformType = value.type;
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
			arrayChild: true,
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

	compareArrayIndexOrders(a1: string, a2: string) {
		return a1 === a2;
	}

	updateArrayExample() {
		let elementExamples: string[] = [];
		let arrayExamples: string[] = [];
		const d1 = this.data.element.interfaceElementArrayIndexDelimiterOne;
		const d2 = this.data.element.interfaceElementArrayIndexDelimiterTwo;
		if (
			this.data.element.interfaceElementArrayIndexOrder === 'INNER_OUTER'
		) {
			elementExamples = [
				'ElementName' + d2 + 1,
				'ElementName' + d2 + 2,
				'ElementName' + d2 + 3,
			];
			arrayExamples = [
				'ElementName' + d1 + 1 + d2 + 1,
				'ElementName' + d1 + 2 + d2 + 1,
				'ElementName' + d1 + 3 + d2 + 1,
				'ElementName' + d1 + 1 + d2 + 2,
				'ElementName' + d1 + 2 + d2 + 2,
				'ElementName' + d1 + 3 + d2 + 2,
			];
		} else {
			elementExamples = [
				'ElementName' + d1 + 1,
				'ElementName' + d1 + 2,
				'ElementName' + d1 + 3,
			];
			arrayExamples = [
				'ElementName' + d1 + 1 + d2 + 1,
				'ElementName' + d1 + 1 + d2 + 2,
				'ElementName' + d1 + 1 + d2 + 3,
				'ElementName' + d1 + 2 + d2 + 1,
				'ElementName' + d1 + 2 + d2 + 2,
				'ElementName' + d1 + 2 + d2 + 3,
			];
		}
		this.elementExample.set(elementExamples);
		this.arrayExample.set(arrayExamples);
	}
}
