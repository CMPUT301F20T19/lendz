package cmput301.team19.lendz;

import java.util.ArrayList;

public class BorrowerInfoArray {

    private static BorrowerInfoArray instance;
    private ArrayList<BorrowerInfo> borrowerInfoArrayList = new ArrayList<>();


    public BorrowerInfoArray() {

    }
    public static BorrowerInfoArray getInstance(){
        if(instance == null){
            instance = new BorrowerInfoArray();
        }
        return instance;
    }

    //add city to arrayList
    public void add_BorrowerInfo(BorrowerInfo item){
        borrowerInfoArrayList.add(item);
    }
    //update city from arrayList
    public void update_BorrowerInfo(BorrowerInfo item,Integer i)
    {
        borrowerInfoArrayList.set(i, item);
    }
    //delete city from arrayList
    public void del_BorrowerList(Integer i){
        borrowerInfoArrayList.remove(i.intValue());
    }
    //get CityList
    public ArrayList<BorrowerInfo> getArray() {
        return borrowerInfoArrayList;
    }

    //
    public BorrowerInfo getBorrower(Integer i ){
        return borrowerInfoArrayList.get(i);
    }

}
