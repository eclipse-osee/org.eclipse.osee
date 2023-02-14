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
import { CommonModule } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { createArtifact } from '@osee/shared/types';
import { combineLatest, map, take } from 'rxjs';

import { ContextSelectionService } from '../../services/create-command-form-services/context-selection.service';
import { CreateCommandWithParameterArtifactService } from '../../services/create-command-form-services/create-command-with-parameter-artifact.service';
import { CreateCommandService } from '../../services/create-command-form-services/create-command.service';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { OpenUrlFormComponent } from './command-actions/open-url-form/open-url-form.component';
import { ParameterSingleSelectComponent } from '../../parameter-types/parameter-single-select/parameter-single-select.component';

@Component({
	selector: 'osee-create-command-form',
	standalone: true,
	templateUrl: './create-command-form.component.html',
	styleUrls: ['./create-command-form.component.sass'],
	imports: [
		CommonModule,
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatSelectModule,
		MatGridListModule,
		MatCardModule,
		MatButtonModule,
		MatIconModule,
		OpenUrlFormComponent,
		ParameterSingleSelectComponent,
	],
})
export class CreateCommandFormComponent implements OnDestroy {
	commandActionOptions = this.parameterDataService.parameterDefaultValue$;
	availableContexts = this.contextSelectionService.availableContexts;
	commandAction: string = '';

	createCommandFormOptions = combineLatest([
		this.availableContexts,
		this.commandActionOptions,
	]).pipe(
		map(([contexts, commandActions]) => ({
			contexts,
			commandActions,
		}))
	);

	constructor(
		private createCommandService: CreateCommandService,
		private parameterDataService: ParameterDataService,
		private createCommandWithParameterArtifactService: CreateCommandWithParameterArtifactService,
		private contextSelectionService: ContextSelectionService
	) {}

	ngOnDestroy(): void {
		this.createCommandService.doneFx = '';
		this.createCommandWithParameterArtifactService.doneFx = '';
	}
	onSubmitHandler(e: {
		command: Partial<createArtifact>;
		parameter: Partial<createArtifact>;
	}) {
		this.createCommandWithParameterArtifactService
			.createCommandWithParameter(e.command, e.parameter)
			.pipe(take(1))
			.subscribe();
	}

	onCommandSelectionChange(e: { selectedOption: string }) {
		this.commandAction = e.selectedOption;
	}

	onContextSelectionChange(e: { selectedOption: string }) {
		this.contextSelectionService.SelectedContext = e.selectedOption;
	}
}
