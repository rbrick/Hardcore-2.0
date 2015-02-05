package org.hcsoups.hardcore.database;

import com.mongodb.DB;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by Ryan on 1/25/2015
 * <p/>
 * Project: HCSoups
 */
@RequiredArgsConstructor
public class Database<T> {
    @Getter
    @NonNull
    DB db;

   public void permformAsyncTask(final DBCallback<T> cb, final T t) {
       new DBTask() {
           @Override
           public void run() {
               cb.call(t);
           }
       }.run();
   }
}
