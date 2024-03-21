/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { twColorClasses } from '@osee/shared/types';
import { artifactTypeIcon } from '../types/artifact-explorer.data';

@Injectable({
	providedIn: 'root',
})
export class ArtifactIconService {
	constructor() {}

	getIconClass(icon: artifactTypeIcon): twColorClasses {
		if (
			icon.color === '' ||
			icon.lightShade === '' ||
			icon.darkShade === ''
		) {
			return '';
		}
		if (icon.lightShade === icon.darkShade) {
			return `tw-text-${icon.color}-${icon.lightShade}`;
		}
		return `tw-text-${icon.color}-${icon.lightShade} dark:tw-text-${icon.color}-${icon.darkShade}`;
	}

	getIconVariantClass(icon: artifactTypeIcon) {
		switch (icon.variant) {
			case 'outlined':
				return 'material-icons-outlined';
			case 'round':
				return 'material-icons-round';
			case 'sharp':
				return 'material-icons-sharp';
			case 'two-tone':
				return 'material-icons-two-tone';
			default:
				return '';
		}
	}
}
