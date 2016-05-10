package com.pixalione.mailliageinterne.database.mongodb.DataMongoDb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by OUASMINE Mohammed Amine on 10/05/2016.
 */
@Document(collection = "KeyWord")
public class KeyWord {

    @Id
    private String id;
    private int frequency;

    public KeyWord() {
    }

    public KeyWord(String id, int frequency) {
        this.id = id;
        this.frequency = frequency;
    }

    public String getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "KeyWord{" +
                "id='" + id + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
