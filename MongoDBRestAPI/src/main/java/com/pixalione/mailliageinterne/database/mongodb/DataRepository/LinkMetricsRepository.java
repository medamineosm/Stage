package com.pixalione.mailliageinterne.database.mongodb.DataRepository;

import com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.MetricsModel.LinkMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by OUASMINE Mohammed Amine on 09/05/2016.
 */
@RepositoryRestResource(collectionResourceRel = "LinkMetrics", path = "LinkMetrics")
public interface LinkMetricsRepository extends MongoRepository<LinkMetrics, String> {
}
