package com.gm.mapr.query;

import org.ojai.store.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ojai.store.QueryCondition.Op.EQUAL;

@SpringBootApplication
public class MapRQueryApplication {

    public static final String OJAI_CONNECTION_URL = "ojai:mapr:";
    //Full path including namespace /mapr/<cluster-name>/apps/business
    public static final String TABLE_NAME = "/mapr/maprdemo.mapr.io/apps/user";

    public static void main(String[] args) {
        SpringApplication.run(MapRQueryApplication.class, args);
    }

    @RestController
    public class MapRQuery {

        @GetMapping("/users/{since}")
        public List<Map<String, Object>> users(@PathVariable String since) {
            return doFindUsersSince(since);
        }

        List<Map<String, Object>> doFindUsersSince(String since) {

            // Create an OJAI connection to MapR cluster
            // NOTE - Connection is auto closeable - close the OJAI connection and release any resources held by the connection
            try (Connection connection = DriverManager.getConnection(OJAI_CONNECTION_URL)) {
                // Get an instance of OJAI
                // NOTE - DocumentStore is auto closeable - Close this instance of OJAI DocumentStore
                try (DocumentStore documentStore = connection.getStore(TABLE_NAME)) {

                    Query query = connection.newQuery()
                            .select("name", "yelping_since", "support") // projection
                            .where(connection.newCondition().is("yelping_since", EQUAL, since).build()) // condition
//                        .setOption(OjaiOptions.OPTION_FORCE_DRILL, true)
                            .build();

                    QueryResult queryResult = documentStore.find(query);

                    System.out.println(queryResult.getQueryPlan().toString());

                    List<Map<String, Object>> output = new ArrayList<>();
                    queryResult.forEach(userDocument -> {
                        output.add(userDocument.asMap());
                    });


                    return output;
                }
            }
        }

    }
}
