import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';

@Component({
  selector: 'app-add-report',
  providers: [AnalyticsService],
  templateUrl: './add-report.component.html',
  styleUrls: ['./add-report.component.css']
})
export class AddReportComponent  {
  public name:string='';
  public dbUsername:string='';
  public dbPassword:string='';
  public query:string = '';
  public type:string = '';
  public createdBy:string = '';
  public dbTypes = [{value:'d',viewValue:'Data Warehouse'}, {value:'r', viewValue:'Revenue'},{value:'s', viewValue:'SPM'}];
 
  constructor(public dialogRef: MatDialogRef<AddReportComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService:AnalyticsService) { }



  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addReport():void{
      this.analyticsService.
          addReport(this.name, this.dbUsername, 
            this.dbPassword, this.query, this.type, this.createdBy)
          .subscribe(data=>{
            this.dialogRef.close(true);
          });  

  }

}
