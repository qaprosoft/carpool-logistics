CARPOOL LOGISTICS
----
Microservice for CarPool application. Builds optimal routes for passengers carriage. 

### Main Features:

##### 1. Minimization of each particular route (with Littles Algorithm)
##### 2. Minimization of all the cars' routes taken together (with Kernighan-Lin Algorithm)
##### 3. Minimization is based on either distance or duration
##### 4. Usage of Google Maps Distance Matrices API for estimation of distance / duration
##### 5. Capability to limit maximal length of route

Running with Docker
----
**docker command** <br />

	> sudo docker run -p 80:80 --env-file ./carpool-logistics-prod.env -t asemenkov/carpool-logistics:latest

**carpool-logistics-prod.env file** <br />
	`CPL_GM_KEY=google_maps_distance_matrices_api_key` <br />
	`CPL_SERVER_PORT=80` <br />
	`CPL_SERVER_PATH=` <br />
	`CPL_DB_URL=database_url` <br />
	`CPL_DB_USER=database_username` <br />
	`CPL_DB_PASS=database_password`

Communication with CarPool Logistics
----
### Start Logistics Process for Certain Tasks

* **Endpoint** <br />
	`/logistics/process`

* **Method** <br />
	`GET`

* **URL Params** <br />
	`tasks=[comma-separated array of integers]`

* **Response**

    **Code:** 200 <br />
    **Content:**
	```json
	{
		"id":"73db31dd-3972-4f7a-83a7-110cd6f2e82b",
		"state":"RUNNING",
		"code":11,
		"message":"Fetching data from database",
		"startTime":"2018-03-02T13:13:29"
	}
	```
	**Description:** <br />
		`id` - uuid of started process, will be used later to get results or abort this process <br />
		`state` - current state of the process, possible values: `[ RUNNING | SUCCESS | ERROR ]` <br />
		`code` `message` - list of codes and messages can be found in [messages.properties](src/main/resources/messages.properties) <br />
		`startTime` - relates to start of Logistics Process, timezone: `Europe/Kiev`

### Abort Logistics Process

* **Endpoint** <br />
	`/logistics/abort`

* **Method** <br />
	`GET`

* **URL Params** <br />
	`id=[id of logistics process]`

* **Response**
    
	**Code:** 200 <br />
	**Content:** none <br />
	**Description:** If `state = RUNNING`, the thread with Logistics Process will be stopped
		
### Get Result of Logistics Process

* **Endpoint** <br />
	`/logistics/result`

* **Method** <br />
	`GET`

* **URL Params** <br />
	`id=[id of logistics process]`

* **Response**
	
	**Code:** 200 <br />
    **Content:**
	```json
	{
		"id":"de6e50ad-cf7a-4bdf-8d25-aedc87d43a11",
		"state":"SUCCESS",
		"code":20,
		"message":"Calculations completed without errors",
		"startTime":"2018-02-26T22:57:34",
		"endTime":"2018-02-26T22:57:58",
		"optimization":"DURATION",
		"hubs":1,
		"passengers":12,
		"cars":3,
		"length":10133,
		"routes":[...]
	}
	```
	**Description:** <br />
		`id` - uuid of started process, must be equals to requested `id` parameter <br />
		`state` - either `SUCCESS` or `ERROR`, if it's `RUNNING`, wait a second <br />
		* Logistics Process may take from 2 to 120 seconds depending on number of passengers and computational power <br />
		`code` `message` - list of codes and messages can be found in [messages.properties](src/main/resources/messages.properties) <br />
		`startTime` `endTime` - relates to start and end of Logistics Process (not trip), timezone: `Europe/Kiev` <br />
		`optimization` - possible values: `[ DURATION | DISTANCE ]` <br />
		`hubs` - total number of distinct assembly points <br />
		`passengers` - total number of passengers <br />
		`cars` - total number of cars needed to carry this amount of passengers <br />
		`length` - total length of all routes taken together (in `seconds` or `meters`) <br />
		`routes` - array of routes, its size must be equal to `cars` value
		
	**"route" sample:**
	```json
	{
		"length":3502,
		"seats":4,
		"hubs":[...],
		"passengers":[...]
	}
	```
	**Description:** <br />
		`length` - length of particular route (in `seconds` or `meters`) <br />
		`seats` - number of occupied seats i.e. number of passengers in particular car <br />
		`hubs` - array of assembly points
		`passengers` - array of transport endpoints
		
	**"hub" sample:**
	```json
	{
		"id":1,
		"name":"name",
		"color":"#2fff95",
		"address":"address",
		"lat":50.000000000000001,
		"lon":30.000000000000001,
		"time":"2018-02-14T21:15:00"
	}
	```
	**Description:** <br />
		`id` - hub's id from database <br />
		`name` - human readable hub's name <br />
		`color` - hub's color from database <br />
		`address` - human readable hub's address <br />
		`lat` `lon` - latitude and longitude of hub's coordinates <br />
		`time` - estimated time of visit, timezone: `Europe/Kiev` <br />
		* If the trip starts at hub which has more than 1 passenger leaving in different time, the latest time will be chosen
	
	**"passenger" sample:**
	```json
	{
		"task":47,
		"id":34,
		"name":"name",
		"phone":"1234567890",
		"address":"address",
		"lat":50.000000000000002,
		"lon":30.000000000000002,
		"time":"2018-02-14T21:39:26"
	}
	```
	**Description:** <br />
		`task` - task's id from database <br />
		`id` - passenger's id from database <br />
		`name` - human readable passenger's full name <br />
		`phone` - passenger's phone <br />
		`address` - human readable passenger's address <br />
		`lat` `lon` - latitude and longitude of passenger's coordinates <br />
		`time` - estimated time of passenger's arrival, timezone: `Europe/Kiev`