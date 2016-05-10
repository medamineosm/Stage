package com.pixalione.mailliageinterne.database.mongodb.DataRepository;

import com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.Link;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


/**
 * Created by AmYné on 28/04/2016.
 */
@RepositoryRestResource(collectionResourceRel = "Links", path = "Links")
public interface LinkRepository extends MongoRepository<Link, String> {

}
