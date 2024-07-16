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
import { AsyncPipe } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCard } from '@angular/material/card';
import { artifact, createArtifact } from '@osee/shared/types';
import { combineLatest, map, take } from 'rxjs';

import { ParameterSingleSelectComponent } from '../../parameter-types/parameter-single-select/parameter-single-select.component';
import { ContextSelectionService } from '../../services/create-command-form-services/context-selection.service';
import { CreateCommandWithParameterArtifactService } from '../../services/create-command-form-services/create-command-with-parameter-artifact.service';
import { CreateCommandService } from '../../services/create-command-form-services/create-command.service';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { OpenUrlFormComponent } from './command-actions/open-url-form/open-url-form.component';

@Component({
	selector: 'osee-create-command-form',
	standalone: true,
	templateUrl: './create-command-form.component.html',
	styles: [],
	imports: [
		AsyncPipe,
		FormsModule,
		MatCard,
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
		command: Partial<createArtifact & artifact>;
		parameter: Partial<createArtifact & artifact>;
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
