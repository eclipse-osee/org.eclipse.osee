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
import { Component, Input } from '@angular/core';
import { MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { BehaviorSubject } from 'rxjs';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import { artifactContextMenuOption } from '../../../types/artifact-explorer';
import { artifactTypeIcon } from '@osee/artifact-with-relations/types';

@Component({
	selector: 'osee-artifact-dialog-title',
	standalone: true,
	imports: [MatDialogTitle, MatIcon],
	templateUrl: './artifact-dialog-title.component.html',
})
export class ArtifactDialogTitleComponent {
	constructor(private artifactIconService: ArtifactIconService) {}

	@Input() set option(option: artifactContextMenuOption) {
		this._option.next(option);
	}
	protected _option = new BehaviorSubject<artifactContextMenuOption>({
		name: '',
		icon: {
			icon: '',
			color: '',
			lightShade: '',
			darkShade: '',
			variant: '',
		},
	});

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}
}
