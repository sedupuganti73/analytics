import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'type'
})
export class TypePipe implements PipeTransform {

  transform(value: any, args?: any): any {
    if ( value === 'S') {
      return 'String';
    } else if ( value === 'D' ) {
      return 'Date';
    } else if ( value === 'I' ) {
      return 'Integer';
    } else if ( value === 'F' ) {
      return 'Decimal';
    }
    return null;
  }

}
