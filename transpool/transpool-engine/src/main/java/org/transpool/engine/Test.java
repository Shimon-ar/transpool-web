package org.transpool.engine;

import org.transpool.engine.ds.MapDb;
import org.transpool.engine.ds.Time;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Time time = new Time(30,5,1);
        Map<String,String> map = new HashMap<>();
        System.out.println(map.get("shimon"));



    }
    //public static void main(String[] args) {
     //   RequestTime time = new RequestTime(1650,"arrivalTime");
//    }
}
