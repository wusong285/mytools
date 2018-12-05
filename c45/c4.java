package com.ky.alert.util.c45;

import java.util.*;

public class c4 {
    private DecisionTreeNode root;
    private boolean[] visable;
    private static final int NOT_FOUND = -1;
    private static final int DATA_START_LINE = 0;
    private Object[] trainingArray;
    private String[] columnHeaderArray;
    private Object[] decisionType;
    private int decisionLocal;
    private int nodeIndex;
    private DecisionTreeNode resultNode;

    /**
     *
     * @param title  标题
     * @param data 学习数据
     * @param printData 要预测的数据
     * @param dLocal  要预测的数据在学习数据的第几列
     * @return
     */
    public DecisionTreeNode evaluation(String[] title ,Object[] data,Object[] printData,int dLocal){
        this.decisionLocal=dLocal;
        this.columnHeaderArray=title;
        this.create(data, this.decisionLocal);
        this.forecast(printData, root);
        return resultNode;
    }

    /**
     * @param printData
     * @param node
     */
    public void forecast(Object[] printData, DecisionTreeNode node) {
        int index = getColumnHeaderIndexByName(node.nodeName);
        if (index == NOT_FOUND) {
            this.resultNode=node;
        }
        DecisionTreeNode[] childs = node.childNodesArray;
        for (int i = 0; i < childs.length; i++) {
            if (childs[i] != null) {
                if (childs[i].parentArrtibute.equals(printData[index])) {
                    forecast(printData, childs[i]);
                }
            }
        }
    }

    /**
     * @param array
     * @param index
     */
    public void create(Object[] array, int index) {
        this.trainingArray = Arrays.copyOfRange(array, DATA_START_LINE, array.length);
        init(array, index);
        decisionType = pickUpAndCreateSubArray(array, index);
        createDecisionTree(this.trainingArray);
        // 输出决策树
        //printDecisionTree(root);
        outputDecisionTree(root, 0, null);
    }

    /**
     * @param array
     * @return Object[]
     */
    @SuppressWarnings("boxing")
    public Object[] getMaxGain(Object[] array) {
        Object[] result = new Object[2];
        double gain = 0;
        int index = -1;

        for (int i = 0; i < visable.length; i++) {
            if (!visable[i]) {
                //TODO ID3 change to C4.5
                double value = gainRatio(array, i, this.nodeIndex);
                System.out.println(value);
                if (gain < value) {
                    gain = value;
                    index = i;
                }
            }
        }
        result[0] = gain;
        result[1] = index;
        //TODO throws can't forecast this model exception
        if (index != -1) {
            visable[index] = true;
        }
        return result;
    }

    /**
     * @param array
     */
    public void createDecisionTree(Object[] array) {
        Object[] maxgain = getMaxGain(array);
        if (root == null) {
            root = new DecisionTreeNode();
            root.parentNode = null;
            root.parentArrtibute = null;
            root.arrtibutesArray = getArrtibutesArray(((Integer) maxgain[1]).intValue());
            root.nodeName = getColumnHeaderNameByIndex(((Integer) maxgain[1]).intValue());
            root.childNodesArray = new DecisionTreeNode[root.arrtibutesArray.length];
            insertDecisionTree(array, root);
        }
    }

    /**
     * @param array
     * @param parentNode
     */
    public void insertDecisionTree(Object[] array, DecisionTreeNode parentNode) {
        String[] arrtibutes = parentNode.arrtibutesArray;
        for (int i = 0; i < arrtibutes.length; i++) {
            Object[] pickArray = pickUpAndCreateSubArray(array, arrtibutes[i],
                    getColumnHeaderIndexByName(parentNode.nodeName));
            Object[] info = getMaxGain(pickArray);
            double gain = ((Double) info[0]).doubleValue();
            if (gain != 0) {
                //非叶子节点
                int index = ((Integer) info[1]).intValue();
                DecisionTreeNode currentNode = new DecisionTreeNode();
                currentNode.parentNode = parentNode;
                currentNode.parentArrtibute = arrtibutes[i];
                currentNode.arrtibutesArray = getArrtibutesArray(index);
                currentNode.nodeName = getColumnHeaderNameByIndex(index);
                currentNode.childNodesArray = new DecisionTreeNode[currentNode.arrtibutesArray.length];
                parentNode.childNodesArray[i] = currentNode;
                insertDecisionTree(pickArray, currentNode);
            } else {
                //叶子节点
                DecisionTreeNode leafNode = new DecisionTreeNode();
                leafNode.parentNode = parentNode;
                leafNode.parentArrtibute = arrtibutes[i];
                leafNode.arrtibutesArray = new String[0];
                leafNode.nodeName = getLeafNodeName(pickArray, this.nodeIndex);
                leafNode.childNodesArray = new DecisionTreeNode[0];
                Map<String,Double> map = new HashMap<>();
                for (Object object :decisionType){
                    double sum = 0;
                    String str = (String) object;
                    for (Object obj : pickArray){
                        String [] objst = (String[])obj;
                        if(objst[decisionLocal].equals(str)) sum++;
                    }
                    if (pickArray.length!=0)
                        map.put(str,sum/pickArray.length);
                    else
                        map.put(str,0d);
                }
                leafNode.probability=map;
                parentNode.childNodesArray[i] = leafNode;
            }
        }
    }

