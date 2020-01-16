import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {MatCheckboxModule} from '@angular/material';
import {AnalyticsService} from '../analytics.service';
import { catchError } from 'rxjs/operators/catchError';


@Component({
  selector: 'app-add-report',
  providers: [AnalyticsService],
  templateUrl: './add-report.component.html',
  styleUrls: ['./add-report.component.css']
})
export class AddReportComponent  implements OnInit {
  public name = '';
  public query = '';
  public createdBy = '';
  public dbTypes = [];
  public title = 'Add Report';
  public reportId: number = null;
  public selectedDB: number;
  public loading = false;
  public errorMessage = '';
  public loadType ='';
  public priority='';
  public loadTypes = ['Once','Daily','Hourly'];
  public runTime='';
  public recordCountQuery ='';
  public methodTypes = ['Overwrite','Append','Upsert'];
  public methodType ='';
  public forceDbTypes = [];
  public selectedForceDB: number;
  public isIncremental = false;
  public appName ='';
  public flowName ='';
  

  constructor(public dialogRef: MatDialogRef<AddReportComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService: AnalyticsService
  ) {
        this.analyticsService.getDatabases().subscribe( data => {
            this.dbTypes = data;
            this.selectedDB = this.dbTypes[0].dsId;
        });
        this.analyticsService.getForceDatabases().subscribe( data => {
            this.forceDbTypes = data;
            this.selectedForceDB = this.forceDbTypes[0].dsId;
        });
     }

  ngOnInit() {
    if ( this.data && this.data.reportId) {
      this.reportId = this.data.reportId;
      this.analyticsService.getReport(this.data.reportId)
      .subscribe(data => {
             const report = data;
             this.name = report.name;
             this.title = 'Edit Report ' + report.name;
             this.query = report.query;
             this.selectedDB =  report.dataSource.dsId;
             this.createdBy = report.createdBy;
             if (report.loadType != null) {
                 if (report.loadType == 0) {
                    this.loadType = 'Once';
                 } else if (report.loadType == 1) {
                    this.loadType = 'Daily';
                 } else if (report.loadType == 2) {
                     this.loadType = 'Hourly';
                 }
             }
             if (report.method != null) {
                 this.methodType = report.method ; 
             }             
             this.priority = report.priority;
             this.runTime = report.runTime;
             this.recordCountQuery = report.recordCountQuery;
             this.isIncremental = report.isIncremental;
             this.appName = report.appName;
             this.flowName = report.flowName;
       });

    }
  }

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addReport(): void {
      this.loading = true;
      this.errorMessage = '';
      this.analyticsService.
          addReport(this.name, this.query, this.selectedDB,this.selectedForceDB, this.createdBy,this.loadType,this.methodType,this.priority,this.runTime,this.recordCountQuery,this.isIncremental, this.appName,this.flowName)
          .subscribe(data => {
            this.loading = false;
            this.dialogRef.close(true);
          },
          err => {
            console.log(err.error.message);
            this.errorMessage = err.error.message;
            this.loading = false;
          }
          )
          ;
  }

  updateReport(): void {
    this.analyticsService.updateReport( this.reportId, this.name, this.query, this.selectedDB,this.selectedForceDB, this.createdBy,this.loadType,this.methodType,this.priority,this.runTime,this.recordCountQuery,this.isIncremental,this.appName,this.flowName)
        .subscribe(data => {
          this.dialogRef.close(true);
        });
  }
  
  

}
