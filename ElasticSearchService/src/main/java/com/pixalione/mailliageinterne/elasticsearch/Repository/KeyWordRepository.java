package com.pixalione.mailliageinterne.elasticsearch.Repository;

import com.pixalione.mailliageinterne.elasticsearch.Model.KeyWord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by OUASMINE Mohammed Amine on 10/05/2016.
 */
public interface KeyWordRepository extends ElasticsearchRepository<KeyWord, String> {

}
