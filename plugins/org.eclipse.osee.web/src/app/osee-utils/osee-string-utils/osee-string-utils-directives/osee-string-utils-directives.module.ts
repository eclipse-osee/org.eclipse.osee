import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HighlightFilteredTextDirective } from './highlight-filtered-text.directive';



@NgModule({
  declarations: [HighlightFilteredTextDirective],
  imports: [
    CommonModule
  ],
  exports: [HighlightFilteredTextDirective]
})
export class OseeStringUtilsDirectivesModule { }
