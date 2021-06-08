import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MessageInterfaceComponent } from './message-interface.component';

const routes: Routes = [{ path: '', component: MessageInterfaceComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessageInterfaceRoutingModule { }
