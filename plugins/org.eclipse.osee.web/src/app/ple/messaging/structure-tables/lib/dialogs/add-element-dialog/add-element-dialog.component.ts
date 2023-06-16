/*********************************************************************
 * Copyright (c) 2021 Boeing
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
	AfterViewInit,
	Component,
	Inject,
	OnInit,
	ViewChild,
} from '@angular/core';
import {
	MatDialog,
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { BehaviorSubject, combineLatest, from, iif, of, Subject } from 'rxjs';
import {
	concatMap,
	debounceTime,
	delay,
	distinctUntilChanged,
	filter,
	map,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { applic } from '@osee/shared/types/applicability';
import { UiService } from '@osee/shared/services';
import { AddElementDialog } from './add-element-dialog';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PlatformTypeQueryComponent } from '../platform-type-query/platform-type-query.component';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	PlatformTypeQuery,
	andQuery,
	andNameQuery,
	MimQuery,
} from '@osee/messaging/shared/query';
import type {
	element,
	newPlatformTypeDialogReturnData,
	enumeration,
	PlatformType,
} from '@osee/messaging/shared/types';
import {
	MatOptionLoadingComponent,
	ApplicabilitySelectorComponent,
} from '@osee/shared/components';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import {
	CurrentStructureService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

@Component({
	selector: 'osee-messaging-add-element-dialog',
	templateUrl: './add-element-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogModule,
		MatStepperModule,
		MatButtonModule,
		FormsModule,
		MatFormFieldModule,
		MatOptionLoadingComponent,
		MatInputModule,
		MatSlideToggleModule,
		MatIconModule,
		MatDividerModule,
		MatProgressSpinnerModule,
		PlatformTypeQueryComponent,
		NewTypeFormComponent,
		MatSelectModule,
		MatOptionModule,
		MatTooltipModule,
		MatAutocompleteModule,
		AsyncPipe,
		NgIf,
		NgFor,
		ApplicabilitySelectorComponent,
	],
})
export class AddElementDialogComponent implements OnInit, AfterViewInit {
	@ViewChild(MatStepper) private _internalStepper!: MatStepper;

	private _afterViewInit = new Subject<boolean>();

	private _moveToNextStep = this._afterViewInit.pipe(
		take(1),
		filter((_) => this._internalStepper !== undefined),
		delay(1),
		tap((v) => {
			if (
				this.data.element.id !== '' &&
				this.data.element.id !== '-1' &&
				this._isFullElement(this.data.element)
			) {
				this.data.element.id = '-1';
				if (this.selectedElement) {
					this.selectedElement.id = '-1';
					this._cleanElement(this.selectedElement);
				}
				this._cleanElement(this.data.element);
				this.data.type = this.data.element.platformType;
				this._internalStepper.next();
			}
		})
	);
	loadingTypes = false;
	types = this.structures.types;
	typeDialogOpen = false;
	searchOpen = false;
	private queryMode = new BehaviorSubject<boolean>(false);
	private query = new BehaviorSubject<MimQuery<PlatformType> | undefined>(
		undefined
	);

	paginationSize = 10;
	elementSearch = new BehaviorSubject<string>('');
	selectedElement: element | undefined = undefined;

	availableElements = this.elementSearch.pipe(
		debounceTime(250),
		map(
			(search) => (pageNum: number | string) =>
				this.structures.getPaginatedElementsByName(
					search,
					this.paginationSize,
					pageNum
				)
		)
	);

	availableElementsCount = this.elementSearch.pipe(
		debounceTime(250),
		switchMap((search) => this.structures.getElementsByNameCount(search))
	);

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
	constructor(
		public dialog: MatDialog,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structures: CurrentStructureService,
		public dialogRef: MatDialogRef<AddElementDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: AddElementDialog,
		private typeDialogService: TypesUIService,
		private _ui: UiService
	) {
		if (
			this.data.element.id !== '' &&
			this.data.element.id !== '-1' &&
			this._isFullElement(this.data.element)
		) {
			this.selectExistingElement(this.data.element);
		}
		this._moveToNextStep.subscribe();
	}
	ngAfterViewInit(): void {
		this._afterViewInit.next(true);
	}

	private _isFullElement(
		value: Partial<element> | element | Required<element>
	): value is element {
		return value?.id !== undefined;
	}

	ngOnInit(): void {
		this.query.next(new PlatformTypeQuery());
		this.queryMode.next(true);
	}

	applySearchTerm(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.elementSearch.next(value);
	}

	createNew() {
		this.data.element.id = '-1';
		this.selectedElement = undefined;
	}
	selectExistingElement(element: element) {
		this.selectedElement = element;
	}

	getElementOptionToolTip(element: element) {
		let tooltip = '';
		if (element.logicalType) {
			tooltip += element.logicalType + '\n\n';
		}
		tooltip += element.description;
		return tooltip;
	}

	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}
	moveToReview(stepper: MatStepper) {
		if (this.selectedElement) {
			this.data.element = this.selectedElement;
		}
		this.moveToStep(3, stepper);
	}
	openPlatformTypeDialog(event?: Event) {
		event?.stopPropagation();
		this.typeDialogOpen = !this.typeDialogOpen;
		if (this.typeDialogOpen) {
			this.searchOpen = false;
		}
	}
	resetDialog() {
		this.searchOpen = false;
		this.typeDialogOpen = false;
	}
	openSearch(event?: Event) {
		event?.stopPropagation();
		this.searchOpen = !this.searchOpen;
		if (this.searchOpen) {
			this.typeDialogOpen = false;
		}
	}
	receivePlatformTypeData(value: newPlatformTypeDialogReturnData) {
		this.typeDialogOpen = !this.typeDialogOpen;
		const {
			platformType: fields,
			createEnum,
			enumSet,
			...enumData
		} = value;
		this.mapTo(fields, createEnum, enumData)
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
									this.loadingTypes = true;
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
	/**
	 *
	 * @TODO replace enumData with actual enum
	 */
	mapTo(
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
	compareTypes(o1: PlatformType, o2: PlatformType) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}

	updateEnumLiteral() {
		this.data.element.enumLiteral =
			this.data.type.enumSet?.description || '';
	}
	receiveQuery(query: PlatformTypeQuery) {
		//close the dialog
		this.searchOpen = !this.searchOpen;
		//switch observable to query type
		this.queryMode.next(true);
		//set query to query
		this.query.next(query);
	}

	private _cleanElement(_element: element) {
		delete _element.beginByte;
		delete _element.beginWord;
		delete _element.endByte;
		delete _element.endWord;
		delete _element.autogenerated;
		delete _element.logicalType;
		delete _element.interfacePlatformTypeDescription;
		delete _element.units;
		delete _element.interfacePlatformTypeMaxval;
		delete _element.interfacePlatformTypeMinval;
		delete _element.elementSizeInBits;
		delete _element.elementSizeInBytes;
	}
}
