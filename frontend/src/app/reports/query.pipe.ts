import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'query'
})
export class QueryPipe implements PipeTransform {

  transform(value: any, args?: any): any {
    return value.substring(0, 50) + '...';
  }

}
