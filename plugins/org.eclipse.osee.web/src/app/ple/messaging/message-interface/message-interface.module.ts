/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { A11yModule } from '@angular/cdk/a11y';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { ActionStateButtonModule } from '../../../shared-components/components/action-state-button/action-state-button.module';
import { BranchUndoButtonModule } from '../../../shared-components/components/branch-undo-button/branch-undo-button.module';
import { DiffViewsModule } from '../../diff-views/diff-views/diff-views.module';
import { GenericButtonsModule } from '../../generic-buttons/generic-buttons.module';
import { PleSharedMaterialModule } from '../../ple-shared-material/ple-shared-material.module';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { DeleteMessageDialogComponent } from './components/dialogs/delete-message-dialog/delete-message-dialog.component';
import { DeleteSubmessageDialogComponent } from './components/dialogs/delete-submessage-dialog/delete-submessage-dialog.component';
import { RemoveMessageDialogComponent } from './components/dialogs/remove-message-dialog/remove-message-dialog.component';
import { RemoveSubmessageDialogComponent } from './components/dialogs/remove-submessage-dialog/remove-submessage-dialog.component';
import { AddMessageDialogComponent } from './components/message-table/add-message-dialog/add-message-dialog.component';
import { EditMessageFieldComponent } from './components/message-table/edit-message-field/edit-message-field.component';
import { MessageTableComponent } from './components/message-table/message-table.component';
import { AddSubMessageDialogComponent } from './components/sub-message-table/add-sub-message-dialog/add-sub-message-dialog.component';
import { EditSubMessageFieldComponent } from './components/sub-message-table/edit-sub-message-field/edit-sub-message-field.component';
import { SubMessageTableComponent } from './components/sub-message-table/sub-message-table.component';
import { UsermenuComponent } from './components/usermenu/usermenu/usermenu.component';
import { MessageInterfaceRoutingModule } from './message-interface-routing.module';
import { MessageInterfaceComponent } from './message-interface.component';
import { ConvertMessageTableTitlesToStringPipe } from './pipes/convert-message-table-titles-to-string.pipe';
import { ConvertSubMessageTitlesToStringPipe } from './pipes/convert-sub-message-titles-to-string.pipe';

@NgModule({
	declarations: [
		MessageInterfaceComponent,
		MessageTableComponent,
		SubMessageTableComponent,
		ConvertMessageTableTitlesToStringPipe,
		ConvertSubMessageTitlesToStringPipe,
		EditSubMessageFieldComponent,
		AddSubMessageDialogComponent,
		EditMessageFieldComponent,
		AddMessageDialogComponent,
		RemoveSubmessageDialogComponent,
		DeleteSubmessageDialogComponent,
		RemoveMessageDialogComponent,
		DeleteMessageDialogComponent,
		UsermenuComponent,
	],
	imports: [
		CommonModule,
		FormsModule,
		MatTableModule,
		MatMenuModule,
		MatStepperModule,
		MatSelectModule,
		MatFormFieldModule,
		MatInputModule,
		MatSlideToggleModule,
		MatTooltipModule,
		MatDialogModule,
		MatSidenavModule,
		MatIconModule,
		A11yModule,
		GenericButtonsModule,
		PleSharedMaterialModule,
		OseeStringUtilsPipesModule,
		OseeStringUtilsDirectivesModule,
		SharedMessagingModule,
		DiffViewsModule,
		ActionStateButtonModule,
		BranchUndoButtonModule,
		MessageInterfaceRoutingModule,
	],
})
export class MessageInterfaceModule {}
