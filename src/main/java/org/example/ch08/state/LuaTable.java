package org.example.ch08.state;

import org.example.ch08.number.LuaParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//最后通过LuaTable向外提供接口,隐藏实际使用的底层实现(arr,map)
public class LuaTable {
    private List<Object> arr;    //存放数组
    private Map<Object, Object> map;  //存放哈希表

    public LuaTable(int nArr, int nRec) {
        if (nArr > 0) {
            arr = new ArrayList<>(nArr);
        }
        if (nRec > 0) {
            map = new HashMap<>(nRec);
        }
    }

    public Object get(Object key) {
        key = floatToInteger(key);
        if (arr != null && key instanceof Long) {
            int idx = ((Long) key).intValue();
            if (idx >= 1 && idx <= arr.size()) {
                return arr.get(idx - 1);
            }
        }
        return map != null ? map.get(key) : null;
    }

    //看来Lua的表idx是从1开始的,所以和Java的映射需要减1
    public void put(Object key, Object value) {
        if(key == null){
            System.out.println("key can't be null");
            throw new RuntimeException("table index is nil!");
        }
        if(key instanceof Double && ((Double) key).isNaN()){
            System.out.println("key can't be NaN");
            throw new RuntimeException("table index is NaN!");
        }
        key = floatToInteger(key);

        if(key instanceof Long){
            //获取索引
           int idx = ((Long) key).intValue();
           if(idx >=1){
               if(arr == null){
                   arr = new ArrayList<>();
               }
               int arrLen = arr.size();
               if (idx <= arrLen) {
                   //如果已经用arr存储的
                   arr.set(idx - 1, value);
                   if(idx == arrLen && value == null){
                       shrinkArray();
                   }
                   return;
               }

               if(idx == arrLen+1){
                   if(map != null){
                       map.remove(key);
                   }
                   if(value != null){
                       arr.add(value);
                       expandArray();
                   }
                   return;
               }
           }
        }

        if(value != null){
            if(map == null){
                map = new HashMap<>();
            }
            map.put(key, value);
        }else{
            if(map != null){
                map.remove(key);
            }
        }
    }

    //如果是浮点数,再转化为整数
    private Object floatToInteger(Object key) {
        if (key instanceof Double) {
            Double f = (Double) key;
            if (LuaParser.isInteger(f)) {
                return f.longValue();
            }
        }
        return key;
    }

    //TODO:我感觉这个方法有问题,这个应该不处理中间的null,因为如果中间的被去掉了的话,那么好像整个索引就乱了,我这里和书上实现的不同
    private void shrinkArray() {
        for(int i = arr.size()-1;i>=0;i--){
            if(arr.get(i) == null){
                arr.remove(i);
            }else{
                //add
                return;
            }
        }
    }

    private void expandArray() {
        if(map != null){
            for (int idx = arr.size() + 1; ; idx++) {
                Object val = map.remove((long) idx);
                if (val != null) {
                    arr.add(val);
                } else {
                    break;
                }
            }
        }
    }

    //本身就是不准确的好像,只返回作为数组的长度
    public int length() {
        return arr == null ? 0 : arr.size();
    }

}
