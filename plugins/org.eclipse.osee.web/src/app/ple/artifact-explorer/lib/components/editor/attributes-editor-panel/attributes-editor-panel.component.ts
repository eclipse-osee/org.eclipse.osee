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
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionService } from '@osee/shared/transactions';
import { attributeType, modifyArtifact, transaction } from '@osee/shared/types';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FormsModule } from '@angular/forms';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { MatExpansionModule } from '@angular/material/expansion';
import { AttributeEnumsDropdownComponent } from '../attribute-enums-dropdown/attribute-enums-dropdown.component';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { tab } from '../../../types/artifact-explorer.data';

@Component({
	selector: 'osee-attributes-editor-panel',
	standalone: true,
	imports: [
		CommonModule,
		MatGridListModule,
		MatFormFieldModule,
		FormsModule,
		MatInputModule,
		MatSelectModule,
		MatIconModule,
		MatAutocompleteModule,
		MatExpansionModule,
		AttributeEnumsDropdownComponent,
	],
	templateUrl: './attributes-editor-panel.component.html',
})
export class AttributesEditorPanelComponent {
	@Input() tab!: tab;
	isDirty: boolean = false;

	enum$ = new Observable<string[]>();

	constructor(
		private transactionService: TransactionService,
		private artExpHttpService: ArtifactExplorerHttpService
	) {}

	populateOptions(attrId: string) {
		this.enum$ = this.artExpHttpService.getAttributeEnums(
			this.tab.branchId,
			this.tab.artifact.id,
			attrId
		);
	}

	makeDirty() {
		this.isDirty = true;
	}

	saveChanges() {
		const tx: transaction = {
			branch: this.tab.branchId,
			txComment:
				'Attribute changes for artifact: ' + this.tab.artifact.name,
		};
		const attributes: attributeType[] = this.tab.artifact.attributes.map(
			(attr) => {
				return { typeId: attr.typeId, value: attr.value };
			}
		);
		const modifyArtifact: modifyArtifact = {
			id: this.tab.artifact.id,
			setAttributes: attributes,
		};
		tx.modifyArtifacts = [modifyArtifact];
		this.transactionService
			.performMutation(tx)
			.pipe(
				tap(() => {
					this.isDirty = false;
				})
			)
			.subscribe();
	}

	// panel open/close state handling
	panelOpen = new BehaviorSubject<boolean>(false);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}
}
