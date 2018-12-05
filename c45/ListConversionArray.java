package com.ky.alert.util.c45;

import java.util.*;

/**
 * 类描述：
 *
 * @author 吴松   song_wu
 * @date 2018/9/21 10:08
 * @updateRemark 修改备注：
 */
public class ListConversionArray {

    /**
     * 将 list 转换为数组
     * @param arrayList
     * @return
     */
    public Object[] conversionList(List<List<Object>> arrayList){
        Vector<Object[]> result = new Vector<>();
        for (List<Object> arrays: arrayList){
            Object[] vector = arrays.toArray();
            result.add(vector);
        }
       return result.toArray();
    }

    public Object[] conversionList(List<List<Object>> arrayList, String[] strList,int index ,String sep){
        Set<String> stringSet = new HashSet<>();
        for (String s : strList[index].split(sep)){
            stringSet.add(s);
        }
        Vector<Object[]> result = new Vector<>();
        for (List<Object> arrays: arrayList){
            if(! stringSet.contains(arrays.get(index))){
                Object[] vector = arrays.toArray();
                result.add(vector);
            }
        }
       return result.toArray();
    }


    public List<List<Object>>forkSetSimple(List<List<Object>> forkList, int local, String sep){
        //新建一个用于返回的list
        List<List<Object>> result = new ArrayList<>();
        //循环传入的list
        for (List<Object> objects :forkList){
            //存放所有可能的组合
            List<String> cartesians = new ArrayList();
            if (local<objects.size()){
                String strs = (String)objects.get(local);
                //取出同列多个字段 进行排列组合
                if (null != strs && !"".equals(strs)){
                    for (String str : strs.split(sep)){
                        cartesians.add(str);
                    }
                }
            }
            //重组成list
            for(String cart: cartesians){
                //深拷贝  否则会覆盖数据
                List<Object> objValue = copy(objects);
                objValue.set(local,cart);
                if (!"".equals(cart) )
                    result.add(objValue);
            }
        }
        return result;
    }

    /**
     * 将制定行数组进行 组合
     * 1 2 3 4
     *组合结果
     * 1 2 3 4 12 13 14 23 24 34 123 124 234 1234
     * @param forkList
     * @param local
     * @param sep
     * @return
     */
    public List<List<Object>>forkSet(List<List<Object>> forkList, int local, String sep){
        //新建一个用于返回的list
        List<List<Object>> result = new ArrayList<>();
        //循环传入的list
        for (List<Object> objects :forkList){
            //存放所有可能的组合
            List<String> cartesians = new ArrayList();
            if (local<objects.size()){
                String strs = (String)objects.get(local);
                //取出同列多个字段 进行排列组合
                if (null == strs || "".equals(strs)){
                    cartesians.add("");
                }else{
                    for (String str : strs.split(sep)){
                        cartesians.add(str);
                    }
                    //存放的是 排列好的所有可能  重点
                    cartesians = this.cartesian(cartesians,sep);
                }
            }
            //重组成list
            for(String cart: cartesians){
                //深拷贝  否则会覆盖数据
                List<Object> objValue = copy(objects);
                objValue.set(local,cart);
                result.add(objValue);
            }
        }
        return result;
    }

    /**
     * 深拷贝
     * @param list
     * @return
     */
    public  List<Object> copy( List<Object> list){
        List<Object> res = new ArrayList<>();
        for (Object o : list){
            res.add(o);
        }
        return res;
    }

    /**
     *  用于组合所有可能
     * @param dkr
     * @param sep
     * @return
     */
    public List<String> cartesian (List<String> dkr, String sep){
        List<String> stringList = new ArrayList<>();
        for (int i=1;i<=dkr.size();i++){
            //组合重点代码
            List<String> cr = getCombinationResult(i, dkr,sep);
            stringList.addAll(cr);
        }
        return stringList;
    }

    /**
     * 使用递归计算
     * @param num 从第 num个字段开始
     * @param strList 当前剩余strlist
     * @param sep  分割符   默认为  "、"
     * @return
     */
    public List<String> getCombinationResult(int num, List<String> strList, String sep) {
        List<String> result = new ArrayList<String>();
        //递归结束条件  只有一个字符
        if (num == 1) {
            for (String c : strList) {
                result.add(c);
            }
            return result;
        }
        //递归循环结束条件  所有字符全取
        if (num >= strList.size()) {
            String sumStr = new String();
            //循环合并所有存在字符
            for (String text :strList){
                sumStr+=text+sep;
            }
            //去掉末尾的 "、"
            sumStr=sumStr.substring(0,sumStr.length()-sep.length());
            result.add(sumStr);
            return result;
        }
        int strlen = strList.size();
        for (int i = 0; i < (strlen - num + 1); i++) {
            //开始递归  从i+1处直至末尾 进行计算
            List<String> cr = getCombinationResult(num - 1, strList.subList(i+1,strlen), sep);
            //得到上面被去掉的字符，进行组合
            String c = strList.get(i);
            for (String s : cr) {
                result.add(c +sep+ s);
            }
        }
        return result;
    }

}
