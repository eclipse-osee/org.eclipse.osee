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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { ActivatedRoute } from '@angular/router';
import { iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { UiService } from '../../../ple-services/ui/ui.service';
import { ActionStateButtonModule } from '../../../shared-components/components/action-state-button/action-state-button.module';
import { BranchPickerModule } from '../../../shared-components/components/branch-picker/branch-picker.module';
import { ImportService } from './lib/services/import.service';

@Component({
	selector: 'osee-import',
	templateUrl: './import.component.html',
	styleUrls: ['./import.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		MatButtonModule,
		MatSelectModule,
		ActionStateButtonModule,
		BranchPickerModule,
	],
})
export class ImportComponent implements OnInit, OnDestroy {
	constructor(
		private route: ActivatedRoute,
		private routerState: UiService,
		private importService: ImportService
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.routerState.idValue = params.get('branchId') || '';
			this.routerState.typeValue = params.get('branchType') || '';
		});
	}

	ngOnDestroy(): void {
		this.importService.reset();
	}

	importOptionSelection = this.importService.selectedImportOption;
	branchId = this.importService.branchId;
	branchType = this.importService.branchType;
	importSummary = this.importService.importSummary;
	importOptions = this.importService.importOptions;
	importSuccess = this.importService.importSuccess;
	selectedImportFileName = this.importService.importFile.pipe(
		switchMap((file) =>
			iif(() => file === undefined, of(''), of(file?.name))
		)
	);

	importOptionSelectionText = this.importOptions.pipe(
		switchMap((options) =>
			iif(
				() => options.length > 0,
				of('Select an import type'),
				of('No import types available')
			)
		)
	);

	selectImportOption(event: MatSelectChange) {
		this.importService.reset();
		this.importService.SelectedImportOption = event.value;
	}

	selectFile(event: Event) {
		const target = event.target as HTMLInputElement;
		if (target.files && target.files.length > 0) {
			const file: File = target.files[0];
			this.importService.ImportFile = file;
			this.importService.ImportSuccess = undefined;
			this.importService.ImportInProgress = true;
			target.value = '';
		}
	}

	performImport() {
		this.importService.performImport();
	}
}

export default ImportComponent;
