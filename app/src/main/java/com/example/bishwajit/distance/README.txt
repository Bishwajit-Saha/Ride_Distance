create on ride start: averageDistanceWithDB = new AverageDistanceWithDB(context);
call to add a location: averageDistanceWithDB.addToDB(location);
on end ride: averageDistanceWithDB.reset();
                     averageDistanceWithDB = null;
              edit the onPostExecute part of AsynckTask to receive the data where required