import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MessageElementInterfaceComponent } from './message-element-interface.component';

const routes: Routes = [{ path: '', component: MessageElementInterfaceComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessageElementInterfaceRoutingModule { }
