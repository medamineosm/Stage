package com.pixalione.mailliageinterne.database.mongodb.DataRepository;

import com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.Scan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by AmYné on 29/04/2016.
 */
@RepositoryRestResource(collectionResourceRel = "Scans", path = "Scans")
public interface ScanRepository extends MongoRepository<Scan, String>{
}
