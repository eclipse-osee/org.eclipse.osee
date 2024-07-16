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
import { AsyncPipe, KeyValuePipe, TitleCasePipe } from '@angular/common';
import {
	Component,
	Input,
	OnInit,
	Output,
	signal,
	viewChild,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MatDialog,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { TypesService } from '@osee/messaging/shared/services';
import type {
	PlatformType,
	logicalType,
	newPlatformTypeDialogReturnData,
} from '@osee/messaging/shared/types';
import { applic } from '@osee/shared/types/applicability';
import {
	BehaviorSubject,
	Observable,
	Subject,
	combineLatest,
	concatMap,
	filter,
	from,
	of,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { EnumSetFormComponent } from '../../forms/enum-set-form/enum-set-form.component';
import { LogicalTypeSelectorComponent } from '../logical-type-selector/logical-type-selector.component';
import { NewPlatformTypeFormPage2Component } from '../new-platform-type-form-page2/new-platform-type-form-page2.component';
import { NewPlatformTypeFormComponent } from '../new-platform-type-form/new-platform-type-form.component';

/**
 * Form used to create a new platform type
 */
@Component({
	selector: 'osee-new-type-form',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatStepper,
		MatStep,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatStepperNext,
		MatLabel,
		MatStepperPrevious,
		NewPlatformTypeFormComponent,
		AsyncPipe,
		TitleCasePipe,
		KeyValuePipe,
		EnumSetFormComponent,
		LogicalTypeSelectorComponent,
		NewPlatformTypeFormPage2Component,
	],
	templateUrl: './new-type-form.component.html',
	styles: [':host{width: 100%;height: 100%;}'],
})
export class NewTypeFormComponent implements OnInit {
	logicalTypeSelector = viewChild(LogicalTypeSelectorComponent);

	selectedLogicalType = signal<logicalType>({
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	});
	logicalTypes: Observable<logicalType[]> = this.typesService.logicalTypes;
	constructor(
		private dialog: MatDialog, //used for checking and closing dialog if in case where this form can perform that action
		private typesService: TypesService
	) {}
	/**
	 * Pre-populate the field with some data, currently only does logical type
	 */
	@Input() preFillData?: PlatformType[];

	_typeFormState = new Subject<newPlatformTypeDialogReturnData>();

	private _dialogToClose = new BehaviorSubject<
		MatDialogRef<unknown, unknown> | undefined
	>(undefined);

	closeDialog = combineLatest([
		this._dialogToClose,
		this._typeFormState,
	]).pipe(
		filter((v) => v[0] !== undefined),
		take(1),
		tap(([dialog, state]) => dialog !== undefined && dialog.close(state))
	);

	updateOuterComponent = new Subject<boolean>();

	/**
	 * Output event to notify that the dialog has been closed
	 * Also contains the results of the dialog.
	 * @type {newPlatformTypeDialogReturnData}
	 */
	@Output() typeFormState = combineLatest([
		this._typeFormState,
		this.updateOuterComponent,
	]).pipe(switchMap(([componentState, update]) => of(componentState)));

	ngOnInit(): void {
		if (this.preFillData !== undefined && this.preFillData.length > 0) {
			this.logicalTypes
				.pipe(
					concatMap((lt) => from(lt)),
					filter(
						(logicalType) =>
							this.preFillData !== undefined &&
							this.preFillData.length > 0 &&
							logicalType.name.toLowerCase() ===
								this.preFillData[0].interfaceLogicalType.toLowerCase()
					),
					take(1),
					tap((lt) => {
						//set the pre fill data
						this.setLogicalType(lt);
					})
				)
				.subscribe();
		}
	}
	/**
	 * Sets the current logical type
	 */
	setLogicalType(value: logicalType) {
		this.selectedLogicalType.set(value);
	}
	/**
	 * Closes the form and returns a result
	 */
	close() {
		const isInDialog = this.dialog.getDialogById('new-type-dialog');

		if (!isInDialog) {
			//update the outer component if you aren't in a dialog
			this.updateOuterComponent.next(true);
			return;
		}
		this._dialogToClose.next(isInDialog);
	}

	updateFormState(state: newPlatformTypeDialogReturnData) {
		this._typeFormState.next(state);
	}

	isApplic(value: unknown): value is applic {
		return (
			(value as any) !== undefined &&
			(value as any).id !== undefined &&
			(value as any).name !== undefined
		);
	}
}
