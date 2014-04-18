# Frontend application

## Installation

* Installing node.js for npm. See https://github.com/joyent/node/blob/master/README.md
* Install gulp & bower
    * npm install -g gulp
    * npm install -g bower
* Launch  `npm update` this will download JS library for development
* Update application dependencies with bower : `bower install` or just run `gulp build`

## Json Schema

### Field

#### Common definition

* required  : true / false
* uniqueItems true / false => will create a unique index on the field

#### Field type / enum

##### Type : string
 * minLength : integer
 * maxLength : integer
 * pattern : regex
 * format :
     * date-time
     * email
     * uri
     * rte

##### Type : Integer  / number
 * required  : true / false
 * minimum : integer
 * maximum : integer

##### Type : Boolean

##### Enum

