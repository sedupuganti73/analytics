import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Response, Headers, Http, ResponseContentType} from "@angular/http";

@Injectable()
export class AnalyticsService {
  prefix ='/api';  
  constructor(private http:Http) { }
  getReports():Observable<Response> {
    return this.http.get(this.prefix+'/reports/');
  }

  getReport(reportId:number):Observable<Response> {
    return this.http.get(this.prefix+'/reports/'+reportId);
  }

  addReport( name:string, query:string, dataSource:number, createdBy:string ):Observable<Response> {
    return this.http.post(this.prefix+'/reports/',{name:name,  query: query , label:'', dataSource: dataSource, createdBy:createdBy});
  }

  updateReport( reportId:number, name:string, query:string, dataSource:number, createdBy:string ):Observable<Response> {
    return this.http.post(this.prefix+'/reports/',{reportId: reportId, name:name,  query: query , label:'', dataSource: dataSource, createdBy:createdBy});
  }

  deleteReport ( reportId: number) {
    return this.http.delete(this.prefix+'/reports/delete/'+reportId);
  }

  getReportColumns(reportId:number):Observable<Response> {
    return this.http.get(this.prefix+'/columns/'+reportId);
  }
  getHistory(reportId:string):Observable<Response> {
    return this.http.get(this.prefix+'/history/'+reportId);
  }

  addReportColumn( name:string, reportId:number, label:string, type:string, format:string ):Observable<Response> {
    return this.http.post(this.prefix+'/columns/create',{name:name, reportId:reportId, label:label, type:type, format: format});
  }

  deleteReportColumn ( columnId: number, reportId: number) {
    return this.http.delete(this.prefix+'/columns/delete/'+columnId+'/'+reportId);
  }

  getDatabases():Observable<Response> {
    return this.http.get(this.prefix+'/ds/');
  }
  getDatabase(dsId:number):Observable<Response> {
    return this.http.get(this.prefix+'/ds/'+dsId);
  }
  addDatabase( name:string, description:string, url:string ):Observable<Response> {
    return this.http.post(this.prefix+'/ds/',{name:name, description:description, url:url});
  }
  updateDatabase( dsId:number, name:string, description:string, url:string ):Observable<Response> {
    return this.http.post(this.prefix+'/ds/',{ dsId:dsId, name:name, description:description, url:url});
  }
  deleteDatabase ( dsId: number) {
    return this.http.delete(this.prefix+'/ds/'+dsId);
  }


}
