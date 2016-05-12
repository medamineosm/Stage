package com.pixalione.mailliageinterne.database.mongodb.DataRepository;

import com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.PageJson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by OUASMINE Mohammed Amine on 11/05/2016.
 */
@RepositoryRestResource(collectionResourceRel = "PageJson", path = "PageJson")
public interface PageJsonRepository extends MongoRepository<PageJson, String> {
}
