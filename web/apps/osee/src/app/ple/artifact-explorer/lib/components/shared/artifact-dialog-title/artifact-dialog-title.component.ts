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
import { Component, input, inject } from '@angular/core';
import { MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import {
	artifactTypeIcon,
	operationType,
} from '@osee/artifact-with-relations/types';

@Component({
	selector: 'osee-artifact-dialog-title',
	imports: [MatDialogTitle, MatIcon],
	templateUrl: './artifact-dialog-title.component.html',
})
export class ArtifactDialogTitleComponent {
	private artifactIconService = inject(ArtifactIconService);

	operationType = input.required<operationType>();

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}
}
