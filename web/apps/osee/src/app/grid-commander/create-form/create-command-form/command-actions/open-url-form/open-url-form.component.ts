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
import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { legacyArtifact, legacyCreateArtifact } from '@osee/transactions/types';
import { OpenUrlFormService } from '../../../../services/create-command-form-services/open-url-form.service';
import {
	commandObject,
	parameterObject,
} from '../../../../types/grid-commander-types/create-command-form-types';

@Component({
	selector: 'osee-open-url-form',
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatSuffix,
		MatIconButton,
		MatIcon,
		MatButton,
	],
	templateUrl: './open-url-form.component.html',
	styles: [],
})
export class OpenUrlFormComponent {
	private openURLFormService = inject(OpenUrlFormService);

	@Output('submitOpenURLCommandForm') submitOpenUrlForm: EventEmitter<{
		command: Partial<legacyCreateArtifact & legacyArtifact>;
		parameter: Partial<legacyCreateArtifact & legacyArtifact>;
	}> = new EventEmitter<{
		command: Partial<legacyCreateArtifact & legacyArtifact>;
		parameter: Partial<legacyCreateArtifact & legacyArtifact>;
	}>();

	displayParameterOptions = false;

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
