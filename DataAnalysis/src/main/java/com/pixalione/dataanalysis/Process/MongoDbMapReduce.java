package com.pixalione.dataanalysis.Process;


import com.mongodb.*;

/**
 * Created by OUASMINE Mohammed Amine on 04/05/2016.
 */
public class MongoDbMapReduce {

    public static void countKeyWordInAllLInks(Mongo mongo) {
        try {
            mongo = mongo != null ? mongo : new Mongo("localhost", 27017);
            DB db = mongo.getDB("testCrawling");
            DBCollection links = db.getCollection("LinkMetrics");
            System.out.println("Nombre de liens : " + links.count());
            DBCollection outputCollection = db.getCollection("KeyWord");

            String map = "function() {\n" +
                    "    var anchor = this.KeyWords;\n" +
                    "    var id = this._id;\n" +
                    "    if (anchor) {\n" +
                    "        // quick lowercase to normalize per your requirements\n" +
                    "        anchor = anchor.toLowerCase().split(\" \");\n" +
                    "        for (var i = anchor.length - 1; i >= 0; i--) {\n" +
                    "            // might want to remove punctuation, etc. here\n" +
                    "            if (anchor[i])  {      // make sure there's something\n" +
                    "               emit(anchor[i], 1); // store a 1 for each word\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            String reduce = "function( key, values ) {\n" +
                            "    var count = 0;\n" +
                            "    values.forEach(function(v) {\n" +
                            "        count +=v;\n" +
                            "    });\n" +
                            "    return count;\n" +
                            "}";

            MapReduceCommand cmd = new MapReduceCommand(links, map, reduce,
                    null, MapReduceCommand.OutputType.INLINE, null);

            MapReduceOutput out = links.mapReduce(cmd);

            for (DBObject o : out.results()) {
                outputCollection.save(o);
                System.out.println(o);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void KeyWordMapReduceOccurence(Mongo mongo)
    {
        try {
            mongo = mongo != null ? mongo : new Mongo("localhost", 27017);
            DB db = mongo.getDB("testCrawling");
            DBCollection links = db.getCollection("LinkMetrics");
            System.out.println("Nombre de liens : " + links.count());
            DBCollection outputCollection = db.getCollection("KeyWord");

            String map = "function() {\n" +
                    "  var KWord = this.KeyWords;\n" +
                    "  if (KWord) {\n" +
                    "      // quick lowercase to normalize per your requirements\n" +
                    "      for (var i = KWord.length - 1; i >= 0; i--) {\n" +
                    "          // might want to remove punctuation, etc. here\n" +
                    "          if (KWord[i])  {      // make sure there's something\n" +
                    "            var Word = KWord[i].toLowerCase();\n" +
                    "             emit(Word, 1); // store a 1 for each word\n" +
                    "          }\n" +
                    "      }\n" +
                    "  }\n" +
                    "}";
            String reduce = "function( key, values ) {    \n" +
                    "    var count = 0;    \n" +
                    "    values.forEach(function(v) {            \n" +
                    "        count +=v;    \n" +
                    "    });\n" +
                    "    return count;\n" +
                    "}";

            MapReduceCommand cmd = new MapReduceCommand(links, map, reduce,
                    null, MapReduceCommand.OutputType.INLINE, null);

            MapReduceOutput out = links.mapReduce(cmd);

            for (DBObject o : out.results()) {
                outputCollection.save(o);
                System.out.println(o);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        MongoDbMapReduce.KeyWordMapReduceOccurence(null);
    }
}
