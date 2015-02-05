package org.hcsoups.hardcore.database;

import com.mongodb.DBCollection;

/**
 * Created by Ryan on 1/25/2015
 * <p/>
 * Project: HCSoups
 */
public interface DBManager<T> {

   DBCollection getCollection();

   void insert(T t);
}
