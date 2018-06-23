package com.gm.mapr.query;

import org.ojai.Document;
import org.ojai.DocumentStream;
import org.ojai.store.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class QueryApplication {

    public static final String OJAI_CONNECTION_URL = "ojai:mapr:";
    //Full path including namespace /mapr/<cluster-name>/apps/business
    public static final String TABLE_NAME = "/mapr/maprdemo.mapr.io/apps/user";

    public static void main(String[] args) {
        SpringApplication.run(QueryApplication.class, args);
    }

    @RestController
    public class MapRQuery {

        @GetMapping("/users/{since}")
        public String users(@PathVariable String since) {
            return doFindUsersSince(since);
        }

        String doFindUsersSince(String since) {

            // Create an OJAI connection to MapR cluster
            Connection connection = DriverManager.getConnection(OJAI_CONNECTION_URL);
            // Get an instance of OJAI
            DocumentStore store = connection.getStore(TABLE_NAME);

            Query query = connection.newQuery()
                    .select("name", "yelping_since", "support")
                    .where(connection.newCondition().is("yelping_since", QueryCondition.Op.EQUAL, since).build()) // condition
                    .limit(1)
                    .build();

            DocumentStream stream = store.find(query);
            StringBuilder output = new StringBuilder();
            for (Document userDocument : stream) {
                output.append(userDocument.asJsonString());
            }


            // Close this instance of OJAI DocumentStore
            store.close();

            // close the OJAI connection and release any resources held by the connection
            connection.close();

            return output.toString();
        }

    }
}
