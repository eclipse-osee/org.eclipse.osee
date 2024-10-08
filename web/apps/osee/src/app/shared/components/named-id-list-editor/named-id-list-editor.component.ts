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
	effect,
	inject,
	input,
	output,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {
	MatActionList,
	MatListItem,
	MatListItemIcon,
} from '@angular/material/list';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { NamedId } from '@osee/shared/types';
import { filter, take, tap } from 'rxjs';
import { EditViewFreeTextFieldDialogComponent } from '../edit-view-free-text-field-dialog/edit-view-free-text-field-dialog.component';

@Component({
	selector: 'osee-named-id-list-editor',
	standalone: true,
	imports: [
		FormsModule,
		MatFormField,
		MatInput,
		MatActionList,
		MatListItem,
		MatDivider,
		MatIcon,
		MatListItemIcon,
		MatPaginator,
	],
	templateUrl: './named-id-list-editor.component.html',
	styles: [],
})
export class NamedIdListEditorComponent {
	name = input('');
	dataToDisplay = input<NamedId[]>([]);
	count = input(0);

	pageSize = input(10);

	pageIndex = input(1);
	allowedToEdit = input(false);

	pageEvent = output<PageEvent>();

	filterChange = output<string>();

	namedIdEdit = output<NamedId>();

	createNew = output<string>();
	_dialog = inject(MatDialog);

	filter = signal('');
	private _filterEffect = effect(() => this.filterChange.emit(this.filter()));

	openNameDialog(namedIdToModify: NamedId) {
		this._dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: namedIdToModify.name,
					type: 'Name',
					return: namedIdToModify.name,
					editable: this.allowedToEdit,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((v) => v !== 'ok' && v !== 'cancel' && v !== undefined),
				tap((v) => {
					this.namedIdEdit.emit({
						id: namedIdToModify.id,
						name: v.return,
					});
				})
			)
			.subscribe();
	}

	addNamedId() {
		const returnVal = '';
		this._dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: '',
					type: 'Name',
					return: returnVal,
					editable: this.allowedToEdit,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((v) => v !== 'ok' && v !== 'cancel' && v !== undefined),
				tap((v) => {
					this.createNew.emit(v.return);
				})
			)
			.subscribe();
	}
}
