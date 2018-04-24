import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Grammer {

    private MyGrammer mygrammer;
    Grammer(String filename) throws IOException {
        mygrammer = new MyGrammer(filename);
    }

    private Items closureI(Items J){                //对ArrayList[A→α.Bβ,a]
        Items I = J.copyItems();
        while(true) {                               //循环直至I.itemSet大小不再改变
            int size = I.size;
            for (Item oneItem : I.itemSet) {        //遍历一个项目集合中的所有项目
                String firstA = oneItem.firstA;
                int dot = oneItem.dot;
                String[] second = oneItem.secondA;
                int secondLen = second.length;
                if (dot < secondLen) {              //如果B部分存在
                    ArrayList<String>b;
                    String itemB = second[dot];
                    String[][] next = mygrammer.getSecondA(itemB);      //获取B->n中的n集合
                    String[] belta = secondLen>dot+1 ? Arrays.copyOfRange(second,dot+1,secondLen) : null;   //获取belta字符串集合
                    //接下来设置b
                    if(belta == null){
                        b = new ArrayList<>(Arrays.asList(new String[oneItem.a.size()]));
                        Collections.copy(b,oneItem.a);
                    }
                    else {
                        b = getFirst(belta);
                    }
                    //添加项目
                    for(String[] B1:next){
                        if(!judgeIn(firstA,B1,I)){
                            I.add(new Item(firstA,B1,0));
                        }
                    }
                }
            }
            if(I.size<size){
                I.size = size;
            }else{
                break;
            }
        }
        return I;
    }
    private Items gGoTo(ArrayList<Item>I,String X){
        Items J = new Items(null);
        for(Item i : I){
            Item item = new Item(i.firstA,i.secondA,i.dot,i.a);
            J.add(item);
        }
        return closureI(J);
    }
    private MyItems myItemsConstruction(){
        MyItems C = new MyItems();
        Items firstSet = closureI(mygrammer.enterance);
        C.add(firstSet);
        while(true){
            int setSize = 0;
            for(Items i:C.itemCollection){
                for(Item item : i.itemSet){
                    Items gotoItem = gGoTo(i.itemSet,item.firstA);
                    if(!(gotoItem==null)){
                        C.add(gotoItem);
                    }
                }
            }
            if(C.itemCollection.size()==setSize){
                break;
            }
        }
        return C;
    }


    private boolean judgeIn(String B,String[]B1,Items I){
        int num = B1.length;
        for(Item i : I.itemSet){
            if(i.firstA.equals(B) && num==i.secondA.length){
                for(int j = 0;j<num;j++){
                    if(!B1[j].equals(i.secondA[j])){
                        return false;
                    }
                }
            }else{
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getFirst(String[]belta){
        ArrayList<String>ans = new ArrayList<>();
        String temp = belta[0];
        if(Character.isUpperCase(temp.charAt(0))) {
            ans.addAll(mygrammer.first(temp));
        }
        ans.add(temp);
        return ans;
    }
}

class Item{
    String firstA;
    String[] secondA;
    int dot;
    ArrayList<String>a;
    Item(String firstA,String[] secondA,int dot){
        this.firstA = firstA;
        this.secondA = secondA;
        this.dot = dot;
        this.a = new ArrayList<>();
    }
    Item(String firstA,String[] secondA,int dot,ArrayList<String>a){
        this.firstA = firstA;
        this.secondA = secondA;
        this.dot = dot;
        this.a = copyArray(a);
    }
    private ArrayList<String> copyArray(ArrayList<String> I){
        if(I==null)
            return null;
        ArrayList<String> ans = new ArrayList<>();
        ans.addAll(I);
        return ans;
    }
    boolean equals(Item i){
        return i.firstA.equals(firstA) && dot == i.dot && listEqual(i.secondA, secondA) && arrayEqual(a, i.a);
    }
    private boolean listEqual(String[]s1,String[]s2){
        int l1 = s1.length;
        int l2 = s2.length;
        if(l1!=l2)
            return false;
        for(int i = 0;i<l1;i++){
            if(!s1[i].equals(s2[i]))
                return false;
        }
        return true;
    }
    private boolean arrayEqual(ArrayList<String>a,ArrayList<String>b){
        int n1 = a.size();
        int n2 = b.size();
        if(n1!=n2)
            return false;
        for(int i=0;i<n1;i++){
            if(!a.get(i).equals(b.get(i)))
                return false;
        }
        return true;
    }
}
class Items{
    ArrayList<Item>itemSet;
    HashMap<String,Item>itemMap;
    int size;
    Items(ArrayList<Item>I){
        if(I==null){
            this.itemSet = new ArrayList<>();
            this.size = 0;
        }else {
            this.itemSet = copyArray(I);
            this.size = I.size();
            for(Item i : this.itemSet){
                this.itemMap.put(i.firstA,i);
            }
        }
    }
    boolean add(Item I){
        for(Item i : this.itemSet){
            if(i.equals(I)){
                return false;
            }
        }
        this.itemMap.put(I.firstA,I);
        this.itemSet.add(I);
        this.size++;
        return true;
    }
    boolean itemsEqual(Items I){
        if(!(I.size==this.size))
            return false;
        for(Item item:I.itemSet){
            if(!(item.equals(this.itemMap.get(item.firstA))))
                return false;
        }
        return true;
    }
    private ArrayList<Item> copyArray(ArrayList<Item> I){
        ArrayList<Item> ans = new ArrayList<>();
        for(Item item : I){
            ans.add(new Item(item.firstA,item.secondA,item.dot,item.a));
        }
        return ans;
    }
    Items copyItems(){
        return new Items(this.copyArray(this.itemSet));
    }
}
class MyItems{
    ArrayList<Items>itemCollection;
    int size =0;
    boolean add(Items items){
        if(!inMyItems(items)){
            this.itemCollection.add(items);
            this.size++;
            return true;
        }
        return false;
    }
    boolean inMyItems(Items myItems){
        for(Items items:this.itemCollection){
            if(items.itemsEqual(myItems))
                return true;
        }
        return false;
    }
}
class MyGrammerItem{
    String firstA;
    String[][] secondA;

    MyGrammerItem(String firstA,String[][] secondA){
        this.firstA = firstA;
        this.secondA = secondA;
    }
}
class MyGrammer{
    private ArrayList<MyGrammerItem>grammers = new ArrayList<>();
    private String[][] firstSet;
    private Map<String,String>grammerMap;
    private int Sum;
    Items enterance;

    MyGrammer(String filename) throws IOException {
        this.Sum = 0;
        this.grammerMap = new HashMap<>();
        this.readText(filename);
        this.firstSet = getFirstSet();
        String[][] enteranceItem = {{"SECTION"},};
        this.enterance = new Items(null);
        String[] entranceStr = {"SECTION"};
        this.enterance.add(new Item("S",entranceStr,0));
    }

    String[][] getSecondA(String firstA){
        for(MyGrammerItem item:grammers){
            if(firstA.equals(item.firstA))
                return item.secondA;
        }
        return null;
    }

    private String[][] getFirstSet(){
        String[][]temp = new String[Sum][];
        int x[] = new int[Sum];
        while(true){
            int num = 0;
            for(int i=Sum-1;i>=0;i--){
                ArrayList<String> tempS = new ArrayList<>();
                boolean Flag = true;
                if(x[i]==0){
                    for(String[] S : grammers.get(i).secondA){
                        String first = S[0];
                        if(!first.equals(grammers.get(i).firstA)) {
                            if (Character.isUpperCase(first.charAt(0))) {
                                int firstPlace = Integer.parseInt(grammerMap.get(first));
                                if (x[firstPlace] == 1) {
                                    tempS.addAll(Arrays.asList(temp[firstPlace]));
                                } else {
                                    Flag = false;
                                    break;
                                }
                            } else {
                                tempS.add(first);
                            }
                        }
                    }
                    if(Flag) {
                        temp[i]= removeRepeat(tempS.toArray(new String[tempS.size()]));
                        num++;
                        x[i]=1;
                    }
                }
            }
            if(num==grammers.size())
                break;
        }
        for(String[] X:temp){
            System.out.println(Arrays.toString(X));
        }
        return temp;
    }

    private void readText(String filename) throws IOException {
        File inputFile = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String temp;
        while((temp = reader.readLine())!=null){
            if(!(temp.startsWith("#") || temp.equals(""))){
                String[]temp1 = temp.split(" -> ");
                String[]temp2 = temp1[1].split(" \\| ");
                String[][]temp3 = new String[temp2.length][];
                for(int i = 0;i<temp2.length;i++){
                    temp3[i] = temp2[i].split(" ");
                }
                grammerMap.put(temp1[0],String.valueOf(Sum));
                Sum++;
                MyGrammerItem item = new MyGrammerItem(temp1[0],temp3);
                grammers.add(item);
            }
        }
        for(MyGrammerItem i : grammers){
            System.out.println(i.firstA+"->"+ Arrays.toString(i.secondA[0]));
        }
    }

    ArrayList<String> first(String temp){
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(firstSet[Integer.parseInt(grammerMap.get(temp))]));
        return list;
    }
    private String[] removeRepeat(String[]S){
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(S));
        return set.toArray(new String[set.size()]);
    }
}