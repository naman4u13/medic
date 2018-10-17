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
   
## API 1
  ### Fetch Symptoms  
```  
  String symurl = "https://sandbox-healthservice.priaid.ch/symptoms?token=" + key.Token + "&format=json&language=en-gb";  
  
```       
```  

      for (int i = 0; i < array.length(); i++) {l̥
        JSONObject currObject = null;
        try {
            currObject = array.getJSONObject(i);
            name = currObject.getString("Name");
        } 
```                          

  ### Libraries for twitter search or streaming
   [Libraries used for search and streaming](https://www.npmjs.com/package/twitter)

 ### Fetch tweets
      const Twit = require('twitter');
      var T = new Twit(config);

      var params = {
         q: '#nodejs',
         count: 4,
         result_type: 'recent',
         lang: 'en'
        }
      T.get('search/tweets', params, function(err, data, response) {//search
         if(!err){
            var tweets = data.statuses;
            for (var i = 0; i < tweets.length; i++) {
            console.log(tweets[i].text);
          }
     
         } else {
            console.log(err);
        }
      })  

      var stream = T.stream('statuses/filter', { track: '#MeToo',language: 'en' });//stream
         stream.on('data', function (data) {
        
     			   console.log((data));
               let tw_obj  = {
                  "id_str":data.id_str,
                  "created_at":data.created_at,
                  "name":data.user.name,
                  "text":data.text,
                  "retweet_count": data.retweet_count,
      
                  }
               tw = new db(tw_obj);
               tw.save((err,data)=>{
               if(err) console.log(err);
                else {
                  console.log("data save");
               }
            })
 
 
 ### Database Schema
        const mongoose = require('mongoose')
        const Schema = mongoose.Schema;

      const tweetSchema = new Schema({
         name:{type:String},
         id_str: {
         type: String,
         unique: true,
         required: true
      },
      status: {
         type: String,
      },
      author: {
         type: String,
    
      },
      created_at: {
      type: String,
      required: true
      },
      text:{type:String},
      retweet_count:{type:Number},
      favorite_count:{type:Number}
      })
      module.exports = mongoose.model('Tweet', tweetSchema)
      # API 2
      ## Pagination
     var perPage = 2
     var page = req.params.page || 1
     var noMatch=' ';
   
    if(req.query.search){
      const regex = new RegExp(escapeRegex(req.query.search), 'gi');
      db
      .find({name:regex})
      .skip((perPage * page) - perPage)
      .limit(perPage)
      .exec(function(err, twit) {
          db.count().exec(function(err, count) {
              if (err) return next(err)
              if(count<1){
                noMatch="Notfound"
              }
              res.render('text', {
                  t: twit,
                  current: page,
                  pages: Math.ceil(count / perPage),
                 noMatch:noMatch
              })
          })
        
      })
  
  
  ## API 2
  ### Pagination 
       var perPage = 2
    var page = req.params.page || 1
    var noMatch=' ';
   
    if(req.query.search){
      const regex = new RegExp(escapeRegex(req.query.search), 'gi');
      db
      .find({name:regex})
      .skip((perPage * page) - perPage)
      .limit(perPage)
      .exec(function(err, twit) {
          db.count().exec(function(err, count) {
              if (err) return next(err)
              if(count<1){
                noMatch="Notfound"
              }
              res.render('text', {
                  t: twit,
                  current: page,
                  pages: Math.ceil(count / perPage),
                 noMatch:noMatch
              })
          })
        
      })
    }
  ### Text Search and Filter
      app.get('/search/:page',function(req,res){
    var perPage = 2
    var page = req.params.page || 1
    var noMatch=' ';
   
    if(req.query.search){
      const regex = new RegExp(escapeRegex(req.query.search), 'gi');
      db
      .find({name:regex})
      .skip((perPage * page) - perPage)
      .limit(perPage)
      .exec(function(err, twit) {
          db.count().exec(function(err, count) {
              if (err) return next(err)
              if(count<1){
                noMatch="Notfound"
              }
              res.render('text', {
                  t: twit,
                  current: page,
                  pages: Math.ceil(count / perPage),
                 noMatch:noMatch
              })
          })
        
      })
    }
    else{
      db
      .find({})
      .skip((perPage * page) - perPage)
      .limit(perPage)
      .exec(function(err, twit) {
          db.count().exec(function(err, count) {
              if (err) return next(err)
              res.render('text', {
                  t: twit,
                  current: page,
                  pages: Math.ceil(count / perPage),
                 noMatch:noMatch
              })
          })
        
      })
    }
    
  });
  
  
  ### Sorting
      Sorting has done on the basis of date and time
## API 3
  ### Save to CSV
     ### Exporting filtered data to CSV file using packages **json2csv** and **fs**.
     ` db.find({},function(err,y){
      if(err) console.log(err);
      const json2csv = require('json2csv').parse;
      const fs = require('fs');
      const fields = ['id_str','created_at', 'name','text'];
      const csv = json2csv({ data: y, fields: fields });
     
       fs.writeFile('file.csv', csv, function(err) {
       if (err) throw err;
       console.log('file saved');
       });
      res.render('index',{p:y});
    })`
