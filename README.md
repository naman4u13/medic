#  medic
## Table Of Contents
1. [Description](#description)
2. [Getting Started](#getting-started)
   * [Prerequisites](#prerequisites)
   * [Installation](#installation-setting-up-project)
3. [API 1](#api-1)
    * [Trigger search or stream](#trigger-search-or-stream) 
    * [Libraries for twitter search or streaming](#libraries-for-twitter-search-or-streaming)
    * [Fetch tweets](#fetch-tweets)
    * [Database Schema](#database-schema)
4. [API 2](#api-2)
    * [Pagination](#pagination)
    * [Text Search and Filter](#text-search-and-filter)
    * [Sorting](#sorting) 
5. [API 3](#api-3)
    * [save to CSV](#save-to-csv)

## Description
Use Apimedic API to search and fetch medical symptom, list down the possible medical conditions/diagnosis, thereafter using web scraping 
to scrape information regarding their treatments and storing it in Firebase Realtime Database. 
1. API 1 to To fetch the symptoms by using Apimedic’s APIs. 
2. API 2 to list down the medical conditions for a given symptom.
3. API 3 to fetch/display treatment options for given medical condition and store it in a database.

 ## Getting Started
> Following instructions will get you a copy of the project up and running on your local machine
****
### Prerequisites
  * you need to have nodejs and npm installed on your system . ([get_node](https://nodejs.org/en/download/))
  * This API make use of **twitter** npm package.
  ```
      'twitter' is Twitter API Client for node. Supports both the REST and Streaming API.  
  ```
  * Package.json include
    + mongoose      
    + ejs-mate
    + body-parser
    + mongodb
    + express
    + fs
    + json2csv
  
     
For installing package(s):
   ```javascript
   npm install --save <package_name> 
   ```
> --save install package into local node_module directory.


 ### Installation (setting up project)
  * Download the zip file and extract it.
  * Cd to the project folder 'Twit_API-master'.
  * make sure you have **mongoDB** installed in your local machine ([get mongoDB](https://docs.mongodb.com/manual/installation/))and mongodb server running([mongod](https://docs.mongodb.com/manual/tutorial/manage-mongodb-processes/)) at port 27017(default).
  
    
 To run server file(Terminal 1):
  ```javascript
   run > node main.js
   ```
  
 To run mongodb server(Terminal 2):
  ```javascript
   run > mongod
   ``` 
   
## API 1 and API 2
  ### Search and Fetch Symptom
  
  On Searching a Symptom, URL for the JSON query to fetch all symptoms is created  ` "https://sandbox-healthservice.priaid.ch/symptoms?token="  key.Token +"&format=json&language=en-gb"`, if apimedic database contains that particular symptom a query URL for its possible diagnosis is formed `"https://sandbox-healthservice.priaid.ch/diagnosis?symptoms=[" + currObject.getString("ID") + "]&gender=male&year_of_birth=1997&token=" + key.Token + "&format=json&language=en-gb"` and executed,
  after fetching JSON objects of the diagnosis, its information is displayed in a list.
  
  
 <img src="https://github.com/naman4u13/medic/blob/master/Img/Screenshot_2018-10-17-21-03-20.png" alt="image" height="300px" width="200px" align="left">
 <img src="https://github.com/naman4u13/medic/blob/master/Img/Screenshot_2018-10-17-21-03-33.png" alt="image" height="300px" width="200px">

 ### Fetching Auth Token
 As AuthToken is valid only for a period of time, it needs to be fetched every time on creation of main activity, ` key = new AccessToken();`
 A class named TokenKey has function named LoadToken for the specified purpose.
 

 ### Working with Volley Library
 Volley is an HTTP library that makes networking for Android apps easier and faster.  
 For fetching both symptoms and diagnosis , connection to API is made through volley.  
 `RequestQueue queue = Volley.newRequestQueue(this);`  
 
  
  ## API 3
  ### Web Scraping for a given Diagnosis    
  For a particular Medical Condition, information regarding its available Treatments were scraped from "Knowledge Panel" on `https://www.google.com` and "Glossary List" on `https://legacy.priaid.ch/en-gb` using "JSOUP" Library 
   Document doc = Jsoup.connect(URL).get();
   
   
   Incase of scraping a Knowledge Panel, a google search like "Treatment for Common Cold" gives us a web page which has a info box containing "TREATMENT" tab which is automatically selected, 4 separate List containing HTML elements of same Class Name is maintained after inspecting each info box.
   ```
      Elements treatoptions = ans.select("div.hXYDxb");
      Elements subtype = ans.select("a.HZnEfd");
      Elements subdetails = ans.select("div.Rs3Epd");
      Elements counter = ans.select("div.Y6f3fc.HtP7nb");
  ```
  <img src="https://github.com/naman4u13/medic/blob/master/Img/Screenshot%20(3).png" alt="image" height="200px" width="300px">
  otherwise for Glossary List, based on ID of Treatment a simple query ```"https://legacy.priaid.ch/en-gb/glossar-details?t=issue&id=" + params[0].ID``` displays a web page from which information under title "Consequences and Treatment" is scraped
 
```
 
  treattab = doc.getElementsByTag("p");

                        for (Element element : treattab) {

                            if (element.previousElementSibling().text().equals("Consequences + Treatment")) {
                                treatObj.Info = element.text();
                                break;
                            }
                        }
   
   
```
   
   

<img src="https://github.com/naman4u13/medic/blob/master/Img/Screenshot%20(6).png" alt="image" height="200px" width="300px">
 




### Use of Firebase Relatime Database
  To store already fetched information of Treatments for a Disease, Firebase realtime database is used which is a schemaless database (NoSQL) in which the data is stored in JSON format. Any further query on same diagnosis will fetch data from database instead of scraping it from web.
 
 
<img src="https://github.com/naman4u13/medic/blob/master/Img/Screenshot%20(4).png" alt="image" height="200px" width="300px">
 


## API 5
A simple "String.contains(Substring)" functionality inside "if condition" during fetching/checking for symptoms can take a text like “I’m having a back pain”, and extracts symptoms and based on those symptoms returns the medical conditions. 
