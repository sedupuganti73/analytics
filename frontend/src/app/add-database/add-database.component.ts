import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';

@Component({
  selector: 'app-add-database',
  templateUrl: './add-database.component.html',
  providers: [AnalyticsService],
  styleUrls: ['./add-database.component.css']
})
export class AddDatabaseComponent  {
  public name:string;
  public description:string;
  public url:string;

  constructor(public dialogRef: MatDialogRef<AddDatabaseComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService:AnalyticsService) { }



  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addDB():void{
      this.analyticsService.
          addDatabase(this.name, this.description, 
            this.url)
          .subscribe(data=>{
            this.dialogRef.close(true);
          });  

  }

}
