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
import { Injectable } from '@angular/core';
import { createArtifact } from '@osee/shared/transactions';
import {
	commandObject,
	parameterObject,
} from '../../types/grid-commander-types/create-command-form-types';

@Injectable({
	providedIn: 'root',
})
export class OpenUrlFormService {
	constructor() {}

	transformCommandObject(cmdObj: {}): Partial<createArtifact> {
		return cmdObj satisfies Partial<createArtifact>;
	}

	transformParmeterObject(parameter: {}): Partial<createArtifact> {
		return parameter satisfies Partial<createArtifact>;
	}

	updateParameterAndTransformObjects(
		parameter: parameterObject,
		command: commandObject
	) {
		if (parameter.description === '')
			parameter.description = command.description;
		if (parameter.defaultValue === '')
			parameter.defaultValue = command.contentURL;

		this.transformCommandObject(command);
		this.transformParmeterObject(parameter);
	}
}
