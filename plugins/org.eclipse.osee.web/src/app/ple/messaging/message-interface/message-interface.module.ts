import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MessageInterfaceRoutingModule } from './message-interface-routing.module';
import { MessageInterfaceComponent } from './message-interface.component';
import { MessageTableComponent } from './components/message-table/message-table.component';
import { SubMessageTableComponent } from './components/sub-message-table/sub-message-table.component';
import { PleSharedMaterialModule } from '../../ple-shared-material/ple-shared-material.module';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { ConvertMessageTableTitlesToStringPipe } from './pipes/convert-message-table-titles-to-string.pipe';
import { ConvertSubMessageTitlesToStringPipe } from './pipes/convert-sub-message-titles-to-string.pipe';
import { MatMenuModule } from '@angular/material/menu';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { EditSubMessageFieldComponent } from './components/sub-message-table/edit-sub-message-field/edit-sub-message-field.component';
import { AddSubMessageDialogComponent } from './components/sub-message-table/add-sub-message-dialog/add-sub-message-dialog.component';
import { MatStepperModule } from '@angular/material/stepper';
import { MatSelectModule } from '@angular/material/select';
import { EditMessageFieldComponent } from './components/message-table/edit-message-field/edit-message-field.component';
import { AddMessageDialogComponent } from './components/message-table/add-message-dialog/add-message-dialog.component';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';


@NgModule({
  declarations: [MessageInterfaceComponent, MessageTableComponent, SubMessageTableComponent, ConvertMessageTableTitlesToStringPipe, ConvertSubMessageTitlesToStringPipe, EditSubMessageFieldComponent, AddSubMessageDialogComponent, EditMessageFieldComponent, AddMessageDialogComponent],
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
    MatProgressBarModule,
    PleSharedMaterialModule,
    OseeStringUtilsPipesModule,
    OseeStringUtilsDirectivesModule,
    SharedMessagingModule,
    MessageInterfaceRoutingModule
  ]
})
export class MessageInterfaceModule { }