    /**
     * @param node
     */
    public void printDecisionTree(DecisionTreeNode node) {
        System.out.println(node.nodeName);
        DecisionTreeNode[] childs = node.childNodesArray;
        for (int i = 0; i < childs.length; i++) {
            if (childs[i] != null) {
                System.out.println(childs[i].parentArrtibute);
                printDecisionTree(childs[i]);
            }
        }
    }

    static void outputDecisionTree(DecisionTreeNode tree, int level, Object from) {
        for (int i = 0; i < level; i++)
            System.out.print("|-----");
        if (from != null)
            System.out.printf("(%s):", from);
        String attrName = tree.nodeName;
        if (tree.childNodesArray!=null && tree.childNodesArray.length>0){
            System.out.printf("[%s = ?]\n", attrName);
        }else{
            System.out.printf("[CATEGORY = ");
            Iterator<Map.Entry<String,Double>> it  = tree.probability.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String,Double> entry = it.next();
                System.out.printf("  "+entry.getKey()+"==>"+entry.getValue());
            }
            System.out.printf(" ]\n");
        }
        for (DecisionTreeNode attrValue : tree.childNodesArray) {
            outputDecisionTree(attrValue, level + 1, attrName + " = " + attrValue.parentArrtibute);
        }
    }


    /**
     * init data
     *
     * @param dataArray
     * @param index
     */
    public void init(Object[] dataArray, int index) {
        this.nodeIndex = index;
        //init data
        visable = new boolean[((Object[]) dataArray[0]).length];
        for (int i = 0; i < visable.length; i++) {
            if (i == index) {
                visable[i] = true;
            } else {
                visable[i] = false;
            }
        }
    }

    /**
     * @param array
     * @param arrtibute
     * @param index
     * @return Object[]
     */
    public Object[] pickUpAndCreateSubArray(Object[] array, String arrtibute,
                                            int index) {
        List list = new ArrayList();
        for (int i = 0; i < array.length; i++) {
            String[] strs = this.objectToString((Object[]) array[i]);
            if (strs[index].equals(arrtibute)) {
                list.add(strs);
            }
        }
        return list.toArray();
    }

    public Object[] pickUpAndCreateSubArray(Object[] array, int index) {
        Set<String> hashSet = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            Object[] strs = (Object[]) array[i];
            if (!"".equals(strs[index])) {
                hashSet.add((String) strs[index]);
            }
        }
        return hashSet.toArray();
    }

    /**
     * gain(A)
     *
     * @param array
     * @param index
     * @return double
     */
    public double gain(Object[] array, int index, int nodeIndex) {
        int[] counts = separateToSameValueArrays(array, nodeIndex);
        String[] arrtibutes = getArrtibutesArray(index);
        double infoD = infoD(array, counts);
        double infoaD = infoaD(array, index, nodeIndex, arrtibutes);
        return infoD - infoaD;
    }

    /**
     * @param array
     * @param nodeIndex
     * @return
     */
    public int[] separateToSameValueArrays(Object[] array, int nodeIndex) {
        String[] arrti = getArrtibutesArray(nodeIndex);
        int[] counts = new int[arrti.length];
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }
        for (int i = 0; i < array.length; i++) {
            String[] strs = this.objectToString((Object[]) array[i]);
            for (int j = 0; j < arrti.length; j++) {
                if (strs[nodeIndex].equals(arrti[j])) {
                    counts[j]++;
                }
            }
        }
        return counts;
    }

    /**
     * gainRatio = gain(A)/splitInfo(A)
     *
     * @param array
     * @param index
     * @param nodeIndex
     * @return
     */
    public double gainRatio(Object[] array, int index, int nodeIndex) {
        double gain = gain(array, index, nodeIndex);
        int[] counts = separateToSameValueArrays(array, index);
        double splitInfo = splitInfoaD(array, counts);
        if (splitInfo != 0) {
            return gain / splitInfo;
        }
        return 0;
    }

    /**
     * infoD = -E(pi*log2 pi)
     *
     * @param array
     * @param counts
     * @return
     */
    public double infoD(Object[] array, int[] counts) {
        double infoD = 0;
        for (int i = 0; i < counts.length; i++) {
            infoD += DecisionTreeUtil.info(counts[i], array.length);
        }
        return infoD;
    }

    /**
     * splitInfoaD = -E|Dj|/|D|*log2(|Dj|/|D|)
     *
     * @param array
     * @param counts
     * @return
     */
    public double splitInfoaD(Object[] array, int[] counts) {
        return infoD(array, counts);
    }

    /**
     * infoaD = E(|Dj| / |D|) * info(Dj)
     *
     * @param array
     * @param index
     * @param arrtibutes
     * @return
     */
    public double infoaD(Object[] array, int index, int nodeIndex,
                         String[] arrtibutes) {
        double sv_total = 0;
        for (int i = 0; i < arrtibutes.length; i++) {
            sv_total += infoDj(array, index, nodeIndex, arrtibutes[i],
                    array.length);
        }
        return sv_total;
    }

    /**
     * ((|Dj| / |D|) * Info(Dj))
     *
     * @param array
     * @param index
     * @param arrtibute
     * @param allTotal
     * @return double
     */
    public double infoDj(Object[] array, int index, int nodeIndex,
                         String arrtibute, int allTotal) {
        String[] arrtibutes = getArrtibutesArray(nodeIndex);
        int[] counts = new int[arrtibutes.length];
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }

        for (int i = 0; i < array.length; i++) {
            String[] strs =this.objectToString((Object[]) array[i]);
            if (strs[index].equals(arrtibute)) {
                for (int k = 0; k < arrtibutes.length; k++) {
                    if (strs[nodeIndex].equals(arrtibutes[k])) {
                        counts[k]++;
                    }
                }
            }
        }

        int total = 0;
        double infoDj = 0;
        for (int i = 0; i < counts.length; i++) {
            total += counts[i];
        }
        for (int i = 0; i < counts.length; i++) {
            infoDj += DecisionTreeUtil.info(counts[i], total);
        }
        return DecisionTreeUtil.getPi(total, allTotal) * infoDj;
    }

    /**
     * @param index
     * @return String[]
     */
    @SuppressWarnings("unchecked")
    public String[] getArrtibutesArray(int index) {
        TreeSet set = new TreeSet(new SequenceComparator());
        for (int i = 0; i < trainingArray.length; i++) {
            String[] strs = objectToString((Object[]) trainingArray[i]);
            set.add(strs[index]);
        }
        String[] result = new String[set.size()];
        return (String[]) set.toArray(result);
    }

    /**
     * @param index
     * @return String
     */
    public String getColumnHeaderNameByIndex(int index) {
        for (int i = 0; i < columnHeaderArray.length; i++) {
            if (i == index) {
                return columnHeaderArray[i];
            }
        }
        return null;
    }

    /**
     * @param array
     * @return String
     */
    public String getLeafNodeName(Object[] array, int nodeIndex) {
        if (array != null && array.length > 0) {
            String[] strs = (String[]) array[0];
            return strs[nodeIndex];
        }
        return null;
    }

    /**
     * @param name
     * @return int
     */
    public int getColumnHeaderIndexByName(String name) {
        for (int i = 0; i < columnHeaderArray.length; i++) {
            if (name.equals(columnHeaderArray[i])) {
                return i;
            }
        }
        return NOT_FOUND;
    }


    public String[] objectToString(Object[] objects){
        String[] strings = new String[objects.length];
        for (int i = 0;i<objects.length;i++){
            strings[i]= (String) objects[i];
        }
        return strings;
    }

    //map 排序
    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}  




