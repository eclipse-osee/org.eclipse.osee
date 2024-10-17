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
import { legacyCreateArtifact } from '@osee/transactions/types';
import {
	commandObject,
	parameterObject,
} from '../../types/grid-commander-types/create-command-form-types';

@Injectable({
	providedIn: 'root',
})
export class OpenUrlFormService {
	transformCommandObject(cmdObj: object): Partial<legacyCreateArtifact> {
		return cmdObj satisfies Partial<legacyCreateArtifact>;
	}

	transformParmeterObject(parameter: object): Partial<legacyCreateArtifact> {
		return parameter satisfies Partial<legacyCreateArtifact>;
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
