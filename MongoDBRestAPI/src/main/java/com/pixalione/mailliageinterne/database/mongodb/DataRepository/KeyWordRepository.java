package com.pixalione.mailliageinterne.database.mongodb.DataRepository;

import com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.KeyWord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by OUASMINE Mohammed Amine on 10/05/2016.
 */
@RepositoryRestResource(collectionResourceRel = "KeyWord", path = "KeyWord")
public interface KeyWordRepository extends MongoRepository<KeyWord, String> {
}
