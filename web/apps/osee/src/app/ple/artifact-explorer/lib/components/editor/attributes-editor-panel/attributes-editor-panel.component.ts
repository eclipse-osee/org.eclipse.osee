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
import { NgClass } from '@angular/common';
import { Component, Input, inject, viewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { AttributesEditorComponent } from '@osee/shared/components';
import { FormDirective } from '@osee/shared/directives';
import {
	legacyAttributeType,
	legacyModifyArtifact,
	legacyTransaction,
} from '@osee/transactions/types';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { artifactTab } from '../../../types/artifact-explorer';
import { MatIcon } from '@angular/material/icon';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { attribute } from '@osee/shared/types';
import { TransactionService } from '@osee/transactions/services';

@Component({
	selector: 'osee-attributes-editor-panel',
	imports: [
		NgClass,
		FormsModule,
		AttributesEditorComponent,
		FormDirective,
		MatIcon,
		ExpansionPanelComponent,
	],
	templateUrl: './attributes-editor-panel.component.html',
})
export class AttributesEditorPanelComponent {
	@Input() tab!: artifactTab;

	enum$ = new Observable<string[]>();

	private transactionService = inject(TransactionService);

	saveChanges() {
		if (this.updatedAttributes.value.length > 0) {
			const tx: legacyTransaction = {
				branch: this.tab.branchId,
				txComment:
					'Attribute changes for artifact: ' + this.tab.artifact.name,
			};
			const attributes: legacyAttributeType[] =
				this.updatedAttributes.value.map((attr) => {
					return { typeId: attr.typeId, value: attr.value };
				});
			const modifyArtifact: legacyModifyArtifact = {
				id: this.tab.artifact.id,
				setAttributes: attributes,
			};
			tx.modifyArtifacts = [modifyArtifact];
			this.transactionService
				.performMutation(tx)
				.pipe(
					tap(() => {
						this.updatedAttributes.next([]);
					})
				)
				.subscribe();
		}
	}

	// Panel open/close state handling

	panelOpen = new BehaviorSubject<boolean>(true);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}

	// Handle output attributes returned from attribute editor

	updatedAttributes = new BehaviorSubject<attribute[]>([]);
	handleUpdatedAttributes(updatedAttributes: attribute[]) {
		this.updatedAttributes.next(updatedAttributes);
	}

	hasChanges(): boolean {
		return this.updatedAttributes.value.length > 0;
	}

	// Handle form status change

	protected _attributesEditorForm = viewChild.required(
		'attributesEditorForm',
		{ read: NgForm }
	);
}
