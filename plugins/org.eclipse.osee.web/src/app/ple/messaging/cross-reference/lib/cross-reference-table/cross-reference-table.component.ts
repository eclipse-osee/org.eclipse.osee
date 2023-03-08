import { Component, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	crossReferenceHeaderDetails,
	CrossReferenceService,
	TableEditTextFieldComponent,
} from '@osee/messaging/shared';
import { CrossReference } from 'src/app/ple/messaging/shared/types/crossReference.d ';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { filter, switchMap, take, tap } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { NewCrossReferenceDialogComponent } from '../new-cross-reference-dialog/new-cross-reference-dialog.component';
import { SplitStringPipe } from 'src/app/shared/utils/pipes/split-string.pipe';
import { HeaderService } from 'src/app/ple-services/ui/header.service';

@Component({
	selector: 'osee-cross-reference-table',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		MatButtonModule,
		MatDialogModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatMenuModule,
		MatTableModule,
		MatTooltipModule,
		NewCrossReferenceDialogComponent,
		TableEditTextFieldComponent,
		SplitStringPipe,
	],
	templateUrl: './cross-reference-table.component.html',
	styleUrls: ['./cross-reference-table.component.scss'],
})
export class CrossReferenceTableComponent implements OnDestroy {
	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;

	constructor(
		private headerService: HeaderService,
		private crossRefService: CrossReferenceService,
		private ui: UiService,
		public dialog: MatDialog
	) {}

	getTableHeaderByName(header: keyof CrossReference) {
		return this.headerService.getHeaderByName(
			crossReferenceHeaderDetails,
			header
		);
	}

	data = this.crossRefService.crossReferences;

	inEditMode = this.crossRefService.inEditMode;

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.FilterValue = filterValue;
	}

	updateCrossReference(
		crossRef: CrossReference,
		header: keyof CrossReference,
		newValue: string
	) {
		this.crossRefService.updateCrossReferenceAttribute(
			crossRef,
			header,
			newValue
		);
	}

	deleteCrossReference(crossRef: CrossReference) {
		const del = this.crossRefService
			.deleteCrossReference(crossRef)
			.pipe(tap((res) => (this.ui.updated = true)));
		del.subscribe();
	}

	menuPosition = {
		x: '0',
		y: '0',
	};

	openMenu(event: MouseEvent, crossRef: CrossReference) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger.menuData = {
			crossRef: crossRef,
		};
		this.matMenuTrigger.openMenu();
	}

	openEditDialog(crossRef: CrossReference) {
		this.dialog
			.open(NewCrossReferenceDialogComponent, {
				data: { crossRef: crossRef },
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((value): value is CrossReference => value !== undefined),
				switchMap((crossRef) =>
					this.crossRefService
						.updateCrossReference(crossRef)
						.pipe(tap((_) => (this.ui.updated = true)))
				)
			)
			.subscribe();
	}

	updateCrossRef = this.crossRefService.updateCrossReference;

	headers: (keyof CrossReference)[] = [
		'name',
		'crossReferenceValue',
		'crossReferenceAdditionalContent',
		'crossReferenceArrayValues',
	];

	ngOnDestroy(): void {
		this.FilterValue = '';
	}

	get filterValue() {
		return this.crossRefService.filterValue;
	}

	set FilterValue(value: string) {
		this.crossRefService.FilterValue = value;
	}
}
