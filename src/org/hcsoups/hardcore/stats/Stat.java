package org.hcsoups.hardcore.stats;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.WriteConcern;
import lombok.*;
import org.hcsoups.hardcore.Hardcore;

import java.util.UUID;

/**
 * Created by Ryan on 12/30/2014
 * <p/>
 * Project: HCSoups
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Stat  {
    @NonNull String name;
    @NonNull UUID id;
    int kills;
    int deaths;
    double kdr;

    public void setKdr() {
        this.kdr = Math.abs(kills*1D/(deaths == 0 ? 1 : deaths));
    }

    public void save() {
        BasicDBObject object = new BasicDBObject()
                .append("name", name)
                .append("uuid", id.toString())
                .append("kills", kills)
                .append("deaths", deaths)
                .append("kdr", kdr);
       DBCollection collection = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("statistics");

       DBCursor cursor = collection.find(new BasicDBObject("uuid", id.toString()));

        if(cursor.hasNext()) {
           BasicDBObject object1 = (BasicDBObject) cursor.next();
            object.put("name", name);
            object.put("uuid", id.toString());
            object.put("kills", kills);
            object.put("deaths", deaths);
            object.put("kdr", kdr);
          collection.update(cursor.getQuery(), object);
       } else {
           collection.insert(object, WriteConcern.NORMAL);
       }

    }

}
