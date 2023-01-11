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
	NgIf,
	NgFor,
	AsyncPipe,
	TitleCasePipe,
	KeyValuePipe,
} from '@angular/common';
import {
	Component,
	ContentChild,
	Input,
	OnInit,
	Output,
	ViewChild,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatStepperModule } from '@angular/material/stepper';
import { EnumSetFormComponent } from '../../forms/enum-set-form/enum-set-form.component';
import { NewPlatformTypeFormComponent } from '../new-platform-type-form/new-platform-type-form.component';
import { LogicalTypeSelectorComponent } from '../logical-type-selector/logical-type-selector.component';
import {
	BehaviorSubject,
	combineLatest,
	concatMap,
	filter,
	from,
	Observable,
	of,
	Subject,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { logicalType } from '../../types/logicaltype';
import { PlatformType } from '../../types/platformType';
import { TypesService } from '../../services/http/types.service';
import { newPlatformTypeDialogReturnData } from '../../types/newTypeDialogDialogData';
import { NewPlatformTypeFormPage2Component } from '../new-platform-type-form-page2/new-platform-type-form-page2.component';

/**
 * Form used to create a new platform type
 */
@Component({
	selector: 'osee-new-type-form',
	standalone: true,
	imports: [
		MatDialogModule, //note this is just to inherit styling from mat-dialog
		MatStepperModule,
		MatFormFieldModule,
		FormsModule,
		MatButtonModule,
		MatIconModule,
		NewPlatformTypeFormComponent,
		NgIf,
		NgFor,
		AsyncPipe,
		TitleCasePipe,
		KeyValuePipe,
		EnumSetFormComponent,
		LogicalTypeSelectorComponent,
		NewPlatformTypeFormPage2Component,
	],
	templateUrl: './new-type-form.component.html',
	styleUrls: ['./new-type-form.component.sass'],
})
export class NewTypeFormComponent implements OnInit {
	@ContentChild('platformTypeForm') platformTypeForm!: NgForm;
	@ViewChild(LogicalTypeSelectorComponent)
	logicalTypeSelector!: LogicalTypeSelectorComponent;

	logicalTypeSubject: BehaviorSubject<logicalType> = new BehaviorSubject({
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
		this.logicalTypeSubject.next(value);
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
		this._typeFormState
			.pipe(
				take(1),
				tap((v) => isInDialog.close(v))
			)
			.subscribe();
	}

	updateFormState(state: newPlatformTypeDialogReturnData) {
		this._typeFormState.next(state);
	}
}
