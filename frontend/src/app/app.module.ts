import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { ReportsComponent } from './reports/reports.component';
import { ReportColumnsComponent } from './report-columns/report-columns.component';
import { HistoryComponent } from './history/history.component';
import { AppRoutingModule } from './app-routing.module';
import {MatToolbarModule,MatSelectModule,MatTooltipModule, MatProgressBarModule, MatDialogModule, MatCardModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatTableModule} from '@angular/material';
import { AddReportComponent } from './add-report/add-report.component';
import { FormsModule }   from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AddColumnComponent } from './add-column/add-column.component';


@NgModule({
  declarations: [
    AppComponent,
    ReportsComponent,
    ReportColumnsComponent,
    HistoryComponent,
    AddReportComponent,
    AddColumnComponent
  ],
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    MatProgressBarModule,
    MatTooltipModule,
    MatToolbarModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatTableModule,
    MatSelectModule,
    HttpModule
  ],
  entryComponents: [AddReportComponent,AddColumnComponent],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
