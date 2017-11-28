import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';

@Component({
  selector: 'app-add-column',
  templateUrl: './add-column.component.html',
  styleUrls: ['./add-column.component.css'],
  providers: [AnalyticsService]
})
export class AddColumnComponent {

  public name:string='';
  public label:string = '';
  public type:string = '';
  public format:string = '';
  public columnTypes = [{value:'c',viewValue:'Currency'}, {value:'d', viewValue:'Date'}];
 
  constructor(public dialogRef: MatDialogRef<AddColumnComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService:AnalyticsService) { }



  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addColumn():void{
      this.analyticsService.
          addReportColumn(this.name , +this.data.reportId, this.label, this.type, this.format )
            .subscribe(data=>{
            this.dialogRef.close(true);
          });  

  }

}
