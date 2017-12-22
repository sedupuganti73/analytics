import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { ReportsComponent } from './reports/reports.component';
import { ReportColumnsComponent } from './report-columns/report-columns.component';
import { HistoryComponent } from './history/history.component';
import { AppRoutingModule } from './app-routing.module';
import {MatToolbarModule, MatSidenavModule, MatListModule, MatSelectModule, MatSnackBarModule,
  MatTooltipModule, MatProgressBarModule, MatDialogModule, MatCardModule, MatProgressSpinnerModule,
  MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatTableModule} from '@angular/material';
import { AddReportComponent } from './add-report/add-report.component';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AddColumnComponent } from './add-column/add-column.component';
import { DatabasesComponent } from './databases/databases.component';
import { AddDatabaseComponent } from './add-database/add-database.component';
import { TypePipe } from './report-columns/type.pipe';


@NgModule({
  declarations: [
    AppComponent,
    ReportsComponent,
    ReportColumnsComponent,
    HistoryComponent,
    AddReportComponent,
    AddColumnComponent,
    DatabasesComponent,
    AddDatabaseComponent,
    TypePipe
  ],
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    MatSidenavModule,
    MatListModule,
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
    MatSnackBarModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    HttpClientModule
  ],
  entryComponents: [AddReportComponent, AddColumnComponent, AddDatabaseComponent],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
