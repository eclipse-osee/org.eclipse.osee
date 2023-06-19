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
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { artifact, createArtifact } from '@osee/shared/types';
import { OpenUrlFormService } from '../../../../services/create-command-form-services/open-url-form.service';
import {
	commandObject,
	parameterObject,
} from '../../../../types/grid-commander-types/create-command-form-types';

@Component({
	selector: 'osee-open-url-form',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatButtonModule,
		MatIconModule,
	],
	templateUrl: './open-url-form.component.html',
	styleUrls: ['./open-url-form.component.sass'],
})
export class OpenUrlFormComponent {
	@Output('submitOpenURLCommandForm') submitOpenUrlForm: EventEmitter<{
		command: Partial<createArtifact & artifact>;
		parameter: Partial<createArtifact & artifact>;
	}> = new EventEmitter<{
		command: Partial<createArtifact & artifact>;
		parameter: Partial<createArtifact & artifact>;
	}>();

	displayParameterOptions: boolean = false;

	patternValidatorForURL =
		'(?:http[s]:?\\/\\/)?(?:[\\w\\-]+(?::[\\w\\-]+)?@)?(?:[\\w\\-]+\\.)+(?:[a-z]{2,4})(?::[0-9]+)?(?:\\/[\\w\\-\\.%]+)*(?:\\?(?:[\\w\\-\\.%]+=[\\w\\-\\.%!]+&?)+)?(#\\w+\\-\\.%!)?';
	protected commandObject: commandObject = {
		name: '',
		description: '',
		contentURL: '',
		customCommand: true,
	};
	protected defaultParameter: parameterObject = {
		name: 'URL',
		description: '',
		defaultValue: '',
		//TODO: Is validator used (checkbox?)
		isValidatorUsed: true,
		//TODO: How to determine Validator Type
		validatorType: this.patternValidatorForURL,
	};

	constructor(private openURLFormService: OpenUrlFormService) {}

	onSubmitHandler(form: NgForm) {
		if (form.form.status === 'INVALID') return;

		this.openURLFormService.updateParameterAndTransformObjects(
			this.defaultParameter,
			this.commandObject
		);

		this.submitOpenUrlForm.emit({
			command: this.commandObject,
			parameter: this.defaultParameter,
		});
		return;
	}

	displayParameterOptionsBtnHandler(e: Event) {
		e.preventDefault();
		this.displayParameterOptions = !this.displayParameterOptions;
	}
}
