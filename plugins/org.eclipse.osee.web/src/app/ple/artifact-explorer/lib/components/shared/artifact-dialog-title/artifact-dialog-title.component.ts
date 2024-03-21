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
import { CommonModule } from '@angular/common';
import {
	artifactContextMenuOption,
	artifactTypeIcon,
} from '../../../types/artifact-explorer.data';
import { BehaviorSubject } from 'rxjs';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ArtifactIconService } from '../../../services/artifact-icon.service';

@Component({
	selector: 'osee-artifact-dialog-title',
	standalone: true,
	imports: [CommonModule, MatDialogModule, MatIconModule],
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
		excludedArtifactTypes: [],
	});

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}
}
